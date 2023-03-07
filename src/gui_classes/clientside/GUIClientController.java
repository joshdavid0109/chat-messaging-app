package gui_classes.clientside;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import server_side.UserManagement_GUI;
import shared_classes.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.List;

public class GUIClientController extends JFrame implements ActionListener{

    public final JList<String> contactList;
    public final ArrayList<String> bookmarkedContacts = new ArrayList<>();
    public final JLabel bookmarkedContactsLabel;
    private JTextField messageInput;
    private JButton sendButton;
    private JButton searchButton;
    private JTextPane messagePane;
    public static JButton bookmarkButton;
    public static JTextField pmTextField;
    static File f = new File("res/users.xml");
    static Scanner scanny = new Scanner(System.in);
    User user;


    // Declare your input and output streams
    private static ObjectInputStream input;
    private static ObjectOutputStream output;

    // Declare your server socket and client socket
    static Socket server;


    public GUIClientController(Socket s) throws IOException, ClassNotFoundException {
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 11);

        server = s;
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
        contactList = new JList<>(getAllContacts());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //contactList.addListSelectionListener(GUIClientController);

        // Initialize the components
        messagePane = new JTextPane();
        messagePane.setFont(font);
        messagePane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messagePane);
        scrollPane.setPreferredSize(new Dimension(350, 200));

        JLabel headerName = new JLabel();
        headerName.setForeground(Color.WHITE);
        headerName.setFont(new Font("Arial", Font.BOLD, 12));
        headerName.setBounds(920, -15, 200, 75);

        JLabel headerStatus = new JLabel("Status");
        headerStatus.setForeground(Color.GREEN);
        headerStatus.setFont(new Font("Arial", Font.BOLD, 10));
        headerStatus.setBounds(920, 0, 200, 75);

        JLabel broadCast = new JLabel("BROADCAST");
        broadCast.setForeground(Color.WHITE);
        broadCast.setFont(new Font("Arial", Font.BOLD, 18));
        broadCast.setBounds(100, 0, 200, 75);

        JLabel currentUserName = new JLabel("mag log in ka muna");
        currentUserName.setForeground(Color.WHITE);
        currentUserName.setFont(new Font("Arial", Font.BOLD, 18));
        currentUserName.setBounds(30, 0, 200, 75);

        JLabel currentUserStatus = new JLabel("Status");
        currentUserStatus.setForeground(Color.GREEN);
        currentUserStatus.setFont(new Font("Arial", Font.PLAIN, 18));
        currentUserStatus.setBounds(270, 0, 200, 75);

        JLabel listOfMembersName = new JLabel("MEMBERS");
        listOfMembersName.setForeground(Color.WHITE);
        listOfMembersName.setFont(new Font("Arial", Font.BOLD, 18));
        listOfMembersName.setBounds(90, 0, 200, 75);

        bookmarkButton = new JButton("Bookmark");
        bookmarkButton.setVisible(true);
        bookmarkButton.setForeground(Color.BLACK);
        bookmarkButton.setBackground(Color.WHITE);
        bookmarkButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        bookmarkButton.setFocusable(false);
        bookmarkButton.setBounds(40, 300, 100, 20);
        bookmarkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedContact = "";
                if (selectedContact.equals(contactList.getSelectedValue())) {
                    if (bookmarkedContacts.contains(selectedContact)) {
                        bookmarkedContacts.remove(selectedContact);
                        bookmarkButton.setText("Bookmark");
                    } else {
                        bookmarkedContacts.add(selectedContact);
                        bookmarkButton.setText("Unbookmark");
                    }
                    updateBookmarkedContactsLabel();
                    updateContactList(getAllContacts());
                }
            }
        });

        messageInput = new JTextField();
        messageInput.setVisible(true);
        messageInput.setBorder(BorderFactory.createEmptyBorder());
        messageInput.setBounds(30, 570, 220, 20);


        boolean loggedIn = false;
        while (!loggedIn) {
            // Prompt the user to enter their username and password
            LoginGUIForm log = new LoginGUIForm(this);

            UserManagement_GUI userManagementGui = new UserManagement_GUI();
            userManagementGui.populateList();
            // Create a login message and send it to the server
            LoginCredentials loginMessage = new LoginCredentials(log.getUsername(), log.getPassword());
            output.writeObject(loginMessage);
//            output.writeObject(f);

            if(input != null){
                Object obj = input.readObject();
                if (obj instanceof User) {
                    user = (User) obj;
                    System.out.println("YOU HAVE LOGGED IN AS: "+user.getName());
                    System.out.println(user.getGroups().toString());
                    loggedIn = true;
                    output.writeObject(f);
                } else if (obj instanceof Message) {
                    //login error message ipriprint ng server sa client
                    Message message = (Message) obj;
                    System.out.println(message.getContent());
                    loggedIn = false;
                } /*else if (obj instanceof File f) {
                 *//**
                 * parse to existing users.xml
                 *//*
                }*/ else {
                    JOptionPane.showMessageDialog(this, "Incorrect username or password. Please try again.");
                }
            }
        }
        headerName.setText(user.getUsername());
        currentUserName.setText(user.getName());

        JScrollPane scrollPaneListMembers = new JScrollPane(contactList);
        scrollPaneListMembers.setVisible(true);
        scrollPaneListMembers.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneListMembers.setBounds(40, 70, 200, 200);

        messagePane.setVisible(true);
        messagePane.setBorder(BorderFactory.createEmptyBorder());
        messagePane.setBounds(30, 70, 290, 470);

        JScrollPane broadcastArea = new JScrollPane();
        broadcastArea.setVisible(true);
        broadcastArea.getViewport().setForeground(new Color(0X23272A));
        broadcastArea.getViewport().setBackground(new Color(0X23272A));
        broadcastArea.setBorder(BorderFactory.createEmptyBorder());
        broadcastArea.setBounds(30, 70, 290, 470);

        JPanel header = new JPanel();
        header.setBackground(Color.BLACK);
        header.setBounds(0,0,1050, 50);
        header.setLayout(null);

        JPanel verticalHeader = new JPanel();
        verticalHeader.setBackground(Color.BLACK);
        verticalHeader.setBounds(0,50,50, 750);

        JPanel broadCastPanel = new JPanel();
        broadCastPanel.setBackground(new Color(0X23272A));
        broadCastPanel.setBounds(50,50,350, 750);
        broadCastPanel.setLayout(null);

        JPanel privateMessagePanel = new JPanel();
        privateMessagePanel.setBackground(new Color(0X2C2F33));
        privateMessagePanel.setBounds(400,50,350, 750);
        privateMessagePanel.setLayout(null);

        JPanel listOfMembers = new JPanel();
        listOfMembers.setBackground(new Color(0X23272A));
        listOfMembers.setBounds(750,50,400, 750);
        listOfMembers.setLayout(null);

        bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");
        bookmarkedContactsLabel.setVisible(true);
        bookmarkedContactsLabel.setForeground(Color.WHITE);
        bookmarkedContactsLabel.setBounds(40,250,200,200);

        // Add the components to the frame
        this.setLayout(new FlowLayout());
        this.add(headerName);
        this.add(headerStatus);
        this.add(broadCast);
        this.add(currentUserName);
        this.add(currentUserStatus);
        this.add(listOfMembersName);
        this.add(bookmarkButton);
        this.add(scrollPane);
        this.add(messageInput);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);
        //this.pack();
        this.setBounds(500, 250, 930, 540);
        this.setTitle("Budget Discord");
        this.setSize(1050, 750);
        this.getContentPane().setBackground(Color.WHITE);

        this.add(header);
        header.add(headerName);
        header.add(headerStatus);
        this.add(verticalHeader);
        this.add(broadCastPanel);
        this.add(privateMessagePanel);
        this.add(listOfMembers);
        listOfMembers.add(listOfMembersName);
        listOfMembers.add(scrollPaneListMembers);
        listOfMembers.add(bookmarkButton);
        listOfMembers.add(bookmarkedContactsLabel);

        sendButton = new JButton("Send");
        sendButton.setVisible(true);
        sendButton.setForeground(Color.BLACK);
        sendButton.setBackground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        sendButton.setFocusable(false);
        sendButton.setBounds(270, 570, 50, 20);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage();
                } catch (ParserConfigurationException | IOException | SAXException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        searchButton = new JButton("Search");
        searchButton.setVisible(true);
        searchButton.setForeground(Color.BLACK);
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        searchButton.setFocusable(false);
        searchButton.setBounds(260, 600, 75, 20);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SearchingContacts();
            }
        });


        privateMessagePanel.add(currentUserName);
        privateMessagePanel.add(currentUserStatus);
        privateMessagePanel.add(messagePane);
        privateMessagePanel.add(messageInput);
        privateMessagePanel.add(sendButton);
        privateMessagePanel.add(searchButton);

        broadCastPanel.add(broadCast);
        broadCastPanel.add(broadcastArea);
        //after log in is successful, makikita nayung main GUI
        this.setVisible(true);
        messagePane.setText("CONNECTED TO: "+server.getLocalAddress()+" PORT: "+server.getPort());

    }

    private void updateContactList(String[] allContacts) {
        ArrayList<String> contactsList = new ArrayList<>();

        bookmarkedContacts.sort(String.CASE_INSENSITIVE_ORDER);
        for (String contact : bookmarkedContacts) {
            if (contactsList.contains(contact)) {
                continue;
            }
            contactsList.add(contact);
        }
        for (String contact : allContacts) {
            if (contactsList.contains(contact)) {
                continue;
            }
            contactsList.add(contact);
        }
        contactList.setListData(contactsList.toArray(new String[0]));
    }

    private void updateBookmarkedContactsLabel() {
        bookmarkedContactsLabel.setText("Bookmarked Contacts: " + bookmarkedContacts.toString());
    }


    private void sendMessage() throws ParserConfigurationException, IOException, SAXException {
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
                    String recipient = words[2].toLowerCase(Locale.ROOT);
                    if(Server.getRegisteredUserNames().contains(recipient)){
                        String messageContent = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
                        System.out.println("pm xx " + messageContent);
                        msg = new Message(user.getName(), recipient, messageContent);
                        break;
                    }
                    else{
                        msg = new Message(user.getName(), recipient, message);
                        messagePane.setText(messagePane.getText()+"\n"+"[ERROR] user "+ recipient+" does not exist.");
                        break;
                    }
                case "quit":
                    XMLParse xmlParse = new XMLParse("res/users.xml");
                    xmlParse.setLoginStatus(user.getName(), "offline");
                    System.exit(0);
                default:
                    msg = new Message("NOTHING");
                    messagePane.setText(messagePane.getText()+"\n"+"[ERROR] error in parsing message -> command not recognized???");
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

    private void setLoginStatus(String name, String status) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(f);
            Element root = document.getDocumentElement();
            NodeList users = root.getElementsByTagName("User");
            int userListLength = users.getLength();
            for(int i = 0; i<userListLength;i++){
                Element u = (Element) users.item(i);
                String nameNode = u.getElementsByTagName("name").item(0).getTextContent();
                if(nameNode.equals(name)){
                    u.getElementsByTagName("status").item(0).setTextContent(status);
                    Server.updateXML(users, document);
                    System.out.println(f.getCanonicalPath()+" has been updated!, status of "+name+" has been set to '"+status+"'.");
                    break;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private String[] getAllContacts() {
        String[] contacts = new String[Server.registeredUsersList.size()];

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");
            NodeList userNodes = document.getElementsByTagName("User");

            contacts = new String[userNodes.getLength()];

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                    String status = userElement.getElementsByTagName("status").item(0).getTextContent();
                    contacts[i] = name + " : " + status;
                }
            }
            // Sort contacts alphabetically
            Arrays.sort(contacts);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return contacts;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        int port = 0;
        String hostName;
        String userName;
        server = null;
        hostName = "localhost";


        boolean validPort = false;
        portAuth:
        while (!validPort) {
            port = Integer.parseInt(JOptionPane.showInputDialog(new JFrame(), "Input port: ", "Port connection", JOptionPane.INFORMATION_MESSAGE));

            hostName = JOptionPane.showInputDialog(new JFrame(),"Input host: ", "Port connection", JOptionPane.INFORMATION_MESSAGE);
            validPort =true;
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
            GUIClientController controller = new GUIClientController(server);
            ServerMessageListener listener = controller.new ServerMessageListener();
            listener.start();

            // Show the frame
            controller.setVisible(true);
        }
        else
            System.exit(0);
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
                        if(msg.getRecipient() == null){
                            continue;
                        }
                        else if(msg.getRecipient().equals("TOALL")){
                            messagePane.setText(messagePane.getText()+"\n"+"[BROADCAST] "+msg.getSender()+": "+msg.getContent());
                        }
                        else{
                            messagePane.setText(messagePane.getText()+"\n"+"[PRIVATE] "+msg.getSender()+": "+msg.getContent());
                        }
                        System.out.println(msg.getSender()+": " + msg.getContent());
                    }
                    else if(obj instanceof List<?>){
                        List<?> list = (List<?>) obj;
                        System.out.println("ASDASDASDASDASDASD");
                        if (!list.isEmpty() && list.get(0) instanceof OfflineMessage) {
                            List<OfflineMessage> offlineMessages = (List<OfflineMessage>) list;
                            for (OfflineMessage offlineMessage : offlineMessages) {
                                String sender = offlineMessage.getSender();
                                String content = offlineMessage.getContent();
                                messagePane.setText(messagePane.getText() + "\n" + "[PRIVATE] " + sender + ": " + content);
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}