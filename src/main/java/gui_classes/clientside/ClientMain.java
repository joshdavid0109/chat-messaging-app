package gui_classes.clientside;

import shared_classes.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientMain implements Runnable{

    private final Socket socket;
    User user;

    public static List<Frame> frameList = new ArrayList<>();

    public ClientMain(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }

    public void run() {
            new Thread(()-> {
                try {
                    frameList.add(new Frame(user, socket));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
    }
}
