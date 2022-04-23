import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class KnockKnock implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out ;
    private BufferedReader in ;
    private State state = State.WAITING ;
    private int currentJoke = 0;

    private static String BYE = "Bye.";
    private enum State {
        WAITING,
        SENT_KNOCK_KNOCK,
        SENT_CLUE,
        ANOTHER
    }
    private static String [] clues = {
        "Turnip",
        "Little Old Lady",
        "Atch", "Who", "Who"
    };
    private static String [] answers = {
        "Turnip the heat, it's cold in here!",
        "I didn't know you could yodel!",
        "Bless you!",
        "Is there an owl in here?",
        "Is there an echo in here?"
    };
    
    public KnockKnock(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run () {
        String inputLine = null;
        String outputLine;

        do {
            outputLine = processInput(inputLine);
            out.println(outputLine);
            if (outputLine.equals(BYE)) {
                break;
            }
            try {
                inputLine = in.readLine();
            } catch (IOException ex) {
                inputLine = null;
            }
        } while (inputLine != null);
    }

    private String processInput ( String input ) {
        String output = null;
        switch (state) {
            case WAITING:
                output = "Knock! Knock!";
                state = State.SENT_KNOCK_KNOCK;
                break;
            case SENT_KNOCK_KNOCK:
                if (input.equalsIgnoreCase ("Who's there?")) {
                output = clues[currentJoke];
                state = State.SENT_CLUE;
                } else {
                    output =
                    "You're supposed to say \"Who ’s there ?\"! " +
                    "Try again. Knock! Knock!";
                }
                break ;
            case SENT_CLUE:
                if (input.equalsIgnoreCase(clues[currentJoke] + " who?")) {
                    output = answers[currentJoke] +
                    " Want another? (y/n)";
                    state = State.ANOTHER;
                } else {
                    output = "You're supposed to say \"" +
                    clues[currentJoke] + "who?\"! " +
                    "Try again. Knock! Knock!";
                    state = State.SENT_KNOCK_KNOCK;
                }
                break ;
            case ANOTHER :
                if (input.equalsIgnoreCase ("y")) {
                    output = "Knock! Knock!";
                    currentJoke = (currentJoke + 1) % clues.length;
                    state = State.SENT_KNOCK_KNOCK;
                } else {
                    output = BYE;
                    state = State.WAITING;
                }
                break;
            }
            return output ;
    }
}

