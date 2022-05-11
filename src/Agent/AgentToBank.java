package Agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Message;
import util.MessageEnums.*;

import java.util.*;

public class AgentToBank {
    private ObjectOutputStream out;
    private bankListener listener;
    private int agentID;
    private List<String> auctionConnects;

    public AgentToBank(Message info, String name, int port) throws IOException{
        Socket bankSocket = new Socket(name, port);
        out = new ObjectOutputStream(bankSocket.getOutputStream());
        listener = new bankListener(new ObjectInputStream(bankSocket.getInputStream()));
        new Thread(listener).start();

        System.out.println("Sending initial message to bank");
        out.writeObject(info);

    }

    public int getId(){ return agentID; }

    public List<String> getAuctionConnects(){ return auctionConnects; }


    /**
     * Take the string that bank sent on init and make a list of the
     * auction house's address/port number. Start at string[1] since 0 is agent id
     * @param info
     */
    private void parseAuctionConnects(String[] info){
        auctionConnects = new ArrayList<>();
        for(int i = 1; i < info.length; i++){
            auctionConnects.add(info[i]);
        }
    }

    /**
     * Listen to messages from the bank indefinitely
     */
    private class bankListener implements Runnable{
        private ObjectInputStream in;

        public bankListener(ObjectInputStream in ){
            this.in = in;
        }


        @Override
        public void run() {
            while(true){
                try {
                    Message inMsg = (Message) in.readObject();
                    System.out.println(inMsg);

                    switch (inMsg.getType()){
                        case ESTABLISH_CONNECTION -> {
                            String info = inMsg.getBody();
                            String[] infoSplit = info.split("\n");
                            agentID = Integer.parseInt(infoSplit[0]);
                            parseAuctionConnects(infoSplit);
                        }

                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
