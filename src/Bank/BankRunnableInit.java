package Bank;

import java.io.BufferedReader;
import java.io.PrintWriter;

import java.io.IOException;
import java.net.Socket;

import java.io.InputStream;
import java.io.ObjectInputStream;

import util.*;
import util.MessageEnums.*;

public class BankRunnableInit implements Runnable {
    private final Socket clientSocket;
    // private PrintWriter out;
    private ObjectInputStream in;

    public BankRunnableInit(Socket clientSocket) throws IOException {
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
        } catch (IOException ex) {
            msg = null;
        } catch (ClassNotFoundException e) { // @TODO FIGURE OUT WHY WE NEED THIS
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (msg.getOrigin() == Origin.AGENT) {
            BankRunnableAgent kk = null;
            try {
                kk = new BankRunnableAgent(clientSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Thread t = new Thread(kk);
            t.start();
        }
        else if (msg.getOrigin() == Origin.AUCTIONHOUSE) {
            BankRunnableAuctionHouse kk = null;
            try {
                kk = new BankRunnableAuctionHouse(clientSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Thread t = new Thread(kk);
            t.start();
        }
    }
}
