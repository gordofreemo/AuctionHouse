package Agent;

import AuctionHouse.Item;
import util.Message;
import util.MessageEnums.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Handles all communication going from the agent to the auction house
 */

public class AgentToAuction {
    private ObjectOutputStream out;
    private AuctionListener listener;
    private List<Item> items;
    private Agent agent;
    private AgentToBank bank;
    private boolean bidAccepted = false;

    AgentToAuction(String address, int port, int id, Agent agent, AgentToBank bank) throws IOException {
        this.bank = bank;
        this.agent = agent;
        Socket houseSocket = new Socket(address, port);
        out = new ObjectOutputStream(houseSocket.getOutputStream());
        listener = new AuctionListener(new ObjectInputStream(houseSocket.getInputStream()));
        new Thread(listener).start();

        System.out.println("Sending init message to auction house");
        Message outMsg = new Message(Origin.AGENT, Type.ESTABLISH_CONNECTION, Integer.toString(id));
        out.writeObject(outMsg);
    }

    /**
     * Ask the auction house to send its list of items
     * @throws IOException
     */
    public void requestItems() throws IOException {
        System.out.println("Ask house for list of items");
        Message requestItem = new Message(Origin.AGENT, Type.GET_ITEMS, "");
        out.writeObject(requestItem);
    }

    public void makeBid(int amount, int id) throws IOException, InterruptedException {
        String info = amount + "\n" + id;
        Message bid = new Message(Origin.AGENT, Type.MAKE_BID, info);
        out.writeObject(bid);
        Thread.sleep(100);
        if(bidAccepted) {
            agent.statusMessages.add("You bid $" + amount + " on Item " + id);
            agent.pendingBids += amount;
            agent.avaliableBalance = agent.balance - agent.pendingBids;
        }

        bidAccepted = false; // Always revert the state
    }

    /**
     * @return the list of items being sold by the auction house
     */
    public List<Item> items(){
        return items;
    }

    /**
     * Listen to messages from the auction house indefinitely
     */
    private class AuctionListener implements Runnable {
        private ObjectInputStream in;

        public AuctionListener(ObjectInputStream in){
            this.in = in;
        }

        @Override
        public void run() {
            while(true){
                try {
                    Message inMsg = (Message) in.readObject();
                    System.out.println(inMsg);

                    switch (inMsg.getType()){
                        case ESTABLISH_CONNECTION -> {

                        }
                        case SEND_ITEMS -> {
                            System.out.println(inMsg.getInfo());
                            items = (List<Item>) inMsg.getInfo();
                        }
                        case BID_SUCCESS -> {
                            bidAccepted = true;
                        }
                        case BID_WIN -> {
                            agent.statusMessages.add("You won an item!");
                        }
                        case BID_OUTBID -> {
                            String[] info = inMsg.getBody().split("\n");
                            int auctionHouseID = Integer.parseInt(info[0]);
                            int itemID = Integer.parseInt(info[1]);
                            agent.statusMessages.add("You have been out bid on Item " + itemID + "!");
                        }
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
