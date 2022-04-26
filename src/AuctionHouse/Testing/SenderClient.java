package AuctionHouse.Testing;

import AuctionHouse.AuctionProxy;

import java.io.IOException;

public class SenderClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        AuctionProxy proxy = new AuctionProxy("localhost", 4001);
        proxy.makeConnection();
    }
}
