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

        try(Socket socket = new Socket(hostName, portNumber);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            Message outMsg = new Message(Origin.AGENT, Type.ESTABLISH_CONNECTION, info);
            System.out.println("Sending message to Bank");
            out.writeObject(outMsg);

            try {
                Object test = in.readObject();
                System.out.println(test);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void run() {

    }
}
