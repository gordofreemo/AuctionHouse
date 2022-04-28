package AuctionHouse.Testing;

import util.Message;
import util.MessageEnums;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SenderClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.0.1", 4001);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        Message message = new Message(MessageEnums.Origin.AGENT, null, "bid");
        out.writeObject(message);


        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Message response = (Message)in.readObject();
        System.out.println(response.getBody());

    }
}
