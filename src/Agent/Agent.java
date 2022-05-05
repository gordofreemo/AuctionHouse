package Agent;

import AuctionHouse.AuctionHouse;
import java.util.List;
import AuctionHouse.*;
import util.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Agent {
    public String name;
    public long balance;
    public long avaliableBalance; // Balance - current bids
    public String accountNumber;
    private AgentProxy proxy;

    public List<AuctionHouse> auctionHouses;

    /**
     * Build an agent
     * @param initBalance starting balance
     * @param name name of the agent (pulled from list?)
     */
    public Agent(long initBalance, String name){
        this.name = name;
        this.balance = initBalance;
        this.avaliableBalance = initBalance;

        // Send info to bank, receive account number
    }

    public void makeBid(int amount, int auctionID){
        proxy.makeBid(amount, auctionID);
    }

}
