package AuctionHouse;

/**
 * Item that is being sold by the Auction House.
 * Might add more state in the future, such as each item having an image
 * associated.
 */

public class Item {
    public long houseID;
    public long itemID;
    public String description;

    public Item(String description, long houseID, long itemID) {
        this.description = description;
        this.houseID     = houseID;
        this.itemID      = itemID;
    }

}
