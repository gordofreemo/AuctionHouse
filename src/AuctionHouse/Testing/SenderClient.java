package AuctionHouse.Testing;

import util.Message;
import util.MessageEnums.Origin;
import util.MessageEnums.Type;

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
            Message message = new Message(Origin.AGENT, null, "");
            System.out.println("Please enter the message type: ");
            String type = sc.nextLine();
            System.out.println("Please enter the message body: ");
            String body = sc.nextLine();
            if(type.equals("bid")) {
                message.setType(Type.MAKE_BID);
                System.out.println("Please enter message body line 2 : ");
                body += '\n' + sc.nextLine();
            }
            else if(type.equals("get")) {
                message.setType(Type.GET_ITEMS);
            }

            if(!type.equals("read")) {
                message.setBody(body);
                out.writeObject(message);
            }
            Message read = (Message) in.readObject();
            System.out.println(read);
        }

    }

    private static class MessageListener implements Runnable {
        private ObjectInputStream stream;
        public MessageListener(ObjectInputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Message message = (Message) stream.readObject();
                    System.out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
