package AuctionHouse;

import util.MessageEnums;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BankProxy {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int accountID;
    private final MessageEnums.Origin origin = MessageEnums.Origin.AUCTIONHOUSE;

    public BankProxy(ObjectInputStream in, ObjectOutputStream out, int id) {
        this.in = in;
        this.out = out;
        accountID = id;
    }


}
