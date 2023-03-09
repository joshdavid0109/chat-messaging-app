package gui_classes.clientside;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientMain {

    static Socket server;

    public static void main(String[] args) {
        int port = 0;
        String hostName;
        String userName;
        server = null;
        hostName = "localhost";

        boolean validPort = false;
        while (!validPort) {
            port = Integer.parseInt(JOptionPane.showInputDialog(new JFrame(), "Input port: ", "Port connection", JOptionPane.INFORMATION_MESSAGE));
            hostName = JOptionPane.showInputDialog(new JFrame(),"Input host: ", "Port connection", JOptionPane.INFORMATION_MESSAGE);
            validPort = true;
        }

        if (port != 0) {
            try {
                server = new Socket(hostName, port);
                System.out.println("Connected to port: " + port);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            // Create a new instance of the controller
            GUIClientController controller = null;
            controller = new GUIClientController(server);
            GUIClientController.ServerMessageListener listener = controller.new ServerMessageListener();
            listener.start();

            // Show the frame
            controller.showFrame();
        } else {
            System.exit(0);
        }
    }
}
