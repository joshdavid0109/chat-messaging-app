package gui_classes.clientSide;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Frame implements ActionListener {
    private final ArrayList<String> bookmarkedContacts = new ArrayList<>();
    private final JList<String> contactList;
    PrintWriter output;
    private String message = " ";

    private final JLabel bookmarkedContactsLabel;
    private static JButton bookmarkButton;
    private static JButton sendButton;
    private static JTextField pmTextField;
    private static JScrollPane pmTextArea;
    private static JScrollPane broadcastArea;
    private static JPanel privateMessagePanel;
    private static JScrollPane scrollPane;
    private static JTextArea textArea;

    Frame() {
        contactList = new JList<>(getAllContacts());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JLabel headerName = new JLabel("USERNAME");
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

        JLabel currentUserName = new JLabel("USERNAME");
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
        sendButton.addActionListener(this);

        bookmarkButton = new JButton("Star");
        bookmarkButton.setVisible(true);
        bookmarkButton.setForeground(Color.BLACK);
        bookmarkButton.setBackground(Color.WHITE);
        bookmarkButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        bookmarkButton.setFocusable(false);
        bookmarkButton.setBounds(40, 300, 100, 20);
        bookmarkButton.addActionListener(this);

        bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");
        bookmarkedContactsLabel.setVisible(true);
        bookmarkedContactsLabel.setForeground(Color.WHITE);
        bookmarkedContactsLabel.setBounds(40,250,200,200);

        pmTextField = new JTextField();
        pmTextField.setVisible(true);
        pmTextField.setBorder(BorderFactory.createEmptyBorder());
        pmTextField.setBounds(30, 570, 220, 20);

        pmTextField.addKeyListener(new KeyAdapter() {
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
        });

        JScrollPane scrollPaneListMembers = new JScrollPane(contactList);
        scrollPaneListMembers.setVisible(true);
        scrollPaneListMembers.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneListMembers.setBounds(40, 70, 200, 200);

        pmTextArea = new JScrollPane();
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

    public void sendMessage() {
        try {
            String message = pmTextField.getText().trim();
            if (message.equals("")) {
                return;
            }
            this.message = message;
            output.println(message);
            pmTextField.requestFocus();
            pmTextField.setText(null);
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

    private String[] getAllContacts() {
        String[] contacts;
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
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return contacts;
    }

    private void updateContactList(String[] allContacts) {
        ArrayList<String> contactsList = new ArrayList<>();

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
        bookmarkedContactsLabel.setText("Bookmarked Contacts: " + bookmarkedContacts);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bookmarkButton) {
            String selectedContact = contactList.getSelectedValue();
            if (selectedContact != null) {
                if (bookmarkedContacts.remove(selectedContact)) {
                    bookmarkButton.setToolTipText("Add to bookmark");
                } else {
                    bookmarkedContacts.add(selectedContact);
                    bookmarkButton.setToolTipText("Remove from bookmark");
                }
                updateBookmarkedContactsLabel();
                updateContactList(getAllContacts());
            }
        }
        if(e.getSource() == sendButton){
//            System.out.println("messagebutton");
            textArea.append(pmTextField.getText()+"\n");
        }
    }
}
