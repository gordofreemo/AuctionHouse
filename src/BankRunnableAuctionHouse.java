import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BankRunnableAuctionHouse implements Runnable{
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private State state = State.WAITING;
    
    private enum State {
        WAITING,
        SENT_KNOCK_KNOCK,
        SENT_CLUE,
        ANOTHER
    }

    public BankRunnableAuctionHouse(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
}
