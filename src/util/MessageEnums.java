package util;

public class MessageEnums {
    // Holds all the possible origins
    public enum Origin {
        BANK,
        AUCTIONHOUSE,
        AGENT
    }

    // Holds all the types (methods) that the Bank can send to the AuctionHouse
    public enum BankToAuctionHouse {

    }

    // Holds all the types (methods) that the Bank can send to the Agent
    public enum BankToAgent {

    }

    // Holds all the types (methods) that the AuctionHouse can send to the Bank
    public enum AuctionHouseToBank {
        BLOCKFUNDS
    }

    // Holds all the types (methods) that the AuctionHouse can send to the Agent
    public enum AuctionHouseToAgent {
        BIDSUCCESS,
        BIDFAILED
    }

    // Holds all the types (methods) that the Agent can send to the Bank
    public enum AgentToBank {
        INIT
    }

    // Holds all the types (methods) that the Agent can send to the AuctionHouse
    public enum AgentToAuctionHouse {
        MAKEBID,
        GET_ITEMS
    }
}
