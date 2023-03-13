package gui_classes.clientside;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.*;

public class GUIClientFrame extends JFrame {

    private GUIClientController controller;
    public static JList<String> contactList;
    public static ArrayList<String> usersList;
    public JList<String> groupsList;
    public JList<String> searchResultsList;
    public ArrayList<String> bookmarkedContacts = new ArrayList<>();
    public  JLabel bookmarkedContactsLabel = new JLabel();
    private JTextArea messagePane;
    private JTextArea userPane;
    private JButton sendButton;
    private JButton logoutButton;
    private JLabel usernameLabel;
    private JTextField messageInput;
    private JTextField searchInput;
    public static JButton bookmarkButton;
    private static JButton searchButton;
    private int fontSize = 12;
    ArrayList<String> similarNames;
    User user;


    public void setContactList(JList<String> contactList) {
        this.contactList = contactList;
    }

    public GUIClientFrame(GUIClientController controller, User u, ObjectOutputStream out) throws IOException {
        this.user = u;
        this.controller = controller;

        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, fontSize);
        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        setTitle("Chat Application - "+user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        usersList = new ArrayList<>(Arrays.asList(getAllContacts()));

        // Inform other clients that new client have just been connected
        out.writeObject(usersList);         // then update members tab
        out.flush();

        contactList = new JList<>(getAllContacts());
        contactList.setFixedCellHeight(20);
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        List<String> x = Server.getGroupsOfUser(user);

        String[] groupsArray = x.toArray(new String[x.size()]);

        groupsList = new JList<>(groupsArray);
        groupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Initialize the components
        messagePane = new JTextArea();
        messagePane.setFont(font);
        messagePane.setEditable(false);
        messagePane.setLineWrap(true); // fit text to panel
        JScrollPane scrollPane = new JScrollPane(messagePane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //scrollPane.setPreferredSize(new Dimension(350, 200));

        JLabel headerName = new JLabel();
        headerName.setForeground(Color.WHITE);
        headerName.setFont(new Font("Arial", Font.BOLD, 12));
        headerName.setBounds(920, -15, 200, 75);

        JLabel headerStatus = new JLabel(user.getStatus());
        headerStatus.setForeground(Color.GREEN);
        headerStatus.setFont(new Font("Arial", Font.BOLD, 10));
        headerStatus.setBounds(920, 0, 200, 75);

        JLabel miscellaneous = new JLabel("MISCELLANEOUS");
        miscellaneous.setForeground(Color.WHITE);
        miscellaneous.setFont(new Font("Arial", Font.BOLD, 18));
        miscellaneous.setBounds(100, 0, 200, 75);

        JLabel currentUserName = new JLabel();
        currentUserName.setForeground(Color.WHITE);
        currentUserName.setFont(new Font("Arial", Font.BOLD, 18));
        currentUserName.setBounds(30, 0, 200, 75);

        JLabel currentUserStatus = new JLabel();
        currentUserStatus.setForeground(Color.GREEN);
        currentUserStatus.setFont(new Font("Arial", Font.PLAIN, 18));
        currentUserStatus.setBounds(270, 0, 200, 75);

        JLabel listOfMembersName = new JLabel("MEMBERS");
        listOfMembersName.setForeground(Color.WHITE);
        listOfMembersName.setFont(new Font("Arial", Font.BOLD, 18));
        listOfMembersName.setBounds(90, 0, 200, 75);

        JLabel listOfUserGroup = new JLabel("YOUR GROUPS");
        listOfUserGroup.setForeground(Color.WHITE);
        listOfUserGroup.setFont(new Font("Arial", Font.BOLD, 18));
        listOfUserGroup.setBounds(90, 20, 200, 75);

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
                if (contactList.getSelectedValue() != null) {
                    selectedContact = contactList.getSelectedValue();
                    int index = contactList.getSelectedIndex();
                    usersList.remove(index);
                    usersList.add(0, selectedContact);

                    String [] temp = usersList.toArray(new String[usersList.size()]);
                    contactList.setListData(temp);
                    contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    contactList.setFixedCellHeight(20);


                }
            }
        });

        messageInput = new JTextField();
        messageInput.setVisible(true);
        messageInput.setBorder(BorderFactory.createEmptyBorder());
        messageInput.setBounds(30, 570, 220, 20);

        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        headerName.setText(user.getUsername());
        currentUserName.setText(user.getName());
        headerStatus.setText("online");

        JScrollPane scrollPaneListMembers = new JScrollPane(contactList);
        scrollPaneListMembers.setVisible(true);
        scrollPaneListMembers.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneListMembers.setBounds(40, 70, 200, 200);
        contactList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList<String> list = (JList<String>) evt.getSource();

                //REFRESH, MAG UUPDATE KUNG SINO ONLINE AND NOT, kahirap neto gawin animal
                /*setContactList(new JList<>(getAllContacts()));
                scrollPaneListMembers.setViewportView(contactList);
                scrollPaneListMembers.revalidate();
                scrollPaneListMembers.repaint();*/

                String selectedValue = list.getSelectedValue();
                String[] x = selectedValue.split(" : ");
                messageInput.setText("/pm "+ x[0]+" ");

            }
        });

        JScrollPane scrollPanelGroups = new JScrollPane(groupsList);
        scrollPanelGroups.setVisible(true);
        scrollPanelGroups.setBorder(BorderFactory.createEmptyBorder());
        scrollPanelGroups.setBounds(40, 70, 200, 200);
        groupsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList<String> list = (JList<String>) evt.getSource();
                String selectedValue = list.getSelectedValue();
                messageInput.setText("/gm "+selectedValue+" ");
            }
        });

        searchInput = new JTextField();
        searchInput.setVisible(true);
        searchInput.setBorder(BorderFactory.createEmptyBorder());
        searchInput.setBounds(40, 70, 170, 20);

        //search button
        searchButton = new JButton("Search");
        searchButton.setVisible(true);
        searchButton.setForeground(Color.BLACK);
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        searchButton.setFocusable(false);
        searchButton.setBounds(230,70,70, 20);

        JScrollPane searchResultsScrollPane = new JScrollPane(searchResultsList);
        searchResultsScrollPane.setVisible(true);
        searchResultsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        searchResultsScrollPane.setBounds(40, 100, 270, 200);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //can contain both users and groups object na
                List<Object> searchRes = new ArrayList<>();
                String toSearch = searchInput.getText().trim();
                searchInput.setText("");
                searchRes = XMLParse.searchXML(toSearch);
                List<String> stringResultMap = new ArrayList<>();
                //List<Object> info = XMLParse.searchXML(toSearch);

                //iterate through lahat ng objects na nasearch (users or groups) tapos
                //sort them idk
                for(Object obj : searchRes){
                    if(obj instanceof User){
                        User user = (User) obj;
                        stringResultMap.add("USER "+" : "+user.getName()+" @"+user.getUsername());
                    }
                    else if(obj instanceof Group){
                        Group grp = (Group) obj;
                        stringResultMap.add("GROUP "+" : "+grp.getName());
                    }
                }

                String[] resultsArr = stringResultMap.toArray(new String[searchRes.size()]);
                searchResultsList = new JList<>( resultsArr);
                searchResultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                searchResultsList.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        JList<String> list = (JList<String>) evt.getSource();
                        String selectedValue = list.getSelectedValue();
                        //messageInput.setText("/gm "+selectedValue+" ");
                        //System.out.println("selected "+selectedValue);

                        String[] thingz = selectedValue.split("\\s+");

                        //can either be "GROUP" or "USER"
                        String type = thingz[0];

                        if (type.equals("USER")) {
                            //String username = thingz[3];
                            /*System.out.println("A "+thingz[0]);
                            System.out.println("B "+thingz[1]);
                            System.out.println("C "+thingz[2]);
                            System.out.println("D "+thingz[3]);*/
                            messageInput.setText("/pm "+thingz[2]+" ");
                        } else if (type.equals("GROUP")) {
                            /*System.out.println("A "+thingz[0]);
                            System.out.println("B "+thingz[1]);
                            System.out.println("C "+thingz[2]);*/
                            messageInput.setText("/gm "+thingz[2]+" ");
                        }
                    }
                });

                searchResultsScrollPane.setViewportView(searchResultsList);
                searchResultsScrollPane.revalidate();
            }
        });



        JButton createGroup = new JButton("Create Group");
        createGroup.setVisible(true);
        createGroup.setForeground(Color.BLACK);
        createGroup.setBackground(Color.WHITE);
        createGroup.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        createGroup.setFocusable(false);
        createGroup.setBounds(790, 640, 100, 20);
        JFrame f = this;
        createGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroup(f, out, user);
            }
        });

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

        JPanel listGroup = new JPanel();
        listGroup.setBackground(new Color(0X23272A));
        listGroup.setBounds(750,350,400, 750);
        listGroup.setLayout(null);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(0X23272A));
        searchPanel.setBounds(200,350,400, 750);
        searchPanel.setLayout(null);

        JLabel bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");
        bookmarkedContactsLabel.setVisible(true);
        bookmarkedContactsLabel.setForeground(Color.WHITE);
        bookmarkedContactsLabel.setBounds(40,250,200,200);

        // Add the components to the frame
        this.setLayout(new FlowLayout());
        this.add(headerName);
        this.add(headerStatus);
        this.add(miscellaneous);
        this.add(currentUserName);
        this.add(currentUserStatus);
        this.add(listOfMembersName);
        this.add(bookmarkButton);
        this.add(createGroup);
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


        this.add(searchPanel);
        //searchPanel.add

        broadCastPanel.add(searchResultsScrollPane);
        broadCastPanel.add(searchInput);
        broadCastPanel.add(searchButton);

        this.add(listGroup);
        listGroup.add(listOfUserGroup);
        listGroup.add(scrollPanelGroups);

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

        privateMessagePanel.add(currentUserName);
        privateMessagePanel.add(currentUserStatus);
        privateMessagePanel.add(messagePane);
        privateMessagePanel.add(messageInput);
        privateMessagePanel.add(sendButton);

        broadCastPanel.add(miscellaneous);
        broadCastPanel.add(broadcastArea);
        //after log in is successful, makikita nayung main GUI
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                XMLParse.setStatusOfUser(user.getUsername(), "offline");
                usersList = new ArrayList<>(Arrays.asList(getAllContacts()));
                try {
                    out.writeObject(usersList);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateStats(ArrayList<String> contacts) {
        String [] temp = contacts.toArray(new String[contacts.size()]);
        contactList.setListData(temp);
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.setFixedCellHeight(20);
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
            System.out.println(e.getMessage());
        }
        return contacts;
    }

    public void appendMessage(String message) {
        Font font = new Font("Verdana", Font.BOLD, 10);
        messagePane.setFont(font);
        messagePane.append(message+ "\n");
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

    private void refresh(){
        contactList = new JList<>(getAllContacts());
    }

}
