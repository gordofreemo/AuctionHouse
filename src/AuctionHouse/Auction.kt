package AuctionHouse

/**
 * An individual auction for a given item.
 * Will probably run on its own thread because of the thirty-second countdown
 * it needs
 */
class Auction(description: String) {
    val item = Item(description, idCount)
    private var currBid = 0

    /**
     * @return - the agent who bid previously on this auction
     */
    var prevBid = 0
        private set

    /**
     * @return - id of the current auction
     */
    val auctionId = idCount++

    /**
     * @return - the agent with the current highest bid on this auction
     */
    var bidder: AgentProxy? = null
        private set

    /**
     * @return - gets the agent that had bid previously, used for outbid
     * notification. Might remove this and alert the bidder in this class.
     */
    var prevBidder: AgentProxy? = null
        private set

    private val counterRun = makeCountdown()
    private var countdown = Thread(counterRun)

    /**
     * When calling this method, the bid should be verified through the use
     * of talking with the bank and by calling the validBid
     * @param agent - agent which is making the bid
     * @param amount - amount which the agent is bidding
     */
    @Synchronized
    fun makeBid(agent: AgentProxy?, amount: Int) {
        countdown.interrupt()
        prevBid = currBid
        currBid = amount
        item.currentBid = amount
        prevBidder = bidder
        bidder = agent
        countdown = Thread(counterRun).apply { start() }
    }

    /**
     * @param amount - amount that is requesting to be bid
     * @return - true if bid is over minimum bid, false otherwise
     */
    fun validBid(amount: Int) = amount > currBid

    /**
     * @return - a new runnable object that counts for 30 seconds, and when it
     * is finished it ends the current auction
     */
    private fun makeCountdown(): Runnable {
        return Runnable {
            try {
                Thread.sleep(10000)
            } catch (e: InterruptedException) {
                return@Runnable
            }

            endAuction()
        }
    }

    /**
     * Alert the current highest bidder that he has won the auction
     */
    private fun endAuction() = bidder!!.alertWin(this)

    override fun toString() = "ID: $auctionId selling $item"

    override fun equals(other: Any?): Boolean {
        if (other?.javaClass != Auction::class.java) return false
        return (other as Auction).auctionId == auctionId
    }

    companion object {
        private var idCount = 0
    }
}
