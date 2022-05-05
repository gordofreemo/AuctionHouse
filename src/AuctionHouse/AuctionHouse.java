package AuctionHouse;

import util.Message;
import util.MessageEnums.Type;
import util.MessageEnums.Origin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AuctionHouse {
    private ArrayList<Auction> auctionList;
    private ArrayList<AgentProxy> connectedAgents;
    private ItemNameGen nameGen;

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

    /**
     * Alert the previous bidder on the auction that he has been outbid
     * @param auctionID - auction in which an agent has just been outbid on
     */
    public void alertOutbid(int auctionID) {
        Message message = new Message(Origin.AUCTIONHOUSE, Type.BID_OUTBID, "\nOutbid in auction w/ id" + auctionID);
        AgentProxy prev = getAuction(auctionID).getPrevBidder();
        if(prev == null) return;
        prev.messageRequest(message);
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
        Auction get = null;
        for(Auction auction : auctionList) {
            if(auction.getAuctionID() == auctionID) get = auction;
        }
        return get;
    }

    public static void main(String[] args) throws IOException {
        AuctionHouse auctionHouse = new AuctionHouse();
        int port = 4001;
        try (ServerSocket server = new ServerSocket(port)) {
            while(true) {
                Socket socket = server.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                in.readObject();
                AgentProxy newProxy = new AgentProxy(out, auctionHouse);
                auctionHouse.connectedAgents.add(newProxy);
                new Thread(new AgentListener(in, newProxy)).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

}
