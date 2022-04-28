package AuctionHouse;

public class AgentProxy {
    private AgentHandler handler;
    private AgentListener listener;

    public AgentProxy(AgentHandler handler, AgentListener listener) {
        this.handler = handler;
        this.listener = listener;
    }
}
