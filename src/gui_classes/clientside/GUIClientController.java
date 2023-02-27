package gui_classes.clientside;

import shared_classes.LoginCredentials;
import shared_classes.Message;
import shared_classes.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class GUIClientController extends JFrame implements ActionListener{

    private JTextArea messageArea;
    private JTextField messageInput;
    private JButton sendButton;
    private JTextPane messagePane;
    static Scanner scanny = new Scanner(System.in);
    User user;

    // Declare your input and output streams
    private static ObjectInputStream input;
    private static ObjectOutputStream output;

    // Declare your server socket and client socket
    static Socket server;


    public GUIClientController(Socket s) throws IOException, ClassNotFoundException {
        this.server = s;
        input = new ObjectInputStream(server.getInputStream());
        try {
            output = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setVisible(false);

        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        // Initialize the components
        messagePane = new JTextPane();
        messagePane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messagePane);
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

        boolean loggedIn = false;
        while (!loggedIn) {
            // Prompt the user to enter their username and password
            String username = JOptionPane.showInputDialog("Enter your username:");
            String password = JOptionPane.showInputDialog("Enter your password:");

            // Create a login message and send it to the server
            LoginCredentials loginMessage = new LoginCredentials(username, password);
            output.writeObject(loginMessage);
            System.out.println("HELLO BRO");

            if(input != null){
                //System.out.println("HELLOasdASDD");
                // Wait for response from server
                Object obj = input.readObject();
                if (obj instanceof User) {
                    // login successful yeahhh
                    user = (User) obj;
                    System.out.println("YOU HAVE LOGGED IN AS: "+user.getName());

                    loggedIn = true;
                }
                else if (obj instanceof Message) {
                    //login error message ipriprint ng server sa client
                    Message message = (Message) obj;
                    System.out.println(message.getContent());
                    loggedIn = false;
                }else {
                    JOptionPane.showMessageDialog(this, "Incorrect username or password. Please try again.");
                }
            }
        }

        //after log in is successful, makikita nayung main GUI
        this.setVisible(true);
    }


    private void sendMessage() {
        // Get the message from the input field
        Message msg = null;
        String message = messageInput.getText();


        if(message.startsWith("/")){
            String[] words = message.split("[/\\s]+");
            String command = words[1];

            switch (command){

                //yung pm syntax -> /pm ARIEL Hello ariel! hehe

                //message object parin gagawin, pero yung recipient is hindi "toall"
                case "pm":

                    String recipient = words[2];
                    String messageContent = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
                    System.out.println("ASd   " + messageContent);
                    msg = new Message(user.getName(), recipient, messageContent);
                    //System.out.println(msg);
                    break;
            }

        }

        else{
            //if walang command, send yung message object sa server as message object parin pero "toall" yung
            //recipient which means ibrobroadcast yung message
            msg = new Message(user.getName(), "TOALL", message);
        }

        // Send the message to the server
        try {
            output.reset();
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Clear the input field
        messageInput.setText("");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        int port;
        String hostName;
        String userName;
        server = null;
        hostName = "localhost";

        System.out.print("INPUT PORT: ");
        port = Integer.parseInt(scanny.nextLine());

        System.out.println("HINDI NAG POPOP UP PERO MAY LUMABAS NA JOPTION PANE");
        System.out.println("ALT TAB NIYO NALANG, HINDI PA MAAYOS LOG IN KAYA");
        System.out.println("ETO LANG PANGTRY NIYO PANG LOG IN:");
        System.out.println();
        System.out.println("asd:asd - darren");
        System.out.println("123:123 - rey");
        System.out.println("xxx:xxx - joshua");
        System.out.println("lol:lol - ariel");
        System.out.println();
        System.out.println("PAG MALI LOG IN, NAG EERROR EWAN KO KUNG BAKIT");
        System.out.println("yung pm ganito yung syntax -> /pm Ariel hello ariel its a me");
        try{
            server = new Socket(hostName, port);
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // Create a new instance of the controller
        GUIClientController controller = new GUIClientController(server);
        ServerMessageListener listener = controller.new ServerMessageListener();
        listener.start();

        // Show the frame
        controller.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
    private class ServerMessageListener extends Thread {

        @Override
        public void run() {
            System.out.println("HELO");
            try {
/*                input = new ObjectInputStream(server.getInputStream());*/
                // Continuously listen for messages from the server
                while (input != null) {
                    Object obj = input.readObject();
                    if (obj instanceof Message) {
                        // Handle incoming message

                        Message msg = (Message) obj;

                        if(msg.getRecipient().equals("TOALL")){
                            messagePane.setText(messagePane.getText()+"\n"+"[BROADCAST] "+msg.getSender()+": "+msg.getContent());
                        }
                        else{
                            messagePane.setText(messagePane.getText()+"\n"+"[PRIVATE] "+msg.getSender()+": "+msg.getContent());
                        }
                        System.out.println(msg.getSender()+": " + msg.getContent());
                    }
                    else{
                        System.out.println("WHATTTT");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}