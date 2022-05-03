package Bank;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Message;

public class BankToAuctionHouse implements Runnable {
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private String desc;

    public BankToAuctionHouse(Socket clientSocket) throws IOException {
        // Register thread with bank state tracker
        BankState.getInstance().addAuctionHouseThread(this);

        this.clientSocket = clientSocket;
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        Message msg = null;
        try {
            msg = (Message) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            msg = null;
        }
    }

    public String getAddress() {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
}
