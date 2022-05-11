package Agent.Testing;

import util.Message;
import util.MessageEnums;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static util.MessageEnums.*;

public class AgentBankComm implements Runnable {
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public static void main(String[] args) throws IOException{
        String hostName = "100.64.0.230";
        int portNumber = 51362;
        String info = "Name:Bob" + '\n' + "Balance:100";
        //Message outMsg = new Message(AGENT, ESTABLISH_CONNECTION, info);
        System.out.println(info);

        try {
            System.out.println("0");
            Socket socket = new Socket(hostName, portNumber);
            System.out.println("1");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("2");
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            System.out.println("3");
            Message outMsg = new Message(Origin.AGENT, Type.ESTABLISH_CONNECTION, info);
            System.out.println("Sending message to Bank");
            Thread.sleep(1000);
            out.writeObject(outMsg);
            out.flush();
            Thread.sleep(1000);
            try {
                System.out.println("Attempting to print out message");
                Message response = (Message) in.readObject();
                System.out.println(response);

                System.out.println(response.getBody());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void run() {

    }
}
