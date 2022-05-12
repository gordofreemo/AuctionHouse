package util;
import java.io.Serializable;
import util.MessageEnums.*;

public class Message implements Serializable {
    private Origin origin = null;
    private Type type = null;
    private String body = null;
    private Object info = null;

    /**
     * Constructor for a message
     * @param origin where the message came from
     * @param type what type of command is being sent
     * @param body any extra string info depending on type
     */
    public Message(Origin origin, Type type, String body) {
        this.origin = origin;
        this.type = type;
        this.body = body;
    }

    /**
     * Override of default to string message
     */
    @Override
    public String toString() {
        return "Message from " + origin + " with type " + type + " and body " + body;
    }

    /**
     * gets the type enum
     * @return type enum
     */
    public Type getType() {
        return type;
    }

    /**
     * gets the info object
     * @return info object
     */
    public Object getInfo() {
        return info;
    }

    /**
     * gets the body string
     * @return body string
     */
    public String getBody() {
        return body;
    }

    /**
     * gets the origin enum
     * @return origin enum
     */
    public Origin getOrigin() {
        return origin;
    }

    /**
     * sets the body string
     * @param body string
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * sets the type enum
     * @param type Type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * sets the info object
     * @param info object
     */
    public void setInfo(Object info) {
        this.info = info;
    }
}
