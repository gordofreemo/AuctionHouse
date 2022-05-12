package Bank;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import AuctionHouse.Auction;
import AuctionHouse.AuctionHouse;
import util.MessageEnums.*;

public class BankState {
    private static BankState instance;
    private BlockingQueue<BankToAgent> AgentThreads = new LinkedBlockingDeque<>();
    private BlockingQueue<BankToAuctionHouse> AuctionHouseThreads = new LinkedBlockingDeque<>();
    private int id = 0;

    /**
     * Private constructor for singleton pattern
     */
    private BankState() {}

    /**
     * gets the singleton instance
     * @return singleton instance
     */
    public static synchronized BankState getInstance() {
        if (instance == null) {
            instance = new BankState();
        }
        return instance;
    }

    /**
     * Gets all the current BankToAgent threads
     * @return blocking queue list of BankToAgent threads
     */
    public BlockingQueue<BankToAgent> getAgentThreads() {
        return AgentThreads;
    }

    /**
     * Gets all the current BankToAuctionHouse threads
     * @return blocking queue list of BankToAuctionHouse threads
     */
    public BlockingQueue<BankToAuctionHouse> getAuctionHouseThreads() {
        return AuctionHouseThreads;
    }

    /**
     * Adds a new BankToAgent thread to the blocking queue
     * @param agentThread BankToAgent thread
     */
    public void addAgentThread(BankToAgent agentThread) {
        AgentThreads.add(agentThread);
    }

    /**
     * Adds a new BankToAuctionHouse thread to the blocking queue
     * @param auctionHouseThread BankToAuctionHouse thread
     */
    public void addAuctionHouseThread(BankToAuctionHouse auctionHouseThread) {
        AuctionHouseThreads.add(auctionHouseThread);
    }

    /**
     * removes an agent thread from the blocking queue by ID
     * @param id ID assigned form BankState upon init
     */
    public void removeAgentThread(int id) {
        for (var thread : AgentThreads) {
            if (thread.getId() == id) {
                AgentThreads.remove(thread);
                return;
            }
        }
    }

    /**
     * removes an auction house thread from the blocking queue by ID
     * @param id ID assigned form BankState upon init
     */
    public void removeAuctionHouseThread(int id) {
        for (var thread : AuctionHouseThreads) {
            if (thread.getId() == id) {
                AuctionHouseThreads.remove(thread);
                return;
            }
        }
    }

    /**
     * generated new id
     * @return Unique ID from either agent or auction house threads
     */
    public synchronized int getNewId() {
        return id++;
    }

    /**
     * gets a list of all auction houses using the auction house threads
     * get address method
     * @return list of auction house in format name:hostname:serverport
     */
    public List<String> getAuctionHouses() {
        List<String> auctionHouses = new ArrayList<>();
        for (BankToAuctionHouse auctionHouse : AuctionHouseThreads) {
            auctionHouses.add(auctionHouse.getAddress());
        }
        return auctionHouses;
    }

    /**
     * blocks funds from an agent
     * @param amount amount to block
     * @param id id of agent to block
     * @return returns if successful/failure/account not found
     */
    public Type blockFunds(int amount, int id) {
        for (var AgentThread : AgentThreads) {
            System.out.println("agent id: " + AgentThread.getId() + "\nsupplied id: " + id);
            if (AgentThread.getId() == id) {
                return AgentThread.blockFunds(amount);
            }
        }
        return Type.ACCOUNT_NOT_FOUND;
    }

    /**
     * releases funds from an agent
     * @param amount amount to release
     * @param id id of agent to release from
     */
    public void releaseFunds(int amount, int id) {
        for (var AgentThread : AgentThreads) {
            if (AgentThread.getId() == id) {
                AgentThread.releaseFunds(amount);
            }
        }
    }

    /**
     * transfers blocked funds from agent to auction house
     * @param agentId agent to transfer from
     * @param auctionHouseId auction house ot transfer into
     * @param amount amount to transfer
     */
    public void transferFunds(int agentId, int auctionHouseId, int amount) {
        for (var AuctionHouseThread : AuctionHouseThreads) {
            if (AuctionHouseThread.getId() == id) {
                AuctionHouseThread.addFunds(amount);
            }
        }
    }
}
