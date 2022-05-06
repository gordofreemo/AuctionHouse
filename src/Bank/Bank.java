package Bank;
import java.io.IOException;
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
                System.out.println("New socket connection from " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new BankInit(clientSocket)).start();
            }
        }
    }
}
