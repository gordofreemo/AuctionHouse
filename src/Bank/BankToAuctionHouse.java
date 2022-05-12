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
    private int port;
    private int balance = 0;
    private String name;
    private String desc;
    private boolean running = true;

    /**
     * Constructor for BankToAuctionHouse connection
     * @param clientSocket socket connection to auction house
     * @param out object output stream from socket
     * @param in object input stream from socket
     * @param msg inital message sent to BankInit
     * @throws IOException
     */
    public BankToAuctionHouse(
        Socket clientSocket,
        ObjectOutputStream out,
        ObjectInputStream in,
        Message msg
    ) throws IOException {
        System.out.println("Auction House connection detected");

        // Register thread with bank state tracker
        BankState.getInstance().addAuctionHouseThread(this);

        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;

        establishConnection(msg);
    }

    /**
     * Main loop for thread. Waits for a message from input stream and then
     * processes it. Will terminate when told to do so. Has 3 listen triggers:
     *     MAKE_BID: Informs the bank of when an agent makes a bid,
     *               automatically blocks those funds. Sends a response if
     *               successfully blocked, or insufficent funds.
     *     BID_OUTBID: Informs the bank that an agent has been outbid. Will
     *                 release their blocked funds.
     *     CLOSE_CONNECTION: Closes the connection and termiantes the thread
     */
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
                        Message outMsg = new Message(
                            Origin.BANK,
                            BankState.getInstance().blockFunds(amount, id), id + ""
                        );

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
                        BankState.getInstance().releaseFunds(
                            releaseAmount,
                            agentId
                        );
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
     * Properly closes the connection and removes the thread from the thread
     * tracker
     */
    private void closeConnection() {
        System.out.println("Socket closed, ending thread");
        BankState.getInstance().removeAuctionHouseThread(id);
        running = false;
    }

    /**
     * Established the connection with the inital message sent to BankInit
     * @param msg inital message sent to BankInit
     */
    private void establishConnection(Message msg) {
        String[] body = msg.getBody().split("\n");
        name = body[0];
        port = Integer.parseInt(body[1]);
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

    /**
     * Gets the auctions houses ID generated from BankState
     * @return acution house id
     */
    public int getId() {
        return id;
    }

    /**
     * Adds funds to the auction house's balance
     * @param amount amount to add to balance
     */
    public void addFunds(int amount) {
        balance += amount;
    }

    /**
     * Gets the auctionhouses name, host name, and their server port
     * @return name:hostname:serverport
     */
    public String getAddress() {
        return name + ":" + clientSocket.getInetAddress().getHostAddress() + ":" + port;
    }
}
