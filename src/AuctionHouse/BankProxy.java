package AuctionHouse;

import util.Message;
import util.MessageEnums.Type;
import util.MessageEnums.Origin;
import util.Tuple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * This class encapsulates the communication between the bank and the
 * auction house.
 */

public class BankProxy {
    private ObjectOutputStream out;
    private int accountID;
    private final Origin origin = Origin.AUCTIONHOUSE;
    private BankListener listener;
    private HashMap<Integer, Tuple<Thread, AsyncHelper>> awaits;

    public BankProxy(String hostname, int port, int hostPort) throws IOException {
        Socket socket = new Socket(hostname, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        listener = new BankListener(new ObjectInputStream(socket.getInputStream()));
        new Thread(listener).start();
        out.writeObject(new Message(origin, Type.ESTABLISH_CONNECTION, "The Shop\n"+hostPort));
        awaits = new HashMap<>();
    }

    /**
     * @return - the bank account ID of the auction house
     */
    public int getAccountID() {
        return accountID;
    }

    /**
     * Sends a message to block funds for a given agent. Any thread calling
     * this method will block until a response is received
     * @param agentID - agent account to block funds for
     * @param amount - amount to block
     * @return - true if block was successful, false if agent did not have
     * enough funds
     */
    public boolean blockFunds(int agentID, int amount) {
        Message message = new Message(origin, Type.MAKE_BID, "");
        message.setBody("" + amount + '\n' + agentID);
        // Sets up logic for awaiting response from bank
        AsyncHelper await = new AsyncHelper();
        Thread thread = new Thread(await);
        awaits.put(agentID, new Tuple<>(thread, await));
        thread.start();
        sendMessage(message);
        try {
            thread.join();
        } catch (InterruptedException e) {}
        awaits.remove(agentID);

        return await.state;
    }

    /**
     * Sends a message to the bank to unblock funds that were previously blocked
     * @param agentID - agent to unblock funds for
     * @param amount - amount to unblock
     */
    public void unblockFunds(int agentID, int amount) {
        Message message = new Message(origin, Type.UNBLOCK_FUNDS, "");
        message.setBody("" + amount + '\n' + agentID);
        sendMessage(message);
    }

    /**
     * @param message - message to send to bank
     */
    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Runnable object to parse messages coming in from bank
     */
    private class BankListener implements Runnable {
        private ObjectInputStream in;

        public BankListener(ObjectInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Message msg = (Message)in.readObject();
                    System.out.println(msg);
                    switch (msg.getType()) {
                        case BID_FAILED -> {
                            int id = Integer.parseInt(msg.getBody());
                            Tuple<Thread, AsyncHelper> tuple = awaits.get(id);
                            tuple.y.state = false;
                            tuple.x.interrupt();
                        }
                        case BID_SUCCESS -> {
                            int id = Integer.parseInt(msg.getBody());
                            Tuple<Thread, AsyncHelper> tuple = awaits.get(id);
                            tuple.y.state = true;
                            tuple.x.interrupt();
                        }
                        case ACKNOWLEDGE_CONNECTION -> accountID = Integer.parseInt(msg.getBody());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Used to assist in blocking until a response is gotten back.
     * The thread that needs to block starts up a new AsyncHelper, joins it,
     * and when a message is received from the bank, the appropriate async
     * helper is interrupted so that the original thread can unblock.
     */
    private class AsyncHelper implements Runnable {
        public boolean state;
        @Override
        public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {}
        }
    }

}
