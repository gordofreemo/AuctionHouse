package Bank;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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

    public int getNewId() {
        return id++;
    }

    public List<String> getAuctionHouses() {
        List<String> auctionHouses = new ArrayList<>();
        for (BankToAuctionHouse auctionHouse : AuctionHouseThreads) {
            auctionHouses.add(auctionHouse.getAddress());
        }
        return auctionHouses;
    }
}