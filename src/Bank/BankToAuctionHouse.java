package Bank;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.Message;
import util.MessageEnums.*;

public class BankToAuctionHouse implements Runnable {
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int id;
    private String name;
    private String desc;

    public BankToAuctionHouse(Socket clientSocket) throws IOException {
        // Register thread with bank state tracker
        BankState.getInstance().addAuctionHouseThread(this);

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
            case ESTABLISH_CONNECTION: //send id back

                break;
            case MAKE_BID:
                String[] body = msg.getBody().split("\n");
                int amount = Integer.parseInt(body[0]);
                int id = Integer.parseInt(body[1]);
                BankState.getInstance().blockFunds(amount, id);
                break;
            case BID_OUTBID:
                break;
            case CLOSE_CONNECTION:
                break;
            default: break;
        }
    }

    private void initHouse(String body) {
        String[] msg = body.split("\n");
        name = msg[0];
        id = BankState.getInstance().getNewId();
    }

    public String getAddress() {
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
}
