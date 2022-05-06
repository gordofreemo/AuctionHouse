package AuctionHouse;

import java.io.Serializable;

/**
 * Item that is being sold by the Auction House.
 * Might add more state in the future, such as each item having an image
 * associated.
 */

public class Item implements Serializable {
    public long houseID;
    public String description;

    public Item(String description, long houseID) {
        this.description = description;
        this.houseID     = houseID;
    }

    @Override
    public String toString() {
        return description + ". HouseID: " + houseID;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != Item.class) return false;
        Item item = (Item) obj;
        boolean b1 = item.description == this.description;
        boolean b2 = item.houseID == this.houseID;
        return b1 && b2;
    }
}
