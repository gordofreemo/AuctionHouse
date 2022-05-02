package util;

import java.io.Serializable;

public class MessageEnums implements Serializable {
    // Holds all the possible origins
    public enum Origin {
        BANK,
        AUCTIONHOUSE,
        AGENT
    }

    // Holds all the possible types
    public enum Type {
        // General
        ESTABLISH_CONNECTION,
        CLOSE_CONNECTION,
        //
        BLOCKFUNDS,
        //
        BIDSUCCESS,
        BIDFAILED,
        //
        INIT,
        //
        MAKEBID,
        //
        GET_ITEMS
    }
}
