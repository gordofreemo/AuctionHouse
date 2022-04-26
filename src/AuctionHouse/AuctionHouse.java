package AuctionHouse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AuctionHouse {
    private ArrayList<Auction> auctionList;
    private ServerSocket serverSocket;
    public AuctionHouse() throws IOException {
        auctionList = new ArrayList<Auction>();
        serverSocket = new ServerSocket(0);
    }


    private void makeBid(int auctionID, int amount) {
        for(Auction auction : auctionList) {
            if(auction.getAuctionID() == auctionID) auction.makeBid();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 4001;
        try (ServerSocket socket = new ServerSocket(port)) {
            while(true) {
                Socket clientSocket = socket.accept();
                OutputStream stream = clientSocket.getOutputStream();
                ObjectOutputStream objectOut = new ObjectOutputStream(stream);
                objectOut.writeObject(new Item("Huge Table", 10, 10));
            }
        }
    }

}
