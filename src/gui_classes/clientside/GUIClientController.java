package gui_classes.clientside;

import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.*;

import javax.lang.model.type.ArrayType;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The GUIClientController class is the controller class for the GUI client. It handles the login process and the sending
 * and receiving of messages
 */
public class GUIClientController implements ActionListener {

    private GUIClientFrame frame;
    private final Socket server;
    private ObjectInputStream input;
    public static ObjectOutputStream output;
    User user;
    ImageIcon icon = new ImageIcon("res/appLogo.png");

    public GUIClientController(Socket server) {
        this.server = server;
        this.run();
    }

    /**
     * It creates a login form, sends the login credentials to the server, and waits for the server to respond with a User
     * object. If the server responds with a User object, then the client is logged in. If the server responds with a
     * Message object, then the client is not logged in and the message is displayed to the user
     */
    public void run() {
        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());

            boolean loggedIn = false;
            logAuth:
            while (!loggedIn) {

                // Prompt the user to enter their username and password
                LoginGUIForm log = new LoginGUIForm(frame, output);

                // Wait for the server to respond with a User object

                Object obj = input.readObject();
                if (obj instanceof JOptionPane j) {
                    JOptionPane.showMessageDialog(new JFrame(), j.getMessage().toString());
                    continue logAuth;
                }
                else if (obj instanceof User) {
                    log.dispose();
                    user = (User) obj;
                    System.out.println("YOU HAVE LOGGED IN AS: " + user.getName());

                    loggedIn = true;
                } else if (obj instanceof Message) {
                    //login error message from server
                    Message message = (Message) obj;
                    JOptionPane.showMessageDialog(frame, message.getContent(), "Login Error", JOptionPane.ERROR_MESSAGE);
                }
                System.out.println();
            }

            // Create the GUI frame
            frame = new GUIClientFrame(this, user, output);
            frame.setIconImage(icon.getImage());
            frame.appendMessage("CONNECTED TO SERVER "+server.getLocalAddress()+" PORT "+server.getPort());
            frame.appendMessage("WELCOME TO THE CHATROOM, "+user.getName());

            // Start a listener thread to receive messages from the server
            ServerMessageListener listener = new ServerMessageListener();
            listener.start();
            frame.setVisible(true);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }  catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }/*finally {
            XMLParse.setStatusOfUser(user.getUsername(), "offline");
        }*/
    }

    public void updateUserStatus() {

    }

    /**
     * If the message starts with a command, the client will check if the command is valid and if it is, it will send the
     * message to the server
     */
    public void sendMessage() throws ParserConfigurationException, IOException, SAXException {
        // Get the message from the input field
        Message msg = null;
        String message = frame.getMessageText();

        if(message.startsWith("/")){
            String[] words = message.split("[/\\s]+");
            String command = words[1];

            switch (command){


                case "pm":
                    // syntax -> /pm <recipient name> <message content>

                    String recipient = words[2].toLowerCase(Locale.ROOT);
                    if(Server.getRegisteredUserNames().contains(recipient)){
                        String messageContent = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
                        msg = new Message(user.getName(), recipient, messageContent);
                        frame.appendMessage("[PRIVATE @"+recipient+" ] YOU: "+messageContent);
                        break;
                    }
                    else{
                        frame.appendMessage("[ERROR] user "+ recipient+" does not exist.");
                        break;
                    }
                case "gm":
                    //  syntax -> /gm <group name> <message content>

                    String group = words[2].toLowerCase(Locale.ROOT);

                    if(!Server.getGroups().contains(group)) {
                        frame.appendMessage("[ERROR] group "+group+" does not exist.");
                        break;
                    }
                    //check muna if user is not member of said group
                    else if(!user.isMember(group)){
                        frame.appendMessage("[ERROR] you are not a member of the group: "+group);
                        break;
                    }
                    else{
                        //send msg to grp
                        String messageContent = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
                        msg = new Message(user.getName(),"@"+group, messageContent);
                        break;
                    }


                case "quit":
                    System.exit(0);
                    break;
                default:
                    msg = null;
                    frame.appendMessage("[ERROR] Command '"+command+ "' not recognized..");
                    break;
            }
        }
        else{
            //broadcast msg
            msg = new Message(user.getName(), "TOALL", message);
        }

        // Send the message to the server
        if (msg != null) {
            try {
                output.reset();
                output.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Clear the input field
        frame.clearMessageText();
    }

    public void showFrame() {
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * It listens for incoming messages from the server and displays them in the chat window
     */
    public class ServerMessageListener extends Thread {
        @Override
        public void run() {
            try {
                while (input != null) {
                    Object obj = input.readObject();

                    if (obj instanceof Message) {

                        // Handle incoming message
                        Message msg = (Message) obj;

                        if(msg.getRecipient() == null){
                            continue;
                        }

                        //broadcast msg
                        else if(msg.getRecipient().equals("TOALL")){
                            frame.appendMessage("[BROADCAST] "+msg.getSender()+": "+msg.getContent());
                        }

                        //group msg
                        else if(msg.getRecipient().startsWith("@")){
                            frame.appendMessage("[GROUP "+msg.getRecipient()+" ] "+msg.getSender()+": "+msg.getContent());
                        }

                        //private msg
                        else{
                            frame.appendMessage("[PRIVATE] "+msg.getSender()+": "+msg.getContent());
                        }

                        //debug statement
                        System.out.println(msg.getSender()+": " + msg.getContent());
                    }

                    //load offline/unread messages sent to the user
                    else if(obj instanceof List<?>){
                        List<?> list = (List<?>) obj;
                        if (!list.isEmpty() && list.get(0) instanceof OfflineMessage) {
                            List<OfflineMessage> offlineMessages = (List<OfflineMessage>) list;
                            for (OfflineMessage offlineMessage : offlineMessages) {
                                String sender = offlineMessage.getSender();
                                String content = offlineMessage.getContent();
                                frame.appendMessage("[UNREAD MSG] " + sender + ": " + content);
                            }
                        }
                        // update members list if sino kakaonline hehe
                        if (!list.isEmpty() && list.get(0) instanceof String) {
                            ArrayList<String> arrayList1 = (ArrayList<String>) list;
                            frame.updateStats(arrayList1);
                        }
                    }
                    // Handles:
                    //      Leaving a group
                    //      Other dialog/pop up messages
                    else if (obj instanceof JOptionPane j) {
                        JOptionPane.showMessageDialog(new JFrame(), j.getMessage());
                        frame.updateGroupsTab();
                    }

                    // Update Groups tab when user is added to a group
                    else if (obj instanceof Group group) {
                        if (!group.getAdmin().getUsername().equals(user.getUsername())) {
                            JOptionPane.showMessageDialog(new JFrame(), "You have been added to group " + group.getName());
                        }
                        frame.updateGroupsTab();
                        System.out.println("groups tab updated");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
