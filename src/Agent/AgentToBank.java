package Agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Message;
import util.MessageEnums.*;

import java.util.*;

public class AgentToBank implements Runnable {
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public AgentToBank(Socket clientSocket) throws IOException{


    }

    @Override
    public void run() {
        Message inMsg = null;

        try {
            inMsg = (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            inMsg = null;
        }

        switch(inMsg.getType()){
            case ESTABLISH_CONNECTION:
        }

    }
}
