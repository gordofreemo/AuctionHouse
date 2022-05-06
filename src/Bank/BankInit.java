package Bank;

import java.io.IOException;
import java.net.Socket;

import java.io.ObjectInputStream;

import util.*;
import util.MessageEnums.*;

public class BankInit implements Runnable {
    private final Socket clientSocket;
    private ObjectInputStream in;

    public BankInit(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("New bank init thread created");
    }

    @Override
    public void run() {
        try {
            Message msg = (Message) in.readObject();
            System.out.println("Message Recieved");

            if (msg.getOrigin() == Origin.AGENT) {
                System.out.println("Origin type agent");
                Thread t = new Thread(new BankToAgent(clientSocket, in, msg));
                t.start();
            }
            else if (msg.getOrigin() == Origin.AUCTIONHOUSE) {
                System.out.println("Origin type auction house");
                Thread t = new Thread(new BankToAuctionHouse(clientSocket));
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
