package AuctionHouse;

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
        auctionList = new ArrayList<Auction>();
        connectedAgents = new ArrayList<>();
        nameGen = new ItemNameGen("nouns.txt", "adjectives.txt");
        auctionList.add(new Auction(nameGen.getItemName()));
        auctionList.add(new Auction(nameGen.getItemName()));
        auctionList.add(new Auction(nameGen.getItemName()));
    }


    public List<Auction> getAuctions() {
        return auctionList;
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        AuctionHouse auctionHouse = new AuctionHouse();
        int port = 4001;
        try (ServerSocket server = new ServerSocket(port)) {
            while(true) {
                Socket socket = server.accept();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                AgentProxy newProxy = new AgentProxy(out, auctionHouse);
                auctionHouse.connectedAgents.add(newProxy);
                new Thread(new AgentListener(in, newProxy)).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

}
