package server_side;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import shared_classes.User;
import shared_classes.XMLParse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class UserManagement_GUI extends JFrame{
    private User user;
    private JList<User> list;
    private DefaultTableModel model;
    private JScrollPane userScroll = new JScrollPane();
    private JTable table;
    private int port;
    private XMLParse xmlParse;

    public UserManagement_GUI(/*User user*/) { this.user = user; }

    public void run() throws IOException, SAXException, ParserConfigurationException {
        xmlParse = new XMLParse();
        JFrame frameUM = new JFrame();
        frameUM.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
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
                } catch (IOException | ParserConfigurationException | SAXException exception) {
                    exception.printStackTrace();
                }
                System.exit(0);
            }
        });
        frameUM.setLayout(null);
        frameUM.setVisible(true);
        frameUM.setResizable(false);
        frameUM.setTitle("Budget Discord User Management");
        frameUM.setSize(755, 500);
        frameUM.getContentPane().setBackground(Color.decode("#3e444f"));

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frameUM.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frameUM.getHeight()) / 2);
        frameUM.setLocation(x, y);

        JButton startServer = new JButton("Start Server");
        startServer.setBounds(570, 25, 150, 45);
        startServer.setBorder(BorderFactory.createEtchedBorder(000000));
        startServer.setForeground(Color.black);
        startServer.setBackground(Color.decode("#149639"));

        AtomicBoolean serverStatus = new AtomicBoolean(false); // false offline
                              // true online

        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!serverStatus.get()) {
                    new Thread(() -> {
                        Server server = new Server(port, frameUM);
                        serverStatus.set(true);
                    }).start();
                }
            }
        });



        JButton addUser = new JButton("Add User");
        addUser.setBounds(570, 80, 150, 45);
        addUser.setBorder(BorderFactory.createEtchedBorder(000000));
        addUser.setForeground(Color.black);
        addUser.setBackground(Color.WHITE);

        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddUserHandler(frameUM);
            }
        });

        JButton deleteUser = new JButton("Delete User");
        deleteUser.setBounds(570, 135, 150, 45);
        deleteUser.setBorder(BorderFactory.createEtchedBorder(000000));
        deleteUser.setForeground(Color.black);
        deleteUser.setBackground(Color.WHITE);

        deleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object [] options = {"Yes", "No"};
                System.out.println(table.getSelectedRow());
                String selectedUser = (String) table.getValueAt(table.getSelectedRow(), 0);



                int c = JOptionPane.showOptionDialog(null, "Are you sure you want to remove " + selectedUser + "?", "User Deletion",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

                if (c == 0) {
                    String username = (String) table.getValueAt(table.getSelectedRow(), 1);
                    xmlParse.deleteUser(username);
                }
            }
        });

        JButton banUser = new JButton("Ban User");
        banUser.setBounds(570, 190, 150, 45);
        banUser.setBorder(BorderFactory.createEtchedBorder(000000));
        banUser.setForeground(Color.black);
        banUser.setBackground(Color.decode("#cc4949"));

        JButton unbanUser = new JButton("Unban User");
        unbanUser.setBounds(570, 245, 150, 45);
        unbanUser.setBorder(BorderFactory.createEtchedBorder(000000));
        unbanUser.setForeground(Color.black);
        unbanUser.setBackground(Color.decode("#c2b225"));

        JButton refresh = new JButton("Refresh");
        refresh.setBounds(570, 400, 150, 45);
        refresh.setBorder(BorderFactory.createEtchedBorder(000000));
        refresh.setForeground(Color.black);
        refresh.setBackground(Color.decode("#FF7F50"));

        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.removeAll();
                populateList();
                frameUM.add(table);
            }
        });


        frameUM.add(startServer);
        frameUM.add(addUser);
        frameUM.add(deleteUser);
        frameUM.add(banUser);
        frameUM.add(unbanUser);
        frameUM.add(refresh);


        populateList();
        userScroll = new JScrollPane(table);
        userScroll.setBounds(35, 30, 500, 400);

        frameUM.add(userScroll);

    }

    public void populateList() {
//        Server.getRegisteredUsers();
        Server.updateUsersList();


//            list1.setListData(Server.registeredUsersList.toArray(new User[0]));


        String[]  columnHeaders = new String[] {
                "Name", "Username", "Status", "Ban Status"};

        Object [][] userData = new String[Server.registeredUsersList.size()][Server.registeredUsersList.size()];
        int i = 0;
        for (User user : Server.registeredUsersList) {
            String name = user.getName();
            String username = user.getUsername();
            String status = user.getStatus();
            String banStatus = user.getBanStatus();
            for (int j =0; j < 4; j++) {
                switch (j) {
                    case 0 -> userData[i][j] = name;
                    case 1 -> userData[i][j] = username;
                    case 2 -> userData[i][j] = status;
                    case 3 -> userData[i][j] = banStatus;
                    default -> {
                    }
                }
            }
            i++;
        }

        table = new JTable(userData, columnHeaders);
        changeTable(table,2);
        table.setRowHeight(30);

        Server.registeredUsersList.removeAll(Server.registeredUsersList);
    }


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        try {
            UserManagement_GUI test = new UserManagement_GUI();
            test.run();
        } catch (Exception e) {
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

    public void changeTable(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st_val = table.getValueAt(row, 2).toString();

                if (st_val.equals("online")) {
                    c.setBackground(Color.GREEN);
                } else {
                    c.setBackground(Color.RED);
                }
                return c;
            }
        });
    }
}

