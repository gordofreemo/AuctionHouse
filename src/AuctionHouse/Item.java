package AuctionHouse;

import java.io.Serializable;

/**
 * Item that is being sold by the Auction House.
 * Might add more state in the future, such as each item having an image
 * associated.
 */

public class Item implements Serializable {
    public int auctionID;
    public String description;
    public int currentBid;

    public Item(String description, int auctionID) {
        this.description = description;
        this.auctionID = auctionID;
        currentBid = 0;
    }

    @Override
    public String toString() {
        return description + ". AuctionID: " + auctionID;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != Item.class) return false;
        Item item = (Item) obj;
        boolean b1 = item.description == this.description;
        boolean b2 = item.auctionID == this.auctionID;
        return b1 && b2;
    }
}
