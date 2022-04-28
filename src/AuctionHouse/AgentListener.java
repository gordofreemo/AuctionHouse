package AuctionHouse;

import java.io.ObjectInputStream;

/**
 * Class responsible for receiving messages from Agent
 */

public class AgentListener implements Runnable {
    private ObjectInputStream in;

    public AgentListener(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {

    }
}
