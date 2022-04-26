package Examples;
import java.net.*;
import java.io.*;

public class KnockKnockClient {
    public static void main ( String [] args ) throws IOException {
        String hostName = "127.0.0.1";
        int portNumber = 22557;
        try (Socket socket = new Socket(hostName, portNumber );
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream ()));
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer = "";
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.equals("Bye.")) { break; }
                String fromUser = stdIn.readLine();

                if (fromUser != null) {
                    // System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        }
    }
}
