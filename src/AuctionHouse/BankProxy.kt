package AuctionHouse

import util.Message
import util.MessageEnums
import util.Tuple
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

/**
 * This class encapsulates the communication between the bank and the
 * auction house.
 */
class BankProxy(shopName: String, hostname: String?, port: Int, hostPort: Int) {
    private val socket = Socket(hostname, port)
    private val output = ObjectOutputStream(socket.getOutputStream())
    private val listener = BankListener(ObjectInputStream(socket.getInputStream()))
    private val origin = MessageEnums.Origin.AUCTIONHOUSE
    private val awaits = mutableMapOf<Int, Tuple<Thread, AsyncHelper>>()

    /**
     * @return - the bank account ID of the auction house
     */
    var accountID = 0
        private set



    init {
        Thread(listener).start()
        val msg = """
            $shopName
            $hostPort
        """.trimIndent()
        output.writeObject(Message(origin, MessageEnums.Type.ESTABLISH_CONNECTION, msg))
    }

    /**
     * Sends a message to block funds for a given agent. Any thread calling
     * this method will block until a response is received
     * @param agentId - agent account to block funds for
     * @param amount - amount to block
     * @return - true if block was successful, false if agent did not have
     * enough funds
     */
    fun blockFunds(agentId: Int, amount: Int): Boolean {
        // Sets up logic for awaiting response from bank
        val await = AsyncHelper()
        val thread = Thread(await)
        awaits[agentId] = Tuple(thread, await)
        thread.start()
        sendMessage(
            Message(
                origin,
                MessageEnums.Type.MAKE_BID,
                """
                $amount
                $agentId
            """.trimIndent()
            )
        )

        try {
            thread.join()
        } catch (_: InterruptedException) { }
        awaits.remove(agentId)

        return await.state
    }

    /**
     * Sends a message to the bank to unblock funds that were previously blocked
     * @param agentID - agent to unblock funds for
     * @param amount - amount to unblock
     */
    fun unblockFunds(agentID: Int, amount: Int) = sendMessage(
        Message(
            origin,
            MessageEnums.Type.UNBLOCK_FUNDS,
            """
                $amount
                $agentID
            """.trimIndent()
        )
    )

    /**
     * @param message - message to send to bank
     */
    private fun sendMessage(message: Message) {
        try {
            output.writeObject(message)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Runnable object to parse messages coming in from bank
     */
    private inner class BankListener(private val input: ObjectInputStream) : Runnable {
        override fun run() {
            while (true) {
                try {
                    val msg = input.readObject() as Message
                    println(msg)

                    when (msg.type) {
                        MessageEnums.Type.BID_FAILED -> {
                            val id = msg.body.toInt()
                            val tuple = awaits[id]!!
                            tuple.y.state = false
                            tuple.x.interrupt()
                        }
                        MessageEnums.Type.BID_SUCCESS -> {
                            val id = msg.body.toInt()
                            val tuple = awaits[id]!!
                            tuple.y.state = true
                            tuple.x.interrupt()
                        }
                        MessageEnums.Type.ACKNOWLEDGE_CONNECTION -> {
                            accountID = msg.body.toInt()
                        }
                        else -> { }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Used to assist in blocking until a response is gotten back.
     * The thread that needs to block starts up a new AsyncHelper, joins it,
     * and when a message is received from the bank, the appropriate async
     * helper is interrupted so that the original thread can unblock.
     */
    private inner class AsyncHelper : Runnable {
        var state = false
        override fun run() {
            try {
                Thread.sleep(10000)
            } catch (_: InterruptedException) { }
        }
    }
}
