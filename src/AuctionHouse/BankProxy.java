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

public class BankProxy {
    private ObjectOutputStream out;
    private int accountID;
    private final Origin origin = Origin.AUCTIONHOUSE;
    private BankListener listener;
    private HashMap<Integer, Tuple<Thread, AsyncHelper>> awaits;

    public BankProxy(String hostname, int port) throws IOException {
        Socket socket = new Socket(hostname, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(new Message(origin, Type.ESTABLISH_CONNECTION, "The Shop"));
        listener = new BankListener(new ObjectInputStream(socket.getInputStream()));
        awaits = new HashMap<>();
        new Thread(listener).start();
    }

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

    public void unblockFunds(int agentID, int amount) {

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
                    System.out.println(msg);
                    switch (msg.getType()) {
                        case BID_FAILED -> {
                            int id = Integer.parseInt(msg.getBody());
                            Tuple<Thread, AsyncHelper> tuple = awaits.get(id);
                            tuple.y.state = false;
                            tuple.x.interrupt();
                        }
                        case BID_WIN -> {
                            int id = Integer.parseInt(msg.getBody());
                            Tuple<Thread, AsyncHelper> tuple = awaits.get(id);
                            tuple.y.state = true;
                            tuple.x.interrupt();
                        }
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
