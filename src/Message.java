public class Message {
    String origin = null;
    String type = null;
    String body = null;
    Object proxy = null;

    public Message(String origin, String type, String body) {
        this.origin = origin;
        this.type = type;
        this.body = body;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }
}
