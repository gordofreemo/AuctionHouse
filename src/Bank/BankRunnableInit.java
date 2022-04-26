package Bank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BankRunnableInit implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public BankRunnableInit(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        String inputLine = null;

        try {
            in.readLine();
        } catch (IOException ex) {
            inputLine = null;
        }

        // if (input = "agent") {
            // BankRunnableAgent kk = new BankRunnableAgent(clientSocket);
            // Thread t = new Thread(kk);
            // t.start();
        // }
        // else if (input = "auction house") {
            // BankRunnableAuctionHouse kk = new BankRunnableAuctionHouse(clientSocket);
            // Thread t = new Thread(kk);
            // t.start();
        // }
    }
}
