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

    public AgentProxy(ObjectOutputStream out, AuctionHouse auctionHouse) throws IOException {
        this.auctionHouse = auctionHouse;
        this.out = out;
    }

    // process the agent making a bid
    public void makeBid(int amount, int auctionID) {
        Message message = new Message(ORIGIN, Type.BID_FAILED, "");
        message.body = "bid failed";
        List<Auction> auctions = auctionHouse.getAuctions();
        Auction auction = null;
        for(Auction  x : auctions) if(x.getAuctionID() == auctionID) auction = x;
        if(auction == null || !auction.validBid(amount)) {
            sendMessage(message);
            return;
        }

        auction.makeBid(this, amount);
        message.body = "bid success";
        message.type = Type.BID_SUCCESS;
        sendMessage(message);
    }

    // alert agent he has won bid
    public void alertWin(Auction auction) {
        Item item = auction.getItem();
        auctionHouse.endAuction(auction.getAuctionID());
        Message message = new Message(ORIGIN, Type.BID_WIN, "YOU WON " + item);
        sendMessage(message);
    }

    // send agent the list of items being auctioned
    public void sendItems() {
        List<Item> itemList = auctionHouse.getAuctions()
                .stream().map(Auction::getItem).toList();
        String auctions = "\n";
        for(Auction auction : auctionHouse.getAuctions()) auctions += auction.toString() + '\n';
        Message message = new Message(ORIGIN, Type.SEND_ITEMS, auctions);
        message.info = itemList;
        sendMessage(message);
    }

    // method is called when a message needs to be sent to a particular agent
    // from an outside source
    public void messageRequest(Message message) {
        sendMessage(message);
    }

    // send the message across the network
    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
