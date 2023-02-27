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
    private transient Socket socket;

    public User(String id, String name, String username, Socket socket) throws IOException {
        this.id = id;
        this.name = name;
        this.username = username;
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
}
