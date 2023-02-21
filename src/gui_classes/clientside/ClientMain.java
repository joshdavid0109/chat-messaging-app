package gui_classes.clientside;

import shared_classes.User;

import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain implements Runnable{

    private Socket socket;
    User user;
    private PrintWriter printWriter;

    public ClientMain(Socket socket, User user, PrintWriter printWriter) {
        this.socket = socket;
        this.user = user;
        this.printWriter = printWriter;
    }

    public void run() {
        new Frame(socket,user,printWriter);
    }
}
