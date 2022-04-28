package AuctionHouse;

import util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class representing a network connection with an Agent type.
 * Encapsulates the communication
 */

public class AgentHandler {
    private Socket agentSocket;
    private Thread listeningThread;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    public AgentHandler(Socket agentSocket) throws IOException {
        this.agentSocket = agentSocket;
        inStream = new ObjectInputStream(agentSocket.getInputStream());
        outStream = new ObjectOutputStream(agentSocket.getOutputStream());
        makeListener();
    }

    public void rejectBid() {

    }

    public void acceptBid() {

    }

    public void alertWin() {

    }

    private void makeListener() {
        Runnable listener = new Runnable() {
            @Override
            public void run() {
                while(agentSocket.isConnected()) {
                    Message message;
                    try {
                        message = (Message)(inStream.readObject());
                        System.out.println(message.getBody());
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        listeningThread = new Thread(listener);
        listeningThread.start();
    }
}
