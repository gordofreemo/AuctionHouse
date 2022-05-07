package AuctionHouse;

/**
 * An individual auction for a given item.
 * Will probably run on its own thread because of the thirty-second countdown
 * it needs
 */

public class Auction {
    private Item item;
    private int currBid;
    private int prevBid;
    private static int idCount = 0;
    private int auctionID;
    private AgentProxy bidder;
    private AgentProxy prevBidder;
    private Runnable counterRun;
    private Thread countdown;

    public Auction(String description) {
        auctionID = idCount;
        item = new Item(description, idCount);
        idCount++;
        currBid = 0;
        prevBid = 0;
        counterRun = makeCountdown();
        countdown = new Thread(counterRun);
    }

    /**
     * @return - item being sold in this auction
     */
    public Item getItem() {
        return item;
    }

    /**
     * @return - id of the current auction
     */
    public int getAuctionID() {
        return auctionID;
    }

    /**
     * @return - gets the agent that had bid previously, used for outbid
     * notification. Might remove this and alert the bidder in this class.
     */
    public AgentProxy getPrevBidder() {
        return prevBidder;
    }

    public int getPrevBid() {
        return prevBid;
    }

    /**
     * When calling this method, the bid should be verified through the use
     * of talking with the bank and by calling the validBid
     * @param agent
     * @param amount
     */
    public synchronized void makeBid(AgentProxy agent, int amount) {
        countdown.interrupt();
        prevBid = currBid;
        currBid = amount;
        item.currentBid = amount;
        prevBidder = bidder;
        bidder = agent;
        countdown = new Thread(counterRun);
        countdown.start();
    }

    public boolean validBid(int amount) {
        return amount > currBid;
    }

    // make the countdown thread to inform bid winner
    private Runnable makeCountdown() {
        return () -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return;
            }
            endAuction();
        };
    }

    private void endAuction() {
        bidder.alertWin(this);
    }

    @Override
    public String toString() {
        return "ID: " + auctionID + " selling " + item.toString();
    }

    @Override
    public boolean equals(Object object) {
        if(object.getClass() != Auction.class) return false;
        Auction eq = (Auction) object;
        return eq.auctionID == this.auctionID;
    }
}
