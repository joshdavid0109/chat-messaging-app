package shared_classes;

import java.awt.*;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

/*public record User(String id, String name, String age, String username, String password, String status, String banStatus) {


    @Override
    public String toString() {
        return "User: " + name + "\nAge: " + age + "\nUsername: " + username;
    }
}*/

public class User {
    private String id;
    private PrintStream streamOut;
    private InputStream streamIn;
    private String name;
    private String age;
    private String username;
    private String password;
    private String status;
    private String banStatus;

    public User(String id, String name, String username){
        this.id = id;
        this.name = name;
        this.age = age;
        this.username = username;
    }

    public PrintStream getOutStream(){
        return this.streamOut;
    }

    public InputStream getInputStream(){
        return this.streamIn;
    }

    public String getName(){
        return this.name;
    }
}


