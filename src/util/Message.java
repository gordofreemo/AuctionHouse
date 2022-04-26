package util;
import java.io.Serializable;
import util.MessageEnums.*;

public class Message implements Serializable {
    public Origin origin = null;
    private String type = null;
    private String body = null;
    private Object proxy = null;

    public Message(Origin origin, String type, String body) {
        this.origin = origin;
        this.type = type;
        this.body = body;
    }

    public Origin getOrigin() {
        return origin;
    }

    public String getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }
}
