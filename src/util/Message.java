package util;
import java.io.Serializable;
import util.MessageEnums.*;

public class Message implements Serializable {
    public Origin origin = null;
    public Type type = null;
    public String body = null;
    public Object proxy = null;

    public Message(Origin origin, Type type, String body) {
        this.origin = origin;
        this.type = type;
        this.body = body;
    }

    public Origin getOrigin() {
        return origin;
    }

    public Type getType() {
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

    @Override
    public String toString() {
        return "Message from " + origin + " with type " + type + " and body " + body;
    }
}
