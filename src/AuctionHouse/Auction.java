package AuctionHouse;

/**
 * An individual auction for a given item.
 * Will probably run on its own thread because of the thirty-second countdown
 * it needs
 */

public class Auction {
    private Item item;
    private long minBid;
    private long currBid;
    private static int idCount = 0;
    private int auctionID;
    private AgentProxy bidder;

    public Auction(String description) {
        this.item = new Item(description, 1, auctionID);
        this.auctionID = idCount++;
        this.minBid = 0;
        this.currBid = 0;
    }

    public Item getItem() {
        return item;
    }

    public int getAuctionID() {
        return auctionID;
    }

    // at this point, bid should be verified
    public synchronized void makeBid(AgentProxy agent, int amount) {
        this.currBid = amount;
        bidder = agent;
        new Thread(makeCountdown()).start();
    }

    // make the countdown thread to inform bid winner
    private Runnable makeCountdown() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    return;
                }
                endAuction();
            }
        };
    }

    private void endAuction() {
        bidder.alertWin(this);
    };

    @Override
    public boolean equals(Object object) {
        if(object.getClass() != Auction.class) return false;
        Auction eq = (Auction) object;
        return eq.auctionID == this.auctionID;
    }
}
