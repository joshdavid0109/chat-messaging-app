package gui_classes.clientside;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.plaf.synth.SynthScrollPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.*;

import static gui_classes.clientside.CreateGroup.renderCell;

public class GUIClientFrame extends JFrame {

    private GUIClientController controller;
    public static JList<String> contactList;
    public static ArrayList<String> usersList;
    public JList<String> groupsList;
    public JList<String> availGroups;
    public JList<String> searchResultsList;
    private JTextArea messagePane;
    private JButton sendButton;
    private JTextField messageInput;
    private JTextField searchInput;
    public static HashMap<String, Boolean> bookmarkedContactsMap;
    public static JButton bookmarkButton;
    private static JButton searchButton;
    private final int fontSize = 12;
    User user;


    public GUIClientFrame(GUIClientController controller, User u, ObjectOutputStream out) throws IOException, ParserConfigurationException, SAXException {
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
        usersList = new ArrayList<>(Arrays.asList(XMLParse.getAllContacts()));

        // Inform other clients that new client have just been connected
        out.writeObject(usersList);         // then update members tab
        out.flush();

        contactList = new JList<>(XMLParse.getAllContacts());
        contactList.setFixedCellHeight(30);
        contactList.setCellRenderer(renderCell());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        List<String> x = Server.getGroupsOfUser(user);

        String[] groupsArray = x.toArray(new String[x.size()]);

        groupsList = new JList<>(groupsArray);
        groupsList.setFixedCellHeight(30);
        groupsList.setCellRenderer(renderCell());
        groupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ArrayList<String> temp = XMLParse.getAllGroups();
        String [] stringOfGroups = temp.toArray(new String[temp.size()]);
        availGroups = new JList<>(stringOfGroups);
        availGroups.setFixedCellHeight(30);
        availGroups.setCellRenderer(renderCell());
        availGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


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

        JLabel groups = new JLabel("GROUPS");
        groups.setForeground(Color.WHITE);
        groups.setFont(new Font("Arial", Font.BOLD, 18));
        groups.setBounds(180, 350, 200, 75);

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
        bookmarkedContactsMap = new HashMap<String, Boolean>();

        for (String s:
                usersList
             ) {
            bookmarkedContactsMap.put((s.split("\\s+")[0]), false);

        }

        bookmarkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedContact = "";
                if (contactList.getSelectedValue() != null) {
                    selectedContact = contactList.getSelectedValue();
                    int index = contactList.getSelectedIndex();

                    if (bookmarkButton.getText().equals("Bookmark")) {
                        usersList.remove(index);
                        usersList.add(0, selectedContact);
                        System.out.println("'" + selectedContact.split(":")[0].trim() + "' bookmark added.");

                        for (Map.Entry<String, Boolean> entry : bookmarkedContactsMap.entrySet()) {
                            if (entry.getKey().equals(selectedContact.split(" : ")[0]))
                                entry.setValue(true);
                        }
                    } else if (bookmarkButton.getText().equals("Unbookmark")) {
                        usersList.remove(index);
                        usersList.add(selectedContact);
                        System.out.println("'" + selectedContact.split(":")[0].trim() + "' bookmark removed.");

                        for (Map.Entry<String, Boolean> entry : bookmarkedContactsMap.entrySet()) {
                            if (entry.getKey().equals(selectedContact.split(" : ")[0]))
                                entry.setValue(false);
                        }
                    }

                    String [] temp = usersList.toArray(new String[usersList.size()]);
                    contactList.setListData(temp);
                    contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    contactList.setFixedCellHeight(30);
                    contactList.setCellRenderer(renderCell());
                }

                // sa pag bookmark ng groups
                String selectedGroup = "";
                if (groupsList.getSelectedValue() != null) {
                    selectedGroup = groupsList.getSelectedValue();
                    boolean isBookmarked = bookmarkedContactsMap.getOrDefault(selectedGroup, false);

                    if (isBookmarked) {
                        bookmarkedContactsMap.put(selectedGroup, false);
                        System.out.println("'" + selectedGroup.split(":")[0].trim() + "' bookmark removed.");
                    } else {
                        bookmarkedContactsMap.put(selectedGroup, true);
                        System.out.println("'" + selectedGroup.split(":")[0].trim() + "' bookmark added.");
                    }

                    boolean updatedIsBookmarked = bookmarkedContactsMap.getOrDefault(selectedGroup, false);
                    bookmarkButton.setText(updatedIsBookmarked ? "Unbookmark" : "Bookmark");
                }

                // Sort groupsList
                ArrayList<String> bookmarkedGroups = new ArrayList<>();
                ArrayList<String> unbookmarkedGroups = new ArrayList<>();

                for (String group : groupsArray) {
                    if (bookmarkedContactsMap.getOrDefault(group, false)) {
                        bookmarkedGroups.add(group);
                    } else {
                        unbookmarkedGroups.add(group);
                    }
                }

                bookmarkedGroups.addAll(unbookmarkedGroups);

                String[] tempGroups = bookmarkedGroups.toArray(new String[bookmarkedGroups.size()]);
                groupsList.setListData(tempGroups);
            }
        });

        groupsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                if (groupsList.getSelectedValue() != null) {
                    String selectedGroup = groupsList.getSelectedValue();
                    boolean isBookmarked = bookmarkedContactsMap.getOrDefault(selectedGroup, false);
                    bookmarkButton.setText(isBookmarked ? "Unbookmark" : "Bookmark");
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
        scrollPaneListMembers.setUI(new BasicScrollPaneUI());
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

                // Pang unbookmark/bookmark
                for (Map.Entry<String, Boolean> e:
                     bookmarkedContactsMap.entrySet()) {
                    if (e.getKey().equals(x[0]) && e.getValue()) {
                        bookmarkButton.setText("Unbookmark");
                    } else if (e.getKey().equals(x[0]))
                        bookmarkButton.setText("Bookmark");

                }
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


        JScrollPane availableGroups = new JScrollPane(availGroups);
        availableGroups.setVisible(true);
        availableGroups.setBorder(BorderFactory.createEmptyBorder());
        availableGroups.setBounds(90, 415, 270, 200);

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
                    if(obj instanceof User user){
                        stringResultMap.add("USER "+" : "+user.getName()+" @"+user.getUsername());
                    }
                    else if(obj instanceof Group grp){
                        stringResultMap.add("GROUP "+" : "+grp.getName());
                    }
                }

                String[] resultsArr = stringResultMap.toArray(new String[searchRes.size()]);
                searchResultsList = new JList<>( resultsArr);
                searchResultsList.setFixedCellHeight(30);
                searchResultsList.setCellRenderer(renderCell());
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
        createGroup.setBounds(90, 640, 100, 20);
        JFrame f = this;
        createGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroup(f, out, user);
            }
        });

        /**
         * Additional feature nalang eheh
         *
         */
        JButton joinGroup = new JButton("Join Group");
        joinGroup.setVisible(true);
        joinGroup.setForeground(Color.BLACK);
        joinGroup.setBackground(Color.WHITE);
        joinGroup.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        joinGroup.setFocusable(false);
        joinGroup.setBounds(260, 640, 100, 20);
        joinGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                    out.writeObject(new JOptionPane());
                    JOptionPane.showMessageDialog(f, "This feature is not yet available"
                    );

            }
        });

        JButton leaveGroup = new JButton("Leave Group");
        leaveGroup.setVisible(true);
        leaveGroup.setForeground(Color.BLACK);
        leaveGroup.setBackground(Color.WHITE);
        leaveGroup.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        leaveGroup.setFocusable(false);
        leaveGroup.setBounds(790, 640, 100, 20);
        leaveGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                    out.writeObject(new JOptionPane());
                String gcName = groupsList.getSelectedValue();
                try {

                    Object[] options = {"Yes", "No"};

                    if (groupsList.getSelectedIndex() == -1) {
                        JOptionPane.showMessageDialog(null, "Select a row at the table to delete.", "User Deletion", JOptionPane.ERROR_MESSAGE, null);
                        return;
                    }


                    int c = JOptionPane.showOptionDialog(null, "Are you sure you want to leave " + gcName + "?", "Leave A Group",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

                    if (c == 0) {
                        out.writeObject("/leavegroup " + gcName + " " + user.getUsername());
                        out.flush();
                    }


                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
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
        this.add(groups);
        this.add(currentUserName);
        this.add(currentUserStatus);
        this.add(listOfMembersName);
        this.add(bookmarkButton);
        this.add(createGroup);
        this.add(joinGroup);
        this.add(leaveGroup);
        this.add(availableGroups);
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
                usersList = new ArrayList<>(Arrays.asList(XMLParse.getAllContacts()));
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
        contactList.setFixedCellHeight(30);
        contactList.setCellRenderer(renderCell());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void updateGroupsTab() throws ParserConfigurationException, IOException, SAXException {
        String [] temp = Server.getGroupsOfUser(user).toArray(new String[Server.getGroupsOfUser(user).size()]);
        groupsList.setListData(temp);
        groupsList.setFixedCellHeight(30);
        groupsList.setCellRenderer(renderCell());
        groupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ArrayList<String> temp1 = XMLParse.getAllGroups();
        String [] stringOfGroups = temp1.toArray(new String[temp1.size()]);
        availGroups.setListData(stringOfGroups);
        availGroups.setFixedCellHeight(30);
        availGroups.setCellRenderer(renderCell());
        availGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    public static ListCellRenderer<? super String> changeColor() {
        return new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(list,value, index, isSelected, cellHasFocus);
                c.setBackground(Color.green);
                return c;

            }
        };

    }


    public void appendMessage(String message) {
        Font font = new Font("Verdana", Font.BOLD, 10);
        messagePane.setFont(font);
        messagePane.append(message+ "\n");
    }

/*    public void setUsers(String[] users) {
        userPane.setText("");
        for (String user : users) {
            userPane.append(user + "\n");
        }
    }

    public void updateUsernameLabel(String username) {
        usernameLabel.setText(username);
    }*/

    public String getMessageText() {
        return messageInput.getText();
    }

    public void clearMessageText() {
        messageInput.setText("");
    }


}
