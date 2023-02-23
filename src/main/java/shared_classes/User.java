package shared_classes;

import java.io.Serializable;

public record User(String id, String name, String age, String username, String password, String status, String banStatus) implements Serializable {


    @Override
    public String toString() {
        return "User: " + name + "\nAge: " + age + "\nUsername: " + username;
    }
}


