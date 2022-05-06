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
        String hostName = "10.88.174.104";
        int portNumber = 51362;
        String info = "Name:Bob" + '\n' + "Balance:100";
        //Message outMsg = new Message(AGENT, ESTABLISH_CONNECTION, info);
        System.out.println(info);

        try {
            System.out.println("0");
            Socket socket = new Socket(hostName, portNumber);
            System.out.println("1");
            Thread.sleep(2000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("2");
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            System.out.println("3");
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            Message outMsg = new Message(Origin.AGENT, Type.ESTABLISH_CONNECTION, info);
            System.out.println("Sending message to Bank");
            Thread.sleep(1000);
            out.writeObject(outMsg);
            out.flush();

            try {
                Message response = (Message) in.readObject();
                System.out.println(response);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(true){}

    }
    @Override
    public void run() {

    }
}
