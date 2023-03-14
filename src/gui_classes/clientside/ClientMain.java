package gui_classes.clientside;

import shared_classes.XMLParse;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientMain {

    static Socket server;

    public static void main(String[] args) {
        int port = 0;
        String hostName;
        server = null;
        hostName = "localhost";

        while (true) {
            try {
                boolean validPort = false;
                while (!validPort) {
                    JFrame f = new JFrame();
                    f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    port = Integer.parseInt(JOptionPane.showInputDialog(f, "Input port: ", "Port connection", JOptionPane.INFORMATION_MESSAGE));
                    hostName = JOptionPane.showInputDialog(f, "Input host: ", "Port connection", JOptionPane.INFORMATION_MESSAGE);
                    validPort = true;
                }

                if (port != 0) {
                    server = new Socket(hostName, port);
                    System.out.println("Connected to port: " + port);

                    GUIClientController controller = new GUIClientController(server);

                    controller.showFrame();
                    break;
                } else {
                    System.exit(0);
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Could not connect to server");
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(new JFrame(), "Could not connect idjay server. Please check the pldt or if u input tamang port/host and try manen.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}