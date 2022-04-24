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

    public Auction(Item item) {
        this.item = item;
    }
}
