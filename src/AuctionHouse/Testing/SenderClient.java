package AuctionHouse.Testing;

import util.Message;
import util.MessageEnums;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SenderClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.0.1", 4001);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Please enter the message type: ");
            String type = sc.nextLine();
            System.out.println("Please enter the message body: ");
            String body = sc.nextLine();
            if(type.equals("bid")) {
                System.out.println("Please enter message body line 2 : ");
                body += '\n' + sc.nextLine();
            }
            if(!type.equals("read")) {
                Message message = new Message(MessageEnums.Origin.AGENT, type, body);
                out.writeObject(message);
            }
            Message returnMessage = (Message) in.readObject();
            System.out.println(returnMessage);
        }

    }
}
