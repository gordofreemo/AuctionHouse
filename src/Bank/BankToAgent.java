package Bank;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import util.Message;
import util.MessageEnums.*;

import java.util.*;

public class BankToAgent implements Runnable{
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String name;
    private int balance;
    private int blocked = 0;
    private int id;
    private boolean running = true;

    public BankToAgent(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in, Message msg) throws IOException {
        System.out.println("Agent connection detected");

        // Register thread with bank state tracker
        BankState.getInstance().addAgentThread(this);

        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;

        // establish conection
        System.out.println("Establishing connection");
        establishConnection(msg.getBody());
    }

    @Override
    public void run() {
        while (running) {
            try {
                Message msg = (Message) in.readObject();
                switch ((Type) msg.getType()) {
                    case CHECK_FUNDS -> {
                        System.out.println("Checking funds");
                        String[] body = msg.getBody().split("\n");

                        try {
                            int amount = Integer.parseInt(body[0]);
                            int id = Integer.parseInt(body[1]);
                            out.writeObject((Type) BankState.getInstance().blockFunds(amount, id));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    case BID_WIN -> {

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

    private void establishConnection(String body) {
        String[] msg = body.split("\n");
        name = msg[0].split(":")[1];
        balance = Integer.parseInt(msg[1].split(":")[1]);
        id = BankState.getInstance().getNewId();
        System.out.println("name: " + name + "\nbalance: " + balance + "\nid: " + id);

        while (BankState.getInstance().getAuctionHouseThreads().size() == 0) {} // @TODO busy wait

        List<String> auctionHouses = BankState.getInstance().getAuctionHouses();

        String addresses = "";
        for (var auctionHouse : auctionHouses) {
            addresses += auctionHouse + "\n";
        }

        Message outMsg = new Message(
            Origin.BANK,
            Type.ESTABLISH_CONNECTION,
            id + "\n" + addresses.trim()
        );

        try {
            out.writeObject(outMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public Type blockFunds(int amount) {
        if (amount > balance) {
            return Type.BID_FAILED;
        }
        blocked = amount;
        balance -= amount;
        return Type.BID_SUCCESS;
    }

    public void releaseFunds(int amount, int id) {
        blocked -= amount;
        balance += amount;
    }

    private void closeConnection() {
        System.out.println("Socket closed, ending thread");
        BankState.getInstance().removeAgentThread(id);
        running = false;
    }
}
