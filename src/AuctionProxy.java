import util.ProxyObject;

/*
It seems like the bank does not have a lot of interaction with the AH,
so it might be possible to just have the proxy with the agent, but time will tell.
 */
public class AuctionProxy extends ProxyObject {

    // rough draft
    public void makeBid(int bid) {}

    // rough draft
    public void getCurrentItems() {}

    @Override
    protected void messageHost() {
        // implement in future
    }

    @Override
    public void makeConnection() {
        // implement in future
    }
}
