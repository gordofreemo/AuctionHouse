package util;
import java.io.Serializable;
import java.util.List;

import AuctionHouse.Item;
import util.MessageEnums.*;

public class Message implements Serializable {
    private Origin origin = null;
    private Type type = null;
    private String body = null;
    private Object info = null;

    public Message(Origin origin, Type type, String body) {
        this.origin = origin;
        this.type = type;
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message from " + origin + " with type " + type + " and body " + body;
    }

    public Type getType() {
        return type;
    }

    public Object getInfo() {
        return info;
    }

    public String getBody() {
        return body;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setInfo(Object info) {
        this.info = info;
    }
}
