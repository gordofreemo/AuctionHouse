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

    public BankInit(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in) throws IOException {
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        System.out.println("New bank init thread created");
    }

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
