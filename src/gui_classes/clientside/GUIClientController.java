package gui_classes.clientside;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GUIClientController implements ActionListener {

    private GUIClientFrame frame;
    private final Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    User user;
    ImageIcon icon = new ImageIcon("res/appLogo.png");

    public GUIClientController(Socket server) {
        this.server = server;
        this.run();
    }

    public void run() {
        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());

            boolean loggedIn = false;
            while (!loggedIn) {
                // Prompt the user to enter their username and password
                System.out.println("login ka na");
                LoginGUIForm log = new LoginGUIForm(frame);

                // Create a login message and send it to the server
                LoginCredentials loginMessage = new LoginCredentials(log.getUsername(), log.getPassword());
                output.writeObject(loginMessage);
                SetOnline(loginMessage);

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
            frame.setIconImage(icon.getImage());
            frame.appendMessage("CONNECTED TO SERVER "+server.getLocalAddress()+" PORT "+server.getPort());

            // Start a listener thread to receive messages from the server
            ServerMessageListener listener = new ServerMessageListener();
            listener.start();
            frame.setVisible(true);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse("res/users.xml");

                NodeList users = document.getElementsByTagName("User");

                for (int i = 0; i < users.getLength(); i++) {
                    Element element = (Element) users.item(i);

                    element.getElementsByTagName("status").item(0).setTextContent("offline");

                    Server.updateXML(users, document);
                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        }
    }

    private void SetOnline(LoginCredentials loginMessage) {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;
        NodeList nodelist = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse("res/users.xml");
            nodelist = document.getElementsByTagName("User");
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new RuntimeException(ex);
        }

        Element element;
            for(int i =0; i < nodelist.getLength();i++) {
                element = (Element) nodelist.item(i);
                String uname = element.getElementsByTagName("Username").item(0).getTextContent();
                String pass = element.getElementsByTagName("Password").item(0).getTextContent();
                if (uname.equals(loginMessage.getUsername()) && pass.equals(loginMessage.getPassword())) {
                    element.getElementsByTagName("status").item(0).setTextContent("online");
                    Server.updateXML(nodelist, document);
                    break;
                }
            }
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

                    System.out.println("A  "+Server.getRegisteredUserNames().toString());
                    System.out.println("B  "+recipient);
                    System.out.println("C  "+Server.getRegisteredUserNames().contains(recipient));

                    if(Server.getRegisteredUserNames().contains(recipient)){
                        String messageContent = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
                        msg = new Message(user.getName(), recipient, messageContent);
                        break;
                    }
                    else{
                        msg = new Message(user.getName(), recipient, message);
                        frame.appendMessage("[ERROR] user "+ recipient+" does not exist.");
                        break;
                    }
                case "gm":
                    String group = words[2].toLowerCase(Locale.ROOT);

                    System.out.println("G  "+Server.getGroups());
                    System.out.println("H  "+group);
                    System.out.println("I  "+Server.getGroups().contains(group));

                    if(Server.getGroups().contains(group)){

                        //gm REAL hello guys

                        String messageContent = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
                        msg = new Message(user.getName(),"@"+group, messageContent);
                        break;
                    }
                    else{
                        msg = new Message(user.getName(),"@"+ group, message);
                        frame.appendMessage("[ERROR] group "+ group+" does not exist.");
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

    @Override
    public void actionPerformed(ActionEvent e) {

    }

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
                        else if(msg.getRecipient().equals("TOALL")){
                            frame.appendMessage("[BROADCAST] "+msg.getSender()+": "+msg.getContent());
                        }
                        else if(msg.getRecipient().startsWith("@")){
                            frame.appendMessage("[GROUP] "+msg.getSender()+": "+msg.getContent());
                        }
                        else{
                            frame.appendMessage("[PRIVATE] "+msg.getSender()+": "+msg.getContent());
                        }
                        System.out.println(msg.getSender()+": " + msg.getContent());
                    }
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
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
