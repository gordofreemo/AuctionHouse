package AuctionHouse;

import util.Message;

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


    public List<Auction> getAuctions() {
        return auctionList;
    }

    public void endAuction(int auctionID) {
        Auction end = null;
        for(Auction auction : auctionList) {
            if(auction.getAuctionID() == auctionID) end = auction;
        }
        if(end == null) return;
        auctionList.remove(end);
        auctionList.add(new Auction(nameGen.getItemName()));
    }


    public static void main(String[] args) throws IOException {
        AuctionHouse auctionHouse = new AuctionHouse();
        int port = 4001;
        try (ServerSocket server = new ServerSocket(port)) {
            while(true) {
                Socket socket = server.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(new Message(null, "success", "win"));
                AgentProxy newProxy = new AgentProxy(out, auctionHouse);
                auctionHouse.connectedAgents.add(newProxy);
                new Thread(new AgentListener(in, newProxy)).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

}
