package AuctionHouse

import util.Message
import util.MessageEnums
import util.Tuple
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*


/**
 * Class representing the auction house server. Connects to the bank for a
 * start and then processes connections made by agents.
 */
class AuctionHouse {
    private val connectedAgents = mutableListOf<Tuple<Socket, AgentProxy>>()
    private val nameGen = ItemNameGen("nouns.txt", "adjectives.txt")
    private lateinit var bank: BankProxy
    private val name = generateName("houseNames.txt")

    /**
     * @return - bank account ID of the auction house
     */
    var houseId = 0
        private set

    /**
     * @return - list of all the current auctions
     */
    val auctions = mutableListOf<Auction>()


    init {
        auctions.apply {
            add(Auction(nameGen.itemName))
            add(Auction(nameGen.itemName))
            add(Auction(nameGen.itemName))
        }
    }

    /**
     * Attempts to block funds from the bank for a given agent. Calling this
     * will block the current thread until it gets a response back.
     * @param agentId - agent to block funds from
     * @param amount - amount to block
     * @return - returns true if block was successful, false if agent did
     * not have enough funds and block was not successful
     */
    fun blockFunds(agentId: Int, amount: Int) = bank.blockFunds(agentId, amount)

    /**
     * Alert the previous bidder on the auction that he has been outbid
     * @param auctionId - auction in which an agent has just been outbid on
     */
    fun alertOutbid(auctionId: Int) {
        val message = Message(
            MessageEnums.Origin.AUCTIONHOUSE,
            MessageEnums.Type.BID_OUTBID,
            """
                 $houseId
                 $auctionId
             """.trimIndent()
        )

        val auction = getAuction(auctionId)
        val prev = auction?.prevBidder ?: return

        bank.unblockFunds(prev.agentId, auction.prevBid)
        prev.messageRequest(message)
    }

    /**
     * Closes the socket associated with a given agent connection
     * @param agent - agent to close connection with
     */
    fun endConnection(agent: AgentProxy) {
        val connection = connectedAgents.find { it.y == agent } ?: return

        try {
            connection.x.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        println("Closed connection w/ agent ${connection.y.agentId}")
        connectedAgents.remove(connection)
    }

    /**
     * After an auction is over, remove it from the list of current auctions
     * and create a new one
     * @param auctionID - auction to remove
     */
    @Synchronized
    fun endAuction(auctionID: Int) {
        val end = getAuction(auctionID)

        auctions.apply {
            remove(end)
            add(Auction(nameGen.itemName))
        }
    }

    /**
     * @param auctionId - auctionID to look for
     * @return - auction object w/ given auctionID
     */
    private fun getAuction(auctionId: Int) = auctions.find { it.auctionId == auctionId }

    /**
     * @return - true if the program can safely exit, false otherwise
     */
    private fun canQuit() = auctions.none { it.bidder != null }

    /**
     * Makes a random name for the AuctionHouse
     * @param filename - file of list of names for auction houses
     * @return - String represent new auction house name
     * @throws IOException - if file is not found or something goes wrong in
     * reading the file
     */
    @Throws(IOException::class)
    private fun generateName(filename: String) = ClassLoader.getSystemResourceAsStream(filename)!!.bufferedReader().lines().toList().random()

    companion object {
        /**
         * @param args - command line argument format:
         * args[0] - bank hostname
         * args[1] - bank port number
         * args[2] - auction house server port
         * @throws IOException
         */
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val auctionHouse = AuctionHouse()
            val serverPort = args[2].toInt()
            val bankHostname = args[0]
            val bankPort = args[1].toInt()
            val bankProxy = BankProxy(auctionHouse.name, bankHostname, bankPort, serverPort)

            auctionHouse.bank = bankProxy
            auctionHouse.houseId = bankProxy.accountID

            val quit = Runnable {
                val sc = Scanner(System.`in`)
                while (sc.hasNext()) {
                    sc.nextLine()
                    if (auctionHouse.canQuit()) System.exit(0)
                    println("Can't exit, active bids")
                }
            }
            Thread(quit).start()

            try {
                ServerSocket(serverPort).use { server ->
                    while (true) {
                        val socket = server.accept()
                        val input = ObjectInputStream(socket.getInputStream())
                        val output = ObjectOutputStream(socket.getOutputStream())
                        val message = input.readObject() as Message
                        val agentId = message.body.toInt()
                        val newProxy = AgentProxy(output, auctionHouse, agentId)
                        auctionHouse.connectedAgents.add(Tuple(socket, newProxy))
                        Thread(AgentListener(input, newProxy)).start()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
