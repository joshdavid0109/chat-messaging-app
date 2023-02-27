package shared_classes;

import java.io.Serializable;

public class ServerMessage implements Serializable {
    private String message;

    public ServerMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return message;
    }
}
