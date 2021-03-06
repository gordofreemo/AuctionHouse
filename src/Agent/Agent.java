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
    public AgentToBank bank;
    public List<String> statusMessages = new ArrayList<>();
    public boolean redrawTabsFlag = false;
    private List<String> connectedHouses = new ArrayList<>();


    /**
     * Build an agent
     * @param initBalance starting balance
     * @param name name of the agent (pulled from list?)
     */
    public Agent(long initBalance, String name, String bankAddress, int bankPort) throws IOException, InterruptedException {
        this.name = name;
        this.balance = initBalance;
        this.avaliableBalance = initBalance;

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
     * Check if the agent is connected to the given house string
     * @param auctionHouse
     * @return
     */
    public boolean isConnectedTo(String auctionHouse){
        if(connectedHouses.contains(auctionHouse)) return true;
        return false;
    }

    /**
     * Connect to the given auction house
     * @param houseToConnectTo auction house address and port in bank format
     * @return the new socket
     */
    public AgentToAuction connectToAuctionHouse(String houseToConnectTo) throws IOException {
        String[] addressAndPort = houseToConnectTo.split(":");

        AgentToAuction house = new AgentToAuction(addressAndPort[0], addressAndPort[1], Integer.parseInt(addressAndPort[2]), getAccountNumber(), this, bank);
        connectedHouses.add(houseToConnectTo);
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
