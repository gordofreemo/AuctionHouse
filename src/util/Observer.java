package util;

/*
Part of the observer pattern idea. Things like the Agent, Auction House, and
Bank might be observers of their proxy objects.
 */
public interface Observer {
    void handleUpdate(Object update);
}
