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

    private BankState() {}

    public static synchronized BankState getInstance() {
        if (instance == null) {
            instance = new BankState();
        }
        return instance;
    }

    public BlockingQueue<BankToAgent> getAgentThreads() {
        return AgentThreads;
    }

    public BlockingQueue<BankToAuctionHouse> getAuctionHouseThreads() {
        return AuctionHouseThreads;
    }

    public void addAgentThread(BankToAgent agentThread) {
        AgentThreads.add(agentThread);
    }

    public void addAuctionHouseThread(BankToAuctionHouse auctionHouseThread) {
        AuctionHouseThreads.add(auctionHouseThread);
    }

    public void removeAgentThread(int id) {
        for (var thread : AgentThreads) {
            if (thread.getId() == id) {
                AgentThreads.remove(thread);
                return;
            }
        }
    }

    public void removeAuctionHouseThread(int id) {
        for (var thread : AuctionHouseThreads) {
            if (thread.getId() == id) {
                AuctionHouseThreads.remove(thread);
                return;
            }
        }
    }

    public synchronized int getNewId() {
        return id++;
    }

    public List<String> getAuctionHouses() {
        List<String> auctionHouses = new ArrayList<>();
        for (BankToAuctionHouse auctionHouse : AuctionHouseThreads) {
            auctionHouses.add(auctionHouse.getAddress());
        }
        return auctionHouses;
    }

    public Type blockFunds(int amount, int id) {
        for (var AgentThread : AgentThreads) {
            System.out.println("agent id: " + AgentThread.getId() + "\nsupplied id: " + id);
            if (AgentThread.getId() == id) {
                return AgentThread.blockFunds(amount);
            }
        }
        return Type.ACCOUNT_NOT_FOUND;
    }

    public void releaseFunds(int amount, int id) {
        for (var AgentThread : AgentThreads) {
            if (AgentThread.getId() == id) {
                AgentThread.releaseFunds(amount, id);
            }
        }
    }

    public void transferFunds(int agentId, int auctionHouseId, int amount) {
        for (var AuctionHouseThread : AuctionHouseThreads) {
            if (AuctionHouseThread.getId() == id) {
                AuctionHouseThread.addFunds(amount);
            }
        }
    }
}
