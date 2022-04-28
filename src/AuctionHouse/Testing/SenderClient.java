package AuctionHouse.Testing;

import util.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SenderClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", 4001);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        Message message = new Message(null, null, "YO MR WHITE");
        out.writeObject(message);
    }
}
