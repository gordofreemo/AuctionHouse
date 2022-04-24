package util;

/*
Simple observer pattern implementation.
The idea is that the proxy objects may need to alert their client of something
happening, for example the agent wins a bid, and the auction house wants to
alert the agent. Thus, it might easy for the proxies to be observable
so that they can update the client with this information.
 */
public interface Observable {
    void registerObserver(Observer observer);
    void alertObservers();
}
