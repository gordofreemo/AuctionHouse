package AuctionHouse;

import util.Message;
import util.MessageEnums.*;

import java.io.ObjectInputStream;
import java.util.Scanner;

/**
 * Class responsible for receiving messages from Agent
 */

public class AgentListener implements Runnable {
    private ObjectInputStream in;
    private AgentProxy proxy;

    public AgentListener(ObjectInputStream in, AgentProxy proxy) {
        this.in = in;
        this.proxy = proxy;
    }

    private void parseBid(Message message) {
        Scanner sc = new Scanner(message.body);
        Integer amount = Integer.parseInt(sc.nextLine());
        Integer auctionID = Integer.parseInt(sc.nextLine());
        proxy.makeBid(amount, auctionID);
    }

    /**
     * Listen for messages from agent socket and communicate with proxy as
     * necessary
     */
    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message)in.readObject();
                switch(message.type) {
                    case MAKE_BID -> parseBid(message);
                    case GET_ITEMS -> proxy.sendItems();
                }
            }
        }
        catch (Exception e) {}
    }
}
