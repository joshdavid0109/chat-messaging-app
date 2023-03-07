package shared_classes;

import java.io.Serializable;

public class errorMessage implements Serializable {
    private String message;

    errorMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
