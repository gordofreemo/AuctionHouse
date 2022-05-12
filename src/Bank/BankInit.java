package Bank;

import java.io.IOException;
import java.net.Socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import util.*;
import util.MessageEnums.*;

public class BankInit implements Runnable {
    private final Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Constructor to read the first message of a new connection
     * @param clientSocket socket to new connection
     * @param out object output stream from socket
     * @param in object input stream from socket
     * @throws IOException
     */
    public BankInit(
        Socket clientSocket,
        ObjectOutputStream out,
        ObjectInputStream in
    ) throws IOException {
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        System.out.println("New bank init thread created");
    }

    /**
     * Main function, waits for first message and then reads the origin to find
     * out what type of thread to create
     */
    @Override
    public void run() {
        try {
            Message msg = (Message) in.readObject();
            System.out.println("Message Recieved");

            if (msg.getOrigin() == Origin.AGENT) {
                System.out.println("Origin type agent");
                Thread t = new Thread(new BankToAgent(clientSocket, out, in, msg));
                t.start();
            }
            else if (msg.getOrigin() == Origin.AUCTIONHOUSE) {
                System.out.println("Origin type auction house");
                Thread t = new Thread(new BankToAuctionHouse(clientSocket, out, in, msg));
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
