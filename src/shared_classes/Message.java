package shared_classes;

import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String recipient;
    private String content;

    public Message(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }
    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
    @Override
    public String toString() {
        return sender + ": " + content;
    }
}
