package AuctionHouse

import util.Message
import util.MessageEnums
import java.io.IOException
import java.io.ObjectInputStream
import java.net.SocketException
import java.util.*

/**
 * Class responsible for receiving messages from Agent
 */
class AgentListener(private val input: ObjectInputStream, private val proxy: AgentProxy) : Runnable {
    /**
     * When receiving the bid message, parse the message for the amount
     * and the auction on which the agent wants to bid on
     * @param message - bid message to parse
     */
    private fun parseBid(message: Message) {
        val scanner = Scanner(message.body)
        val amount = scanner.nextLine().toInt()
        val auctionId = scanner.nextLine().toInt()

        proxy.run {
            makeBid(amount, auctionId)
            sendItems()
        }
    }

    /**
     * Listen for messages from agent socket and communicate with proxy as
     * necessary
     */
    override fun run() {
        try {
            while (true) {
                val message = input.readObject() as Message
                println(message)

                when (message.type) {
                    MessageEnums.Type.MAKE_BID -> parseBid(message)
                    MessageEnums.Type.GET_ITEMS -> proxy.sendItems()
                    MessageEnums.Type.CAN_CLOSE -> proxy.tryClose()
                    else -> error("Invalid message type ${message.type}")
                }
            }
        } catch (e: SocketException) {
            proxy.killConnection()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}