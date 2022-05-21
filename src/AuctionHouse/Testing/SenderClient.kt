package AuctionHouse.Testing

import util.Message
import util.MessageEnums
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.*


class SenderClient {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val socket = Socket("127.0.0.1", 4001)
            val output = ObjectOutputStream(socket.getOutputStream())
            val input = ObjectInputStream(socket.getInputStream())

            val scanner = Scanner(System.`in`)

            while (true) {
                val message = Message(MessageEnums.Origin.AGENT, null, "")

                println("Please enter the message type: ")
                val type = scanner.nextLine()

                println("Please enter the message body: ")
                var body = scanner.nextLine()

                when (type) {
                    "bid" -> {
                        message.type = MessageEnums.Type.MAKE_BID
                        println("Please enter message body line 2 : ")
                        body += "\n${scanner.nextLine()}"
                    }
                    "get" -> message.type = MessageEnums.Type.GET_ITEMS
                    else -> {
                        if (type != "read") {
                            message.body = body
                            output.writeObject(message)
                        }
                    }
                }

                val read = input.readObject() as Message
                println(read)
            }
        }
    }
}