package AuctionHouse;

import util.Message;
import util.MessageEnums;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class AgentProxy {
    private ObjectOutputStream out;
    private final MessageEnums.Origin ORIGIN = MessageEnums.Origin.AUCTIONHOUSE;
    private AuctionHouse auctionHouse;

    public AgentProxy(ObjectOutputStream out, AuctionHouse auctionHouse) throws IOException {
        this.auctionHouse = auctionHouse;
        this.out = out;
    }

    public void makeBid(int amount, int auctionID) {
        List<Auction> auctions = auctionHouse.getAuctions();
        Auction auction = null;
        for(Auction  x : auctions) if(x.getAuctionID() == auctionID) auction = x;
        if(auction == null) return;
        auction.makeBid(this, amount);
    }

    public void alertWin(Auction auction) {
        Item item = auction.getItem();
        Message message = new Message(ORIGIN, "", "YOU WON " + item);
        sendMessage(message);
    }

    public void sendItems() {
        List<Item> itemList = auctionHouse.getAuctions()
                .stream().map(Auction::getItem).toList();
        Message message = new Message(ORIGIN, "", "");
        message.setProxy(itemList);
        sendMessage(message);
    }

    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
