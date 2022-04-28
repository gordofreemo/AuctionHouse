package AuctionHouse;

import util.Message;

import java.io.ObjectInputStream;

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

    /**
     * Listen for messages from agent socket and communicate with proxy as
     * necessary
     */
    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message)in.readObject();
                switch(message.getBody().toLowerCase()) {
                    case "get" -> proxy.sendItems();
                    case "bid" -> proxy.makeBid(100, 0);
                }
            }
        }
        catch (Exception e) {}
    }
}
