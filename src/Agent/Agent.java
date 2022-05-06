package Agent;

import AuctionHouse.AuctionHouse;

import java.net.UnknownHostException;
import java.util.List;
import AuctionHouse.*;
import util.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Agent {
    public String name;
    public long balance;
    public long avaliableBalance; // Balance - current bids
    public String accountNumber;
    public Boolean isConnected = false;
    public AgentToAuction proxy;

    public List<AuctionHouse> auctionHouses;

    /**
     * Build an agent
     * @param initBalance starting balance
     * @param name name of the agent (pulled from list?)
     */
    public Agent(long initBalance, String name){
        this.name = name;
        this.balance = initBalance;
        this.avaliableBalance = initBalance;

        // Send info to bank, receive account number
        String bankAddress = "";
        int bankPort = 0;
        /*try(Socket socket = new Socket(bankAddress, bankPort)) {


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } */

    }

    /**
     * Connects the agent with the auction house at
     * the given address and port number
     * @param hostAddress
     * @param hostPort
     */
    public void connectWithAH(String hostAddress, int hostPort){
        try(Socket socket = new Socket(hostAddress, hostPort)){
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            AuctionListener auctionListener = new AuctionListener(in, this);
            isConnected = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void makeBid(int amount, int auctionID){
        // proxy.makeBid(amount, auctionID);
    }

}
