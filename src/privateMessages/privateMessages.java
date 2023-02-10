package privateMessages;


public class privateMessages {
    private final String recipient;
    private final String message;
    private final String sender;


    public privateMessages(String recipient, String message, String sender) {
        this.recipient = recipient;
        this.message = message;
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
