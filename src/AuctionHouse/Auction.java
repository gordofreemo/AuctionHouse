package AuctionHouse;

/**
 * An individual auction for a given item.
 * Will probably run on its own thread because of the thirty-second countdown
 * it needs
 */

public class Auction {
    private Item item;
    private long currBid;
    private final double bidScale = 1.25; // for now : must bid at least (1.25 * currBid) to count
    private static int idCount = 0;
    private int auctionID;
    private AgentProxy bidder;
    private Runnable counterRun;
    private Thread countdown;

    public Auction(String description) {
        this.item = new Item(description, 1, auctionID);
        this.auctionID = idCount++;
        this.currBid = 0;
        counterRun = makeCountdown();
        countdown = new Thread(counterRun);
    }

    public Item getItem() {
        return item;
    }

    public int getAuctionID() {
        return auctionID;
    }

    // at this point, bid should be verified
    public synchronized void makeBid(AgentProxy agent, int amount) {
        countdown.interrupt();
        this.currBid = amount;
        bidder = agent;
        countdown = new Thread(counterRun);
        countdown.start();
    }

    public boolean validBid(int amount) {
        if(amount < currBid * bidScale) return false;
        return true;
    }

    // make the countdown thread to inform bid winner
    private Runnable makeCountdown() {
        return () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return;
            }
            endAuction();
        };
    }

    private void endAuction() {
        System.out.println("YO HE WON");
        bidder.alertWin(this);
    };

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
