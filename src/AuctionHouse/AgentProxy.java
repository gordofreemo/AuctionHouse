package AuctionHouse;

import util.Message;
import util.MessageEnums.Origin;
import util.MessageEnums.Type;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Encapsulates agent interaction w/ the auction house and communication
 * over the network.
 */

public class AgentProxy {
    private ObjectOutputStream out;
    private final Origin ORIGIN = Origin.AUCTIONHOUSE;
    private AuctionHouse auctionHouse;
    private int agentID;

    public AgentProxy(ObjectOutputStream out, AuctionHouse auctionHouse, int agentID) {
        this.auctionHouse = auctionHouse;
        this.out = out;
        this.agentID = agentID;
        Message message = new Message(
                ORIGIN, Type.ACKNOWLEDGE_CONNECTION, "Agent, Hello"
        );
        sendMessage(message);
    }

    /**
     * @return - bank account ID of the current agent
     */
    public int getAgentID() {
        return agentID;
    }

    /**
     * Closes the socket connection associated with the agent
     */
    public void killConnection() {
        auctionHouse.endConnection(this);
    }

    /**
     * Handles the logic for an agent making a bid on an auction.
     * Sends either a bid failed or a bid success message to the agent
     * depending on what happened. If bid is successful, alerts the previous
     * bidder that he has been outbid.
     * @param amount - amount that the agent is bidding
     * @param auctionID - ID of the auction the agent is bidding on
     */
    public void makeBid(int amount, int auctionID) {
        Message message = new Message(ORIGIN, Type.BID_FAILED, "");
        message.setBody("bid failed");
        List<Auction> auctions = auctionHouse.getAuctions();
        Auction auction = null;
        for(Auction  x : auctions) if(x.getAuctionID() == auctionID) auction = x;
        if(auction == null || !auction.validBid(amount)) {
            sendMessage(message);
            return;
        }
        boolean bankCheck = auctionHouse.blockFunds(agentID, amount);
        auction.makeBid(this, amount);

        if(bankCheck) {
            message.setBody("bid success");
            message.setType(Type.BID_SUCCESS);
        }
        auctionHouse.alertOutbid(auctionID);
        sendMessage(message);
    }

    /**
     * Sends a message to the agent who has just won a given auction.
     * Also ends the auction
     * @param auction - auction object representing auction that was just won
     */
    public void alertWin(Auction auction) {
        Item item = auction.getItem();
        item.auctionID = auctionHouse.getHouseID();
        auctionHouse.endAuction(auction.getAuctionID());
        Message message = new Message(ORIGIN, Type.BID_WIN, "YOU WON "+item);
        message.setInfo(item);
        sendMessage(message);
    }

    /**
     * When receiving a get_items request, send a list of the items
     * to the agent as a message
     */
    public void sendItems() {
        List<Item> itemList = auctionHouse.getAuctions()
                .stream().map(Auction::getItem).toList();
        StringBuilder auctions = new StringBuilder("\n");
        for(Auction auction : auctionHouse.getAuctions()) {
            auctions.append(auction.toString()).append('\n');
        }
        Message message = new Message(
                ORIGIN, Type.SEND_ITEMS, auctions.toString()
        );
        message.setInfo(itemList);
        sendMessage(message);
    }

    /**
     * Used to verify whether this agent is able to safely close the connection
     * with the auction house. Checks whether there are any active bids,
     * if there are it sends a message to the agent saying it cannot close the
     * socket, otherwise tells the agent that it can.
     */
    public void tryClose() {
        Message message = new Message(ORIGIN, null, "");
        List<Auction> auctions = auctionHouse.getAuctions();
        boolean active = auctions.stream().anyMatch(e -> e.getBidder() == this);
        if(active) message.setType(Type.CANT_CLOSE);
        else message.setType(Type.CAN_CLOSE);
        sendMessage(message);
    }

    /**
     * Method that is used by outside classes to send a given message to the
     * agent. Might remove this in favor of message system encapsulation
     * @param message - message to send
     */
    public void messageRequest(Message message) {
        sendMessage(message);
    }

    /**
     * Sends a message across the network. It is its own separate method
     * so it can catch that pesky IO exception.
     * @param message - message to send over the network to given agent
     */
    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
