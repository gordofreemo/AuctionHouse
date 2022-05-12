package AuctionHouse;

import util.Message;
import util.MessageEnums.Type;
import util.MessageEnums.Origin;
import util.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class representing the auction house server. Connects to the bank for a
 * start and then processes connections made by agents.
 */

public class AuctionHouse {
    private ArrayList<Auction> auctionList;
    private ArrayList<Tuple<Socket, AgentProxy>> connectedAgents;
    private ItemNameGen nameGen;
    private BankProxy bank;
    private int houseID;
    private String name;

    public AuctionHouse() throws IOException {
        auctionList = new ArrayList<>();
        connectedAgents = new ArrayList<>();
        nameGen = new ItemNameGen("nouns.txt", "adjectives.txt");
        name = generateName("houseNames.txt");
        auctionList.add(new Auction(nameGen.getItemName()));
        auctionList.add(new Auction(nameGen.getItemName()));
        auctionList.add(new Auction(nameGen.getItemName()));
    }

    public int getHouseID() {
        return houseID;
    }

    /**
     * @return - list of all the current auctions
     */
    public List<Auction> getAuctions() {
        return auctionList;
    }

    /**
     * Attempts to block funds from the bank for a given agent. Calling this
     * will block the current thread until it gets a response back.
     * @param agentID - agent to block funds from
     * @param amount - amount to block
     * @return - returns true if block was successful, false if agent did
     * not have enough funds and block was not successful
     */
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

    /**
     * Closes the socket associated with a given agent connection
     * @param agent - agent to close connection with
     */
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
    public synchronized void endAuction(int auctionID) {
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

    private boolean canQuit() {
        for(var auction : auctionList) {
            if(auction.getBidder() != null) return false;
        }
        return true;
    }

    private String generateName(String filename) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        InputStream stream = ClassLoader.getSystemResourceAsStream(filename);
        Scanner sc = new Scanner(stream);
        while(sc.hasNext()) names.add(sc.nextLine());
        stream.close();
        return names.get((int)(Math.random()*names.size()));
    }

    /**
     * @param args - command line argument format:
     *             args[0] - bank hostname
     *             args[1] - bank port number
     *             args[2] - auction house server port
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        AuctionHouse auctionHouse = new AuctionHouse();
        int serverPort = Integer.parseInt(args[2]);
        String bankHostname = args[0];
        int bankPort = Integer.parseInt(args[1]);

        auctionHouse.bank = new BankProxy(auctionHouse.name, bankHostname, bankPort, serverPort);
        auctionHouse.houseID = auctionHouse.bank.getAccountID();

        Runnable quit = () -> {
            Scanner sc = new Scanner(System.in);
            while(sc.hasNext()) {
                sc.nextLine();
                if(auctionHouse.canQuit()) System.exit(0);
                System.out.println("Can't exit, active bids");
            }
        };
        new Thread(quit).start();

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
