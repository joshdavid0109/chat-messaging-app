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
    private final PrintWriter printWriter;
    public static List<Frame> frameList = new ArrayList<>();

    public ClientMain(Socket socket, User user, PrintWriter printWriter) {
        this.socket = socket;
        this.user = user;
        this.printWriter = printWriter;
    }

    public void run() {
            new Thread(()-> {
                try {
                    frameList.add(new Frame(user, socket, printWriter));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
    }
}
