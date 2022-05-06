package Bank;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Bank {
      public static void main(String [] args) throws IOException {
        int portNumber = 51362;
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            // Listen for new clients forever
            while (true) {
                // Create new thread to handle each client
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                System.out.println("New socket connection from " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new BankInit(clientSocket, out, in)).start();
            }
        }
    }
}
