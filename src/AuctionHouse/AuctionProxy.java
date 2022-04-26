package AuctionHouse;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

// 51362

/*
It seems like the bank does not have a lot of interaction with the AH,
so it might be possible to just have the proxy with the agent, but time will tell.
 */
public class AuctionProxy {
    private int port;
    private String hostname;
    Socket socket;
    private InputStream auctionInput;
    private OutputStream auctionOutput;

    public AuctionProxy(String hostname, int port) throws IOException {
        this.port = port;
        this.hostname = hostname;
    }
    // rough draft
    public void makeBid(int bid) {}

    // rough draft
    public List<Item> getCurrentItems() {
        return null;
    }

    protected void messageHost() {
        // implement in future
    }

    public void makeConnection() throws IOException, ClassNotFoundException {
        socket = new Socket(hostname, port);
        auctionOutput = socket.getOutputStream();
        auctionInput = socket.getInputStream();
        ObjectInputStream out = new ObjectInputStream(auctionInput);
        Item item = (Item) out.readObject();
        System.out.println(item);
    }
}
