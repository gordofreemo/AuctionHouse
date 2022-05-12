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

    /**
     * Constructor for a single BankToAgent connection
     * @param clientSocket socket connection to agent
     * @param out object output stream from socket
     * @param in object input stream from socket
     * @param msg inital message from BankInit
     * @throws IOException
     */
    public BankToAgent(
        Socket clientSocket,
        ObjectOutputStream out,
        ObjectInputStream in,
        Message msg
    ) throws IOException {
        System.out.println("Agent connection detected");

        // Register thread with bank state tracker
        BankState.getInstance().addAgentThread(this);

        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;

        // establish conection
        System.out.println("Establishing connection");
        establishConnection(msg);
    }

    /**
     * Main loop for thread. Waits for a message from input stream and then
     * processes it. Will terminate when told to do so. Has 3 listen triggers:
     *     CHECK_FUNDS: Checks the funds of an agent and sends it back as a
     *                  response
     *     BID_WIN: transfers the blocked funds from an agent to an
     *              auction house
     *     GET_HOUSES: returns a list of all auction houses
     *     CLOSE_CONNECTION: Closes the connection and termiantes the thread
     */
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
                        String[] body = msg.getBody().split("\n");
                        int auctionHouseID = Integer.parseInt(body[0]);
                        int transferAmount = Integer.parseInt(body[1]);

                        try {
                            BankState.getInstance().transferFunds(id, auctionHouseID, transferAmount);
                            blocked -= transferAmount;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    case GET_HOUSES -> {
                        List<String> auctionHouses = BankState.getInstance().getAuctionHouses();
                        String addresses = "garbage\n";
                        for (var auctionHouse : auctionHouses) {
                            addresses += auctionHouse + '\n';
                        }

                        Message outMsg = new Message(
                                Origin.BANK,
                                Type.SEND_HOUSES,
                                addresses
                        );

                        try{
                            out.writeObject(outMsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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

    /**
     * Established the connection with the inital message sent to BankInit
     * @param msg inital message sent to BankInit
     */
    private void establishConnection(Message msg) {
        String[] body = msg.getBody().split("\n");
        name = body[0].split(":")[1];
        balance = Integer.parseInt(body[1].split(":")[1]);
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

    /**
     * Gets the auctions houses ID generated from BankState
     * @return acution house id
     */
    public int getId() {
        return id;
    }

    /**
     * Blocks funds from agent
     * @param amount amount to block
     * @return success or failure message
     */
    public Type blockFunds(int amount) {
        if (amount > balance) {
            return Type.BID_FAILED;
        }
        blocked = amount;
        balance -= amount;
        return Type.BID_SUCCESS;
    }

    /**
     * releases blocked funds back to balance
     * @param amount amount to release
     */
    public void releaseFunds(int amount) {
        blocked -= amount;
        balance += amount;
    }

    /**
     * Gets the auctionhouses name, host name, and their server port
     * @return name:hostname:serverport
     */
    private void closeConnection() {
        System.out.println("Socket closed, ending thread");
        BankState.getInstance().removeAgentThread(id);
        running = false;
    }
}
