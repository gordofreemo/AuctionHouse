package AuctionHouse;

import util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
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


    /**
     * When receiving the bid message, parse the message for the amount
     * and the auction on which the agent wants to bid on
     * @param message - bid message to parse
     */
    private void parseBid(Message message) {
        Scanner sc = new Scanner(message.getBody());
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
                System.out.println(message);
                switch(message.getType()) {
                    case MAKE_BID -> parseBid(message);
                    case GET_ITEMS -> proxy.sendItems();
                    case CAN_CLOSE -> proxy.tryClose();
                }
            }
        }
        catch (SocketException e) {
            proxy.killConnection();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
