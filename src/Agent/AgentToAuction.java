package Agent;

import AuctionHouse.Item;
import util.Message;
import util.MessageEnums.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
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
    private String name;
    private boolean bidAccepted = false;
    private HashMap<Integer, Integer> itemValues = new HashMap<>();

    AgentToAuction(String name, String address, int port, int id, Agent agent, AgentToBank bank) throws IOException {
        this.bank = bank;
        this.name = name;
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

    public String getHouseName(){ return name; }

    public void makeBid(int amount, int id) throws IOException, InterruptedException {
        String info = amount + "\n" + id;
        Message bid = new Message(Origin.AGENT, Type.MAKE_BID, info);
        out.writeObject(bid);
        Thread.sleep(100);
        if(bidAccepted) {
            agent.statusMessages.add("You bid $" + amount + " on Item " + id);
            itemValues.put(id, amount);
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
     * Get the current bid on the given item
     * @param id
     * @return
     */
    public int getItemBid(int id){
        return itemValues.get(id);
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
                            // Get the items as a list, pull the current bid from the body of the message
                            System.out.println(inMsg.getInfo());
                            String[] bodyNewLineSplit = inMsg.getBody().split("\n");
                            for(int i = 1; i < bodyNewLineSplit.length; i++){
                                String line = bodyNewLineSplit[i];
                                String[] lineSplit = line.split(" ");
                                itemValues.put(Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[lineSplit.length-1]));
                            }
                            items = (List<Item>) inMsg.getInfo();
                        }
                        case BID_SUCCESS -> {
                            bidAccepted = true;
                        }
                        case BID_WIN -> {
                            String[] bodySplit = inMsg.getBody().split("AuctionID:");
                            agent.statusMessages.add(bodySplit[0]);
                            agent.redrawTabsFlag = true;
                        }
                        case BID_OUTBID -> {
                            agent.redrawTabsFlag = true;
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
