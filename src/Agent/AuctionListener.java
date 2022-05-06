package Agent;

import util.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class AuctionListener implements Runnable {
    private ObjectInputStream in;
    private Agent agent;

    AuctionListener(ObjectInputStream in, Agent agent){
        this.in = in;
        this.agent = agent;
    }

    @Override
    public void run() {
        try{
            while(true){
                Message inMsg = (Message) in.readObject();
                System.out.println(inMsg);
                switch(inMsg.getType()){
                    case SEND_ITEMS -> System.out.println("Do Something");
                    case BID_SUCCESS -> System.out.println("Do something");
                    case BID_WIN -> System.out.println("Do something");
                    case BID_FAILED -> System.out.println("Do something");
                    case BID_OUTBID -> System.out.println("Do something");
                }
            }
        }catch (Exception e){}
    }
}
