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
        /*
          General
        */
        ESTABLISH_CONNECTION,
        CLOSE_CONNECTION,
        ACKNOWLEDGE_CONNECTION,

        /*
         Bank -> Agent
        */
        BLOCKFUNDS,

        /*
          AuctionHouse -> Agent
        */
        BID_SUCCESS,
        BID_FAILED,
        BID_WIN,
        BID_OUTBID,
        SEND_ITEMS,

        /*
         Agent -> AuctionHouse
         */
        MAKE_BID,
        GET_ITEMS
    }
}
