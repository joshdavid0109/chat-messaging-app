package shared_classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private String id;
    private transient ObjectOutputStream streamOut;
    private transient ObjectInputStream streamIn;
    private String name;
    private String username;
    private String password;
    private String age;
    private String status;
    private String banStatus;

    public User(String id, String name,String age, String username, String password, String status, String banStatus) throws IOException {
        this.id = id;
        this.name = name;
        this.age = age;
        this.username = username;
        this.password = password;
        this.status = status;
        this.banStatus = banStatus;
    }

/*    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.streamOut = new ObjectOutputStream(socket.getOutputStream());
        this.streamIn = new ObjectInputStream(socket.getInputStream());
    }*/

    public ObjectOutputStream getOutStream() {
        return this.streamOut;
    }

    public ObjectInputStream getInputStream() {
        return this.streamIn;
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(String banStatus) {
        this.banStatus = banStatus;
    }
}
