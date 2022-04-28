package AuctionHouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AuctionHouse {
    private ArrayList<Auction> auctionList;
    private ServerSocket serverSocket;
    private ArrayList<AgentHandler> connectedAgents;

    public AuctionHouse() throws IOException {
        auctionList = new ArrayList<Auction>();
        serverSocket = new ServerSocket(0);
        connectedAgents = new ArrayList<AgentHandler>();
    }

    private void connectAgent(Socket socket) throws IOException {
        connectedAgents.add(new AgentHandler(socket));
    }

    public static void main(String[] args) throws IOException {
        AuctionHouse auctionHouse = new AuctionHouse();
        int port = 4001;
        try (ServerSocket socket = new ServerSocket(port)) {
            while(true) {
                Socket clientSocket = socket.accept();
                auctionHouse.connectAgent(clientSocket);
            }
        }
    }

}
