package gui_classes.clientside;

import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.LoginCredentials;
import shared_classes.Message;
import shared_classes.User;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Locale;

public class GUIClientController implements ActionListener, Runnable {

    private GUIClientFrame frame;
    private final Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    User user;

    public void run() {
        try {
            // Create streams to communicate with the server
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());

            boolean loggedIn = false;
            while (!loggedIn) {
                // Prompt the user to enter their username and password
                LoginGUIForm log = new LoginGUIForm(frame);

                // Create a login message and send it to the server
                LoginCredentials loginMessage = new LoginCredentials(log.getUsername(), log.getPassword());
                output.writeObject(loginMessage);

                // Wait for the server to respond with a User object
                Object obj = input.readObject();
                if (obj instanceof User) {
                    user = (User) obj;
                    System.out.println("YOU HAVE LOGGED IN AS: " + user.getName());
                    user.printGroups();
                    loggedIn = true;
                } else if (obj instanceof Message) {
                    //login error message from server
                    Message message = (Message) obj;
                    JOptionPane.showMessageDialog(frame, message.getContent(), "Login Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Incorrect username or password. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Create the GUI frame
            frame = new GUIClientFrame(this, user);

            // Start a listener thread to receive messages from the server
            ServerMessageListener listener = new ServerMessageListener();
            listener.start();

            // Show the frame
            frame.setVisible(true);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public GUIClientController(Socket server) {
        this.server = server;
        this.run();
    }

    public void sendMessage() throws ParserConfigurationException, IOException, SAXException {
        // Get the message from the input field
        Message msg = null;
        String message = frame.getMessageText();

        if(message.startsWith("/")){
            String[] words = message.split("[/\\s]+");
            String command = words[1];

            switch (command){

                //yung pm syntax -> /pm ARIEL Hello ariel! hehe
                //message object parin gagawin, pero yung recipient is hindi "toall"

                case "pm":
                    String recipient = words[2].toLowerCase(Locale.ROOT);
                    if(Server.getRegisteredUserNames().contains(recipient)){
                        String messageContent = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
                        msg = new Message(user.getName(), recipient, messageContent);
                        break;
                    }
                    else{
                        msg = new Message(user.getName(), recipient, message);
                        //messagePane.setText(messagePane.getText()+"\n"+"[ERROR] user "+ recipient+" does not exist.");
                        frame.appendMessage("[ERROR] user "+ recipient+" does not exist.");
                        break;
                    }
                case "quit":
                    System.exit(0);
                default:
                    msg = new Message("NOTHING");
                    //messagePane.setText(messagePane.getText()+"\n"+"[ERROR] error in parsing message -> command not recognized???");
                    //frame.appendMessage("[ERROR] user "+ recipient+" does not exist.");
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
        frame.clearMessageText();
    }

    /*public void setUserName(String userName) {
        this.userName = userName;
        frame.setTitle(userName);
    }*/

    public void showFrame() {
        frame.setVisible(true);
    }

    /*public String getUsername() {

    }*/

    public class ServerMessageListener extends Thread {
        @Override
        public void run() {
            System.out.println("server listener started!!");
            try {
                while (input != null) {
                    System.out.println(input.readObject());
                    Object obj = input.readObject();
                    if (obj instanceof Message) {
                        // Handle incoming message
                        Message msg = (Message) obj;
                        if(msg.getRecipient() == null){
                            continue;
                        }
                        else if(msg.getRecipient().equals("TOALL")){
                            frame.addMessage("[BROADCAST] "+msg.getSender()+": "+msg.getContent());
                        }
                        else{
                            frame.addMessage("[PRIVATE] "+msg.getSender()+": "+msg.getContent());
                        }
                        System.out.println(msg.getSender()+": " + msg.getContent());
                    }
                    /*else if(obj instanceof List<?>){
                        List<?> list = (List<?>) obj;
                        if (!list.isEmpty() && list.get(0) instanceof OfflineMessage) {
                            List<OfflineMessage> offlineMessages = (List<OfflineMessage>) list;
                            for (OfflineMessage offlineMessage : offlineMessages) {
                                String sender = offlineMessage.getSender();
                                String content = offlineMessage.getContent();
                                frame.addMessage("[PRIVATE] " + sender + ": " + content);
                            }
                        }
                    }*/
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("send")) {
            //String recipient = frame.getSelectedUser();
            String recipient = "ariel";
            String content = frame.getMessageText();
            if (recipient.equals("")) {
                JOptionPane.showMessageDialog(frame, "Please select a recipient", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (content.equals("")) {
                JOptionPane.showMessageDialog(frame, "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    sendMessage();
                } catch (ParserConfigurationException | IOException | SAXException ex) {
                    ex.printStackTrace();
                }
                frame.clearMessageText();
            }
        } /*else if (command.equals("set-username")) {
            String userName = frame.getUserName();
            if (userName.equals("")) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                setUserName(userName);
            }
        }*/
    }
}
