package Bank;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Message;
import util.MessageEnums.*;

import java.util.*;

public class BankToAgent implements Runnable{
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String name;
    private int balance;
    private int id;

    public BankToAgent(Socket clientSocket) throws IOException {
        // Register thread with bank state tracker
        BankState.getInstance().addAgentThread(this);

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

        switch ((Type) msg.getType()) {
            case ESTABLISH_CONNECTION:
                initUser(msg.getBody());

                while (BankState.getInstance().getAuctionHouseThreads().size() == 0) {}

                List<String> auctionHouses = BankState.getInstance().getAuctionHouses();

                String addresses = "";
                for (var auctionHouse : auctionHouses) {
                    addresses += auctionHouse + "\n";
                }

                Message outMsg = new Message(
                    Origin.BANK,
                    Type.ESTABLISH_CONNECTION,
                    id + "\n" + addresses.trim()
                );

                try {
                    out.writeObject(outMsg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            default: break;
        }
    }

    private void initUser(String body) {
        String[] msg = body.split("\n");
        name = msg[0].split(":")[1];
        balance = Integer.parseInt(msg[1].split(":")[1]);
        id = BankState.getInstance().getNewId();
    }
}
