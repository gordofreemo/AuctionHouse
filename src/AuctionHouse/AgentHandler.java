package AuctionHouse;

import java.io.ObjectOutputStream;

/**
 * Class representing a network connection with an Agent type.
 * Encapsulates the communication
 */

public class AgentHandler {
    private ObjectOutputStream outStream;

    public AgentHandler(ObjectOutputStream out) {
        outStream = out;
    }

    public void rejectBid() {

    }

    public void acceptBid() {

    }

    public void alertWin() {

    }

}
