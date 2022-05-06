package Bank;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

    public BankToAgent(Socket clientSocket) throws IOException {
        // Register thread with bank state tracker
        BankState.getInstance().addAgentThread(this);

        this.clientSocket = clientSocket;
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

        System.out.println("Agent connection recieved from " + clientSocket.getInetAddress().getAddress());
    }

    @Override
    public void run() {
        while (running) {
            Message msg = null;
            try {
                msg = (Message) in.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                msg = null;
            }

            switch ((Type) msg.getType()) {
                case ESTABLISH_CONNECTION:
                    System.out.println("Establishing connection");
                    initUser(msg.getBody());

                    while (BankState.getInstance().getAuctionHouseThreads().size() == 0) {}

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
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case CHECK_FUNDS: // send response message to the auction house
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
                    break;
                case BID_WIN:

                    break;
                case CLOSE_CONNECTION:
                    running = true;
                    break;
                default: break;
            }
        }
    }

    private void initUser(String body) {
        String[] msg = body.split("\n");
        name = msg[0].split(":")[1];
        balance = Integer.parseInt(msg[1].split(":")[1]);
        id = BankState.getInstance().getNewId();
        System.out.println("name: " + name + "\nbalance: " + balance + "\nid: " + id);
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

    public void releaseFunds() {
        balance += blocked;
        blocked = 0;
    }
}
