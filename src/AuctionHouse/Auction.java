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
    private int auctionID;

    public Auction(Item item, int auctionID, int minBid) {
        this.item = item;
        this.auctionID = auctionID;
        this.minBid = minBid;
        this.currBid = 0;
    }

    public int getAuctionID() {
        return auctionID;
    }

    public void makeBid() {}

    @Override
    public boolean equals(Object object) {
        if(object.getClass() != Auction.class) return false;
        Auction eq = (Auction) object;
        return eq.auctionID == this.auctionID;
    }
}
