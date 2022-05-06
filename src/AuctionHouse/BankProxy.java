package AuctionHouse;

import util.Message;
import util.MessageEnums.Type;
import util.MessageEnums.Origin;
import util.Tuple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class BankProxy {
    private ObjectOutputStream out;
    private int accountID;
    private final Origin origin = Origin.AUCTIONHOUSE;
    private BankListener listener;
    private HashMap<Integer, Tuple<Thread, AsyncHelper>> awaits;

    public BankProxy(ObjectInputStream in, ObjectOutputStream out) {
        this.out = out;
        listener = new BankListener(in);
        new Thread(listener).start();
    }

    public boolean blockFunds(int agentID, int amount) {
        Message message = new Message(origin, Type.MAKE_BID, "");
        message.setBody("" + agentID + '\n' + amount);
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

    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


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
                    switch (msg.getType()) {
                        case BID_FAILED -> {
                            int id = Integer.parseInt(msg.getBody());
                            AsyncHelper await = awaits.get(id).y;
                            await.state = false;

                        }
                        case BID_WIN -> {
                            int id = Integer.parseInt(msg.getBody());
                            AsyncHelper await = awaits.get(id).y;
                            await.state = true;
                        }
                        default -> System.out.println(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class AsyncHelper implements Runnable {
        public boolean state;
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
