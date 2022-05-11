package Agent;

import AuctionHouse.AuctionHouse;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import AuctionHouse.*;
import util.Message;
import util.MessageEnums;

import java.io.IOException;
import static util.MessageEnums.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Agent {
    public String name;
    public long balance;
    public long avaliableBalance; // Balance - current bids
    public long pendingBids = 0;
    public int accountNumber = -1; // Set to minus one to make sure we wait until we actually get an account number
    public AgentToAuction proxy;
    public AgentToBank bank;
    public List<String> auctionConnects;
    public List<String> statusMessages = new ArrayList<>();


    public List<AuctionHouse> auctionHouses;

    /**
     * Build an agent
     * @param initBalance starting balance
     * @param name name of the agent (pulled from list?)
     */
    public Agent(long initBalance, String name) throws IOException, InterruptedException {
        this.name = name;
        this.balance = initBalance;
        this.avaliableBalance = initBalance;

        String bankAddress = "100.64.0.230"; //localHost
        int bankPort = 51362;

        String info = "Name:" + name + "\nBalance:" + balance;
        Message bankOutMsg = new Message(Origin.AGENT, Type.ESTABLISH_CONNECTION, info);
        bank = new AgentToBank(bankOutMsg, bankAddress, bankPort);

    }

    /**
     * @return a list of the available auction house connects available
     */
    public List<String> getAuctionConnects() {return bank.getAuctionConnects();}

    public int getAccountNumber(){ return bank.getId(); }

    public long getPendingBids(){return pendingBids;}

    /**
     * Connect to the given auction house
     * @param houseToConnectTo auction house address and port in bank format
     * @return the new socket
     */
    public AgentToAuction connectToAuctionHouse(String houseToConnectTo) throws IOException {
        String[] addressAndPort = houseToConnectTo.split(":");

        //AgentToAuction house = new AgentToAuction(addressAndPort[0], Integer.parseInt(addressAndPort[1]), getAccountNumber());
        AgentToAuction house = new AgentToAuction("100.64.0.230", 4001, getAccountNumber(), this, bank);
        return house;
    }

    /**
     * Get the list of items that the given auction house is selling
     * @param house the given auction house
     * @return the list of items
     */
    public List<Item> getItems(AgentToAuction house) throws IOException, InterruptedException {
        house.requestItems();
        Thread.sleep(500);
        return house.items();
    }

    /**
     * Make a bid on an item
     * @param house current auction house
     * @param amount the bid amount
     * @param id the item
     */
    public void makeBid(AgentToAuction house, int amount, int id) throws InterruptedException, IOException {
        house.makeBid(amount, id);
        Thread.sleep(500);
    }

    public void makeBid(int amount, int auctionID){
        // proxy.makeBid(amount, auctionID);
    }

}
