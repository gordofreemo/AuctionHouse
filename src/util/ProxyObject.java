package util;

import java.io.Serializable;

/*
    Represents an object that the Agent, Bank, Auction House will communicate with
    instead of directly sending messages over the network.
    Instead, the client classes interact with the proxy, which takes care
    of the network communication.
 */
public abstract class ProxyObject implements Observable {
    private Observer observer;


    /*
    Idea: All proxies need a way to communicate with their "parent" objects,
    i.e. the Bank proxy needs to send messages to the bank.
    Private because the client using the proxy should not need to even know
    that it is communicating over a network.
     */
    abstract protected void messageHost();

    /*
    For a start, the idea is when opening a connection initially, both
    parties will send one another proxy objects. The proxy object will
    then need to connect again with the host.
    This can change if we just pass a socket to the Proxy, but we'll see.
     */
    abstract public void makeConnection();

    @Override
    public void registerObserver(Observer observer) {
        this.observer = observer;
    }

    /*
    In the future, can either have the proxies override this method
    or make some sort of standard update type.
    However, most likely since the kinds of updates that the bank might receive
    from the auction house are different from the updates the agent receives
    from auction house, this will likely need to change.
     */
    @Override
    public void alertObservers() {
        observer.handleUpdate(null);
    }
}
