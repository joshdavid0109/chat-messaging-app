package gui_classes.clientside;

import shared_classes.User;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ClientMain implements Runnable{
    User user;
    private Socket socket;
    Frame frame;

    public ClientMain(User user, Socket socket) {
        this.user = user;
        this.socket = socket;
    }

    public void run() {
        try {
            this.frame = new Frame(user, socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
