package Bank;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import util.Message;
import util.MessageEnums.*;

public class BankToAuctionHouse implements Runnable {
    private final Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int id;
    private int balance = 0;
    private String name;
    private String desc;
    private boolean running = true;

    public BankToAuctionHouse(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in, Message msg) throws IOException {
        System.out.println("Auction House connection detected");

        // Register thread with bank state tracker
        BankState.getInstance().addAuctionHouseThread(this);

        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;

        initHouse(msg);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Message msg = (Message) in.readObject();

                switch ((Type) msg.getType()) {
                    case MAKE_BID -> {
                        String[] body = msg.getBody().split("\n");
                        int amount = Integer.parseInt(body[0]);
                        int id = Integer.parseInt(body[1]);
                        Message outMsg = new Message(Origin.BANK, BankState.getInstance().blockFunds(amount, id), id + "");
                        try {
                            out.writeObject(outMsg);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    case BID_OUTBID -> {
                        String[] body = msg.getBody().split("\n");
                        int releaseAmount = Integer.parseInt(body[0]);
                        int agentId = Integer.parseInt(body[1]);
                        BankState.getInstance().releaseFunds(releaseAmount, agentId);
                    }

                    case CLOSE_CONNECTION -> {
                        closeConnection();
                    }

                    default -> {}
                }
            }
            catch (EOFException | SocketException e) {
                closeConnection();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() {
        System.out.println("Socket closed, ending thread");
        BankState.getInstance().removeAuctionHouseThread(id);
        running = false;
    }

    private void initHouse(Message msg) {
        String[] body = msg.getBody().split("\n");
        name = body[0];
        id = BankState.getInstance().getNewId();

        System.out.println("name: " + name + "\nid: " + id);

        Message outMsg = new Message(Origin.BANK, Type.ESTABLISH_CONNECTION, id + "");
        try {
            out.writeObject(outMsg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void addFunds(int amount) {
        balance += amount;
    }

    public String getAddress() {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
}
