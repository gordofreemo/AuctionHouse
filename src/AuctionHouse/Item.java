package AuctionHouse;

import java.io.Serializable;

/**
 * Item that is being sold by the Auction House.
 * Might add more state in the future, such as each item having an image
 * associated.
 */

public class Item implements Serializable {
    public long houseID;
    public long itemID;
    public String description;

    public Item(String description, long houseID, long itemID) {
        this.description = description;
        this.houseID     = houseID;
        this.itemID      = itemID;
    }

    @Override
    public String toString() {
        return description + ". HouseID: " + houseID + ". ItemID: " + itemID;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != Item.class) return false;
        Item item = (Item) obj;
        boolean b1 = item.description == this.description;
        boolean b2 = item.itemID == this.itemID;
        boolean b3 = item.houseID == this.houseID;
        return b1 && b2 && b3;
    }
}
