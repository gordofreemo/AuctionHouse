package AuctionHouse;

import util.Message;
import util.MessageEnums;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BankProxy {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int accountID;
    private final MessageEnums.Origin origin = MessageEnums.Origin.AUCTIONHOUSE;
    private BankListener listener;

    public BankProxy(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
        listener = new BankListener();
    }


    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private class BankListener implements Runnable {

        @Override
        public void run() {

        }
    }
}
