package gui_classes.clientside;

import shared_classes.Message;
import shared_classes.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GUIClientController extends JFrame {

    private JTextArea messageArea;
    private JTextField messageInput;
    private JButton sendButton;
    static BufferedReader input;
    static ObjectOutputStream output;
    static Scanner scanny = new Scanner(System.in);
    Socket server;
    User user;

    public GUIClientController(Socket s, User u) {
        this.server = s;
        this.user = u;

        // Initialize the frame
        System.out.println("CONNECTED TO "+server.getLocalAddress());
        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        // Initialize the components
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setPreferredSize(new Dimension(350, 200));

        messageInput = new JTextField();
        messageInput.setPreferredSize(new Dimension(250, 25));

        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 25));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle button click event
                sendMessage();
            }
        });

        // Add the components to the frame
        setLayout(new FlowLayout());
        add(scrollPane);
        add(messageInput);
        add(sendButton);

        try{
            input = new BufferedReader(new InputStreamReader(server.getInputStream()));
            output = new ObjectOutputStream(server.getOutputStream());
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private void sendMessage() {
        // Get the message from the input field
        String message = messageInput.getText();

        Message msg = new Message(user.getName(), "BRO", message);

        // Send the message to the server
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Clear the input field
        messageInput.setText("");
    }

    public static void main(String[] args) {
        int port;
        String hostName;
        Socket server = null;
        System.out.print("INPUT PORT: ");
        port = Integer.parseInt(scanny.nextLine());
        hostName = "localhost";

        try{
            server = new Socket(hostName, port);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


        //DITO LOG IN MUNAXXXX
        //HARDCODE BITCH
        User u = new User("69", "DARREN", "@franzxsu");
        // Create a new instance of the controller
        GUIClientController controller = new GUIClientController(server, u);

        // Show the frame
        controller.setVisible(true);
    }
}
