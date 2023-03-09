package gui_classes.clientside;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import server_side.UserManagement_GUI;
import shared_classes.*;

public class GUIClientFrame extends JFrame {

    private GUIClientController controller;
    public JList<String> contactList;
    public ArrayList<String> bookmarkedContacts = new ArrayList<>();
    public  JLabel bookmarkedContactsLabel;
    private JTextArea messagePane;
    private JTextArea userPane;
    private JTextField inputField;
    private JButton sendButton;
    private JButton logoutButton;
    private JLabel usernameLabel;
    private JTextField messageInput;
    public static JButton bookmarkButton;
    private JButton searchButton;
    private int fontSize = 12;
    User user;

    public GUIClientFrame(GUIClientController controller, User user) {
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, fontSize);
        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        //contactList = new JList<String>(getAllContacts());
        //contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.controller = controller;

        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        contactList = new JList<>(getAllContacts());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //contactList.addListSelectionListener(GUIClientController);

        // Initialize the components
        messagePane = new JTextArea();
        messagePane.setFont(font);
        messagePane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messagePane);
        scrollPane.setPreferredSize(new Dimension(350, 200));

        JLabel headerName = new JLabel();
        headerName.setForeground(Color.WHITE);
        headerName.setFont(new Font("Arial", Font.BOLD, 12));
        headerName.setBounds(920, -15, 200, 75);

        JLabel headerStatus = new JLabel(user.getStatus());
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

        JLabel currentUserStatus = new JLabel(user.getStatus());
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

        messageInput = new JTextField();
        messageInput.setVisible(true);
        messageInput.setBorder(BorderFactory.createEmptyBorder());
        messageInput.setBounds(30, 570, 220, 20);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

        JLabel bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");
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
                    controller.sendMessage();
                } catch (ParserConfigurationException | IOException | SAXException ex) {
                    throw new RuntimeException(String.valueOf(ex));
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
        //messagePane.setText("CONNECTED TO: "+server.getLocalAddress()+" PORT: "+server.getPort());
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

    public String[] getAllContacts() {
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


    public void appendMessage(String message) {
        messagePane.append(message + "\n");
    }

    public void setUsers(String[] users) {
        userPane.setText("");
        for (String user : users) {
            userPane.append(user + "\n");
        }
    }

    public void updateUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    public String getMessageText() {
        return messageInput.getText();
    }

    public void clearMessageText() {
        messageInput.setText("");
    }

    private class InputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendButton.doClick();
        }
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String recipient = userPane.getSelectedText();
            if (recipient == null) {
                recipient = "TOALL";
            }
            String content = inputField.getText();
            try {
                controller.sendMessage();
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                ex.printStackTrace();
            }
            inputField.setText("");
        }
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //controller.logout();
        }
    }

}
