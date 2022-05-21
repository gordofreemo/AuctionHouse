package AuctionHouse

import util.Message
import util.MessageEnums
import java.io.IOException
import java.io.ObjectOutputStream

/**
 * Encapsulates agent interaction w/ the auction house and communication
 * over the network.
 */
class AgentProxy(private val out: ObjectOutputStream, private val auctionHouse: AuctionHouse, val agentId: Int) {
    companion object {
        private val ORIGIN = MessageEnums.Origin.AUCTIONHOUSE
    }

    init {
        sendMessage(Message(ORIGIN, MessageEnums.Type.ACKNOWLEDGE_CONNECTION, "Agent, Hello"))
    }

    /**
     * Closes the socket connection associated with the agent
     */
    fun killConnection() {
        auctionHouse.endConnection(this)
    }

    /**
     * Handles the logic for an agent making a bid on an auction.
     * Sends either a bid failed or a bid success message to the agent
     * depending on what happened. If bid is successful, alerts the previous
     * bidder that he has been outbid.
     * @param amount - amount that the agent is bidding
     * @param auctionID - ID of the auction the agent is bidding on
     */
    fun makeBid(amount: Int, auctionId: Int) {
        val message = Message(ORIGIN, MessageEnums.Type.BID_FAILED, "bid failed")
        val auction = auctionHouse.auctions.find { it.auctionId == auctionId }

        if (auction == null || !auction.validBid(amount)) {
            sendMessage(message)
            return
        }

        val bankCheck = auctionHouse.blockFunds(agentId, amount)
        auction.makeBid(this, amount)

        if (bankCheck) {
            message.apply {
                body = "bid success"
                type = MessageEnums.Type.BID_SUCCESS
            }
        }

        auctionHouse.alertOutbid(auctionId)
        sendMessage(message)
    }

    /**
     * Sends a message to the agent who has just won a given auction.
     * Also ends the auction
     * @param auction - auction object representing auction that was just won
     */
    fun alertWin(auction: Auction) {
        val item = auction.item
        item.auctionID = auctionHouse.houseId
        auctionHouse.endAuction(auction.auctionId)
        sendMessage(Message(ORIGIN, MessageEnums.Type.BID_WIN, "YOU WON $item").apply { info = item })
    }

    /**
     * When receiving a get_items request, send a list of the items
     * to the agent as a message
     */
    fun sendItems() {
        val itemList = auctionHouse.auctions.map(Auction::item)
        val auctions = auctionHouse.auctions.joinToString(prefix = "\n", separator = "\n", postfix = "\n") { it.toString() }
        sendMessage(Message(ORIGIN, MessageEnums.Type.SEND_ITEMS, auctions).apply { info = itemList })
    }

    /**
     * Used to verify whether this agent is able to safely close the connection
     * with the auction house. Checks whether there are any active bids,
     * if there are it sends a message to the agent saying it cannot close the
     * socket, otherwise tells the agent that it can.
     */
    fun tryClose() {
        val message = Message(ORIGIN, null, "")
        val active = auctionHouse.auctions.any { it.bidder === this }
        sendMessage(
            message.apply {
                type = if (active) MessageEnums.Type.CANT_CLOSE else MessageEnums.Type.CAN_CLOSE
            }
        )
    }

    /**
     * Method that is used by outside classes to send a given message to the
     * agent. Might remove this in favor of message system encapsulation
     * @param message - message to send
     */
    fun messageRequest(message: Message) = sendMessage(message)

    /**
     * Sends a message across the network. It is its own separate method
     * so it can catch that pesky IO exception.
     * @param message - message to send over the network to given agent
     */
    private fun sendMessage(message: Message) {
        try {
            out.writeObject(message)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}