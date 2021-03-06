package Agent;

import AuctionHouse.Item;
import util.Message;
import util.MessageEnums.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
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
    private HashMap<Integer, Integer> myBids = new HashMap<>(); // Map the agent's CURRENT bid on an object to that objects item number
    public boolean runThread = true;

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
        // System.out.println("Ask house for list of items");
        Message requestItem = new Message(Origin.AGENT, Type.GET_ITEMS, "");
        out.writeObject(requestItem);
    }

    /**
     * Make a bid on the given item
     * @param amount how much to bid
     * @param id on which item
     * @throws IOException
     * @throws InterruptedException
     */
    public void makeBid(int amount, int id) throws IOException, InterruptedException {
        String info = amount + "\n" + id;
        Message bid = new Message(Origin.AGENT, Type.MAKE_BID, info);
        out.writeObject(bid);
        Thread.sleep(100);
        if(bidAccepted) {
            myBids.put(id, 0);
            agent.pendingBids -= myBids.get(id);
            agent.statusMessages.add("You bid $" + amount + " on Item " + id);
            itemValues.put(id, amount);
            myBids.put(id,amount);
            agent.pendingBids += myBids.get(id);
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
            while(runThread){
                try {
                    Message inMsg = (Message) in.readObject();
                    // System.out.println(inMsg);

                    switch (inMsg.getType()){
                        case ESTABLISH_CONNECTION -> {

                        }
                        case SEND_ITEMS -> {
                            // Get the items as a list, pull the current bid from the body of the message
                            String[] bodyNewLineSplit = inMsg.getBody().split("\n");
                            for(int i = 1; i < bodyNewLineSplit.length; i++){
                                String line = bodyNewLineSplit[i];
                                String[] lineSplit = line.split(" ");
                                int id = Integer.parseInt(lineSplit[1]);
                                int value = Integer.parseInt(lineSplit[lineSplit.length-1]);
                                if(!itemValues.containsKey(id)) agent.redrawTabsFlag = true;
                                else if(itemValues.get(id) != value) agent.redrawTabsFlag = true;
                                itemValues.put(id, value);
                            }
                            items = (List<Item>) inMsg.getInfo();
                        }
                        case BID_SUCCESS -> {
                            bidAccepted = true;
                        }
                        case BID_WIN -> {
                            String[] bodySplit = inMsg.getBody().split("AuctionID:");
                            String[] secondSplit = inMsg.getBody().split(" ");
                            // Find out what item we won and how much we spent on int
                            int transferAmount = Integer.parseInt(secondSplit[secondSplit.length-1]);
                            int auctionID = Integer.parseInt(secondSplit[secondSplit.length-4]);
                            // Alert the user that they won an item
                            agent.statusMessages.add(bodySplit[0]);
                            agent.redrawTabsFlag = true;
                            // Alert the bank that we won an item
                            agent.bank.sendWin(auctionID, transferAmount);
                            agent.balance -= transferAmount;
                            agent.pendingBids -= transferAmount;
                        }
                        case BID_OUTBID -> {
                            agent.redrawTabsFlag = true;
                            String[] info = inMsg.getBody().split("\n");
                            int auctionHouseID = Integer.parseInt(info[0]);
                            int itemID = Integer.parseInt(info[1]);
                            agent.pendingBids -= myBids.get(itemID); // Remove the previous bid from the agent's pending bids
                            agent.avaliableBalance += myBids.get(itemID);
                            myBids.put(itemID, 0); // Since the agent no longer has an active bid on the item
                            agent.statusMessages.add("You have been out bid on Item " + itemID + "!");
                        }
                    }

                } catch(EOFException e){
                    runThread = false;
                } catch (SocketException e){
                    System.out.println("Auction House closed");
                    runThread = false;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
