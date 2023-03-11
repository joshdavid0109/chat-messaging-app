package gui_classes.clientside;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateGroup extends JDialog implements Runnable{
    private JButton addButton;
    private JList<String> usersList;
    private JButton saveButton;
    private JList<String> selectedUsersList;
    private JButton removeButton;
    private JPanel panel;
    private JScrollPane usersPane;
    private JLabel groupName;
    private ArrayList<String> selectedUsers;

    public CreateGroup(JFrame parent, String groupName, ObjectOutputStream out) {
        super(parent, "Create Group", true);

        usersList.setListData(usersList());
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setFixedCellHeight(30);


        panel.setBackground(Color.decode("#3e444f"));
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public CreateGroup(JFrame parent) {
        super(parent, "Create Group", true);
        selectedUsers = new ArrayList<>();
        saveButton.setUI(new BasicButtonUI());

        Font f = new Font("Verdana", Font.BOLD, 15);
        groupName.setFont(f);
        groupName.setText("Sample Group");

        usersList.setListData(usersList());
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setFixedCellHeight(30);
        usersList.setCellRenderer(getRenderer());


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String selectedUser = usersList.getSelectedValue();

                boolean check =false;
                if (selectedUsers.size()>0) {
                    String temp = "";
                    for (int i = 0; i < selectedUsers.size(); i++) {
                        temp = selectedUsers.get(i);
                        if (temp.equals(selectedUser)) {
                            check =true;
                            break;
                        }
                    }
                    if (!check) {
                        System.out.println(temp + "\n" + selectedUser);
                        selectedUsers.add(selectedUser);
                        String[] t = selectedUsers.toArray(new String[0]);
                        selectedUsersList.setSelectedIndex(-1);
                        selectedUsersList.setListData(t);
                        selectedUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        selectedUsersList.setFixedCellHeight(30);
                        selectedUsersList.setCellRenderer(getRenderer());
                    }
                } else {
                    System.out.println("Add " + selectedUser);
                    selectedUsers.add(selectedUser);
                    String[] temp = selectedUsers.toArray(new String[0]);
                    selectedUsersList.setListData(temp);
                    selectedUsersList.setFocusable(false);
                    selectedUsersList.setSelectedIndex(-1);
                    selectedUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    selectedUsersList.setFixedCellHeight(30);
                    selectedUsersList.setCellRenderer(getRenderer());
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = selectedUsersList.getSelectedValue();
                for (int i =0; i< selectedUsers.size();i++) {
                    String temp= selectedUsers.get(i);
                    if (temp.equals(selectedUser)) {
                        selectedUsers.remove(i);
                        break;
                    }
                }
                String [] temp = selectedUsers.toArray(new String[selectedUsers.size()]);
                selectedUsersList.setListData(temp);
                selectedUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                selectedUsersList.setFixedCellHeight(30);
                selectedUsersList.setCellRenderer(getRenderer());
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO writeObject then parse to xml file
            }
        });


        panel.setBackground(Color.decode("#3e444f"));
        getContentPane().add(panel);
        setSize(500, 500);
        setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,Color.BLACK));
                return listCellRendererComponent;
            }
        };
    }

    public static void main(String[] args) {
        new CreateGroup(null);
    }

    @Override
    public void run() {
       this.run();
    }

    public String[] usersList() {
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
                    String username = userElement.getElementsByTagName("Username").item(0).getTextContent();
                    contacts[i] = name + " - " + username;
                }
            }
            // Sort contacts alphabetically
            Arrays.sort(contacts);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
        return contacts;
    }
}
