package AuctionHouse;

import util.Message;
import util.MessageEnums;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AuctionHouse {
    private ArrayList<Auction> auctionList;
    private ServerSocket serverSocket;
    private ArrayList<AgentProxy> connectedAgents;

    public AuctionHouse() throws IOException {
        auctionList = new ArrayList<Auction>();
        serverSocket = new ServerSocket(0);
        connectedAgents = new ArrayList<>();
    }

    private void connectAgent(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        AgentHandler handler = new AgentHandler(out);
        AgentListener listener = new AgentListener(in);
        connectedAgents.add(new AgentProxy(handler, listener));
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        AuctionHouse auctionHouse = new AuctionHouse();
        int port = 4001;
        try (ServerSocket socket = new ServerSocket(port)) {
            while(true) {
                Socket clientSocket = socket.accept();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                Message message = (Message) in.readObject();
                if(message.origin == MessageEnums.Origin.AGENT) {
                    auctionHouse.connectAgent(clientSocket, in, out);
                }
            }
        }
    }

}
