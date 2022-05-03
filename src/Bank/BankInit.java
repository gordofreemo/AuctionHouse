package Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;

import java.io.IOException;
import java.net.Socket;

import java.io.InputStream;
import java.io.ObjectInputStream;

import util.*;
import util.MessageEnums.*;

public class BankInit implements Runnable {
    private final Socket clientSocket;
    // private PrintWriter out;
    private ObjectInputStream in;

    public BankInit(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        // out = new PrintWriter(clientSocket.getOutputStream(), true);
        // in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Override
    public void run() {
        Message msg = null;
        try {
            msg = (Message) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            msg = null;
        }

        if (msg.getOrigin() == Origin.AGENT) {
            BankToAgent kk = null;
            try {
                kk = new BankToAgent(clientSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Thread t = new Thread(kk);
            t.start();
        }
        else if (msg.getOrigin() == Origin.AUCTIONHOUSE) {
            BankToAuctionHouse kk = null;
            try {
                kk = new BankToAuctionHouse(clientSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Thread t = new Thread(kk);
            t.start();
        }
    }
}
