package gui_classes.clientside;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.ClientHandler;
//import server_side.Server;
import server_side.Server;
import shared_classes.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static server_side.Server.loginHandlerArraylist;

public class Frame implements ListSelectionListener {
    public final ArrayList<String> bookmarkedContacts = new ArrayList<>();
    public final JList<String> contactList;

    User user;
    Socket socket;
    PrintWriter output;
    BufferedReader bufferedReader;
    public String message = " ";

    public final JLabel bookmarkedContactsLabel;
    public static JButton bookmarkButton;
    public static JButton sendButton;
    public static JTextField pmTextField;
    public static JScrollPane broadcastArea;
    public static JPanel privateMessagePanel;
    public static JScrollPane scrollPane;
    private static JTextArea textArea;



    Frame(User user, Socket socket, PrintWriter printWriter) throws IOException {
        this.user = user;
        this.socket = socket;
        output = new PrintWriter(socket.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        contactList = new JList<>(getAllContacts());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.addListSelectionListener(this);

        JLabel headerName = new JLabel();
        headerName.setText(user.username());
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

        JLabel currentUserName = new JLabel(user.name());
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
                    if (!pmTextField.getText().equals(""))
                        broadcast(pmTextField.getText());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

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

        bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");
        bookmarkedContactsLabel.setVisible(true);
        bookmarkedContactsLabel.setForeground(Color.WHITE);
        bookmarkedContactsLabel.setBounds(40,250,200,200);

        pmTextField = new JTextField();
        pmTextField.setVisible(true);
        pmTextField.setBorder(BorderFactory.createEmptyBorder());
        pmTextField.setBounds(30, 570, 220, 20);

        /*pmTextField.addKeyListener(new KeyAdapter() {
            // send message on Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }

                // Get last message typed
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = pmTextField.getText().trim();
                    pmTextField.setText(message);
                    message = currentMessage;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String currentMessage = pmTextField.getText().trim();
                    pmTextField.setText(message);
                    message = currentMessage;
                }
            }
        });*/

        JScrollPane scrollPaneListMembers = new JScrollPane(contactList);
        scrollPaneListMembers.setVisible(true);
        scrollPaneListMembers.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneListMembers.setBounds(40, 70, 200, 200);

        JScrollPane pmTextArea = new JScrollPane();
        pmTextArea.setVisible(true);
        pmTextArea.setBorder(BorderFactory.createEmptyBorder());
        pmTextArea.setBounds(30, 70, 290, 470);

        textArea = new JTextArea();
        pmTextArea.setViewportView(textArea);

        textArea.append("New message\n");
        //textArea.setCaretPosition(textArea.getDocument().getLength());


        broadcastArea = new JScrollPane();
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

        privateMessagePanel = new JPanel();
        privateMessagePanel.setBackground(new Color(0X2C2F33));
        privateMessagePanel.setBounds(400,50,350, 750);
        privateMessagePanel.setLayout(null);

        JPanel listOfMembers = new JPanel();
        listOfMembers.setBackground(new Color(0X23272A));
        listOfMembers.setBounds(750,50,400, 750);
        listOfMembers.setLayout(null);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.pack();
        frame.setBounds(500, 250, 930, 540);
        frame.setTitle("Budget Discord");
        frame.setSize(1050, 750);
        frame.getContentPane().setBackground(Color.WHITE);

        frame.add(header);
        header.add(headerName);
        header.add(headerStatus);

        frame.add(verticalHeader);

        frame.add(broadCastPanel);
        broadCastPanel.add(broadCast);
        broadCastPanel.add(broadcastArea);

        frame.add(privateMessagePanel);
        privateMessagePanel.add(currentUserName);
        privateMessagePanel.add(currentUserStatus);
        privateMessagePanel.add(sendButton);
        privateMessagePanel.add(pmTextField);
        privateMessagePanel.add(pmTextArea);

        frame.add(listOfMembers);
        listOfMembers.add(listOfMembersName);
        listOfMembers.add(scrollPaneListMembers);
        listOfMembers.add(bookmarkButton);
        listOfMembers.add(bookmarkedContactsLabel);
        privateMessagePanel.add(new JLabel("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHELLO"));
        pmTextArea.add(new JLabel("<html><b>" + "Your name" + ":</b> " + message + "</html>"));
        pmTextArea.add(new JLabel("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
    }

  /*  public void run() throws IOException {
        Scanner sc = new Scanner(socket.getInputStream());

        while (sc.hasNextLine()) {
            message =sc.nextLine();
            // Gestion des messages private
            *//*if (message.charAt(0) == '@'){
                if(message.contains(" ")){
                    System.out.println("private msg : " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate= message.substring(1, firstSpace);
                    server.sendMessageToUser(
                            message.substring(
                                    firstSpace+1, message.length()
                            ), user, userPrivate
                    );
                }

                // Gestion du changement
            }else if (message.charAt(0) == '#'){
                user.changeColor(message);
                // update color for all other users
                this.server.broadcastAllUsers();
            }else{}*//*
                // update user list
                broadcast(message, user);

        }
        // end of Thread
        sc.close();
    }*/
      public JTextArea textAreaPane() {
          return textArea;
      }

    public void setTextArea(String message) {
        this.textAreaPane().append(message);
    }

    public JTextField msgTextField() {
          return pmTextField;
    }

    public void setMsgTextField(String message) {
          this.msgTextField().setText(message);
    }


    public void sendMessage(String m) throws IOException {
        try {
            for (Map.Entry<ClientHandler, User> hash : Server.loggedInUserHashMap.entrySet()) {

                if (hash.getValue().username().equals(user.username())) {
                    if (m.equals("")) {
                        return;
                    } else {
//                        message = hash.getKey().bufferedReader.readLine();
                        System.out.println(m);
                        textArea.append(m);
                        pmTextField.requestFocus();
                        pmTextField.setText(null);
                        break;
                    }

                }
                break;
            }

/*
            pmTextArea.add(new JLabel("<html><b>" + "Your name" + ":</b> " + message + "</html>"));
            privateMessagePanel.add(new JLabel("<html><b>" + "Your name" + ":</b> " + message + "</html>"));
            privateMessagePanel.revalidate();
            privateMessagePanel.repaint();*/
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    public void broadcast(String m) throws IOException {
        for (Frame frame : ClientMain.frameList) {
            if (frame != null ) {
                if (frame.msgTextField().getText()!= null) {
                    m = frame.msgTextField().getText();
                    frame.sendMessage(m + " test message \n");
                }
            }
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

    public void valueChanged(ListSelectionEvent e) {
        String selectedContact = contactList.getSelectedValue();
        if (selectedContact != null) {
            if (bookmarkedContacts.contains(selectedContact)) {
                bookmarkButton.setText("Unbookmark");
            } else {
                bookmarkButton.setText("Bookmark");
            }
        }
    }
}
