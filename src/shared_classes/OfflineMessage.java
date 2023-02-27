package shared_classes;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OfflineMessage extends Message implements Serializable {
    private LocalDateTime timestamp;

    public OfflineMessage(String sender, String recipient, String content) {
        super(sender, recipient, content);
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + timestamp.toString() + "]";
    }
}
