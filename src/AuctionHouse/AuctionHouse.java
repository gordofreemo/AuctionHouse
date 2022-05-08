package AuctionHouse;

import util.Message;
import util.MessageEnums.Type;
import util.MessageEnums.Origin;
import util.Tuple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AuctionHouse {
    private ArrayList<Auction> auctionList;
    private ArrayList<Tuple<Socket, AgentProxy>> connectedAgents;
    private ItemNameGen nameGen;
    private BankProxy bank;
    private int houseID;

    public AuctionHouse() throws IOException {
        auctionList = new ArrayList<>();
        connectedAgents = new ArrayList<>();
        nameGen = new ItemNameGen("nouns.txt", "adjectives.txt");
        auctionList.add(new Auction(nameGen.getItemName()));
        auctionList.add(new Auction(nameGen.getItemName()));
        auctionList.add(new Auction(nameGen.getItemName()));
    }


    /**
     * @return - list of all the current auctions
     */
    public List<Auction> getAuctions() {
        return auctionList;
    }

    public boolean blockFunds(int agentID, int amount) {
        return bank.blockFunds(agentID, amount);
    }

    /**
     * Alert the previous bidder on the auction that he has been outbid
     * @param auctionID - auction in which an agent has just been outbid on
     */
    public void alertOutbid(int auctionID) {
        Message message = new Message(Origin.AUCTIONHOUSE, Type.BID_OUTBID, "");
        message.setBody(houseID + "\n" + auctionID);
        Auction auction = getAuction(auctionID);
        AgentProxy prev = auction.getPrevBidder();
        if(prev == null) return;
        bank.unblockFunds(prev.getAgentID(), auction.getPrevBid());
        prev.messageRequest(message);
    }

    public void endConnection(AgentProxy agent) {
        Tuple<Socket, AgentProxy> connection = null;
        for (Tuple<Socket, AgentProxy> connectedAgent : connectedAgents) {
            if (connectedAgent.y == agent) connection = connectedAgent;
        }
        if(connection == null) return;
        try {
            connection.x.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Closed connection w/ agent " + connection.y.getAgentID());
        connectedAgents.remove(connection);
    }

    /**
     * After an auction is over, remove it from the list of current auctions
     * and create a new one
     * @param auctionID - auction to remove
     */
    public void endAuction(int auctionID) {
        Auction end = getAuction(auctionID);
        auctionList.remove(end);
        auctionList.add(new Auction(nameGen.getItemName()));
    }


    /**
     * @param auctionID - auctionID to look for
     * @return - auction object w/ given auctionID
     */
    private Auction getAuction(int auctionID) {
        for(Auction auction : auctionList) {
            if(auction.getAuctionID() == auctionID) return auction;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        AuctionHouse auctionHouse = new AuctionHouse();
        int serverPort = 4001;
        String bankHostname = "localhost";
        int bankPort = 51362;

        auctionHouse.bank = new BankProxy(bankHostname, bankPort);
        auctionHouse.houseID = auctionHouse.bank.getAccountID();

        try (ServerSocket server = new ServerSocket(serverPort)) {
            while(true) {
                Socket socket = server.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                Message message = (Message)in.readObject();
                int agentID = Integer.parseInt(message.getBody());
                AgentProxy newProxy = new AgentProxy(out, auctionHouse, agentID);
                auctionHouse.connectedAgents.add(new Tuple<>(socket, newProxy));
                new Thread(new AgentListener(in, newProxy)).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

}
