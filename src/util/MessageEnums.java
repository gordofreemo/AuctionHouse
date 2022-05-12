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
    public enum Type implements Serializable {
        /*
          General
        */
        ESTABLISH_CONNECTION,
        CLOSE_CONNECTION,
        CHECK_CLOSE,
        CAN_CLOSE,
        CANT_CLOSE,
        ACKNOWLEDGE_CONNECTION,

        ACCOUNT_NOT_FOUND,

        /*
         AuctionHouse -> Bank
        */
        BLOCK_FUNDS,
        UNBLOCK_FUNDS,

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
        GET_ITEMS,

        // Agent -> Bank
        CHECK_FUNDS,
        GET_HOUSES,

        /*
            Bank -> AH
         */
        SEND_HOUSES
    }
}
