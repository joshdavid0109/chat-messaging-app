package server_side;

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
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.table.DefaultTableCellRenderer;

import static java.awt.Color.*;

public class UserManagement_GUI extends JFrame{
    private User user;
    private JScrollPane userScroll = new JScrollPane();
    private JTable table;
    private int port;
    private XMLParse xmlParse;
    public static JLabel portNumber;

    public UserManagement_GUI(/*User user*/) { this.user = user; }

    public void run(){
        xmlParse = new XMLParse();
        JFrame frameUM = new JFrame();
        frameUM.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                XMLParse.setEveryoneOffline();
                System.exit(0);
            }
        });

        frameUM.setLayout(null);
        frameUM.setVisible(true);
        frameUM.setResizable(false);
        frameUM.setTitle("Budget Discord User Management");
        frameUM.setSize(755, 500);
        frameUM.getContentPane().setBackground(decode("#3e444f"));

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frameUM.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frameUM.getHeight()) / 2);
        frameUM.setLocation(x, y);


        portNumber = new JLabel("Port: ");
        portNumber.setForeground(white);
        portNumber.setBounds(610, 30, 175, 45);

        AtomicBoolean serverStatus = new AtomicBoolean(false); // false offline
                              // true online

        AtomicReference<Server> server = null;
        JSplitPane serverSwitch = new JSplitPane();
        serverSwitch.setDividerSize(0);
        serverSwitch.setResizeWeight(0.5);

        //start button
        serverSwitch.setLeftComponent(new JButton("Start"));
        serverSwitch.getLeftComponent().setEnabled(true);
        serverSwitch.getLeftComponent().setBackground(decode("#149639"));
        JButton leftComponent = (JButton) serverSwitch.getLeftComponent();
        leftComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!serverStatus.get()) {

                    serverSwitch.getRightComponent().setEnabled(true);
                    serverSwitch.getRightComponent().setBackground(decode("#BC544B"));
                    new Thread(() -> {
                        server.set(new Server(port, frameUM));

                    }).start();
                    if (Server.serverSocket!=null) {
                        serverStatus.set(true);
                    }
                } else
                    JOptionPane.showMessageDialog(null, "Server is already running at port " + Server.port,
                            "Server Status", JOptionPane.ERROR_MESSAGE, null);
            }
        });

        //stop button
        serverSwitch.setRightComponent(new JButton("Stop"));
        serverSwitch.getRightComponent().setBackground(Color.gray);
        serverSwitch.getRightComponent().setEnabled(false);
        JButton rightComponent = (JButton)serverSwitch.getRightComponent();
        rightComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Server.serverSocket.close();
                    for (ClientHandler c: Server.loginHandlerArraylist
                         ) {
                        c.clientSocket.close();
                    }
                    serverStatus.set(false);
                    serverSwitch.getRightComponent().setEnabled(false);
                    rightComponent.setBackground(gray);
                    portNumber.setText("Port: ");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        serverSwitch.setBounds(557, 80, 175, 45);


        JButton addUser = new JButton("Add User");
        addUser.setBounds(570, 130, 150, 45);
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
        deleteUser.setBounds(570, 180, 150, 45);
        deleteUser.setBorder(BorderFactory.createEtchedBorder(000000));
        deleteUser.setForeground(Color.black);
        deleteUser.setBackground(Color.WHITE);

        deleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object [] options = {"Yes", "No"};
                System.out.println(table.getSelectedRow());
                if (table.getSelectedRow() ==-1){
                    JOptionPane.showMessageDialog(null, "Select a row at the table to delete.", "User Deletion", JOptionPane.ERROR_MESSAGE, null);
                    return;
                }

                String selectedUser = (String) table.getValueAt(table.getSelectedRow(), 0);

                int c = JOptionPane.showOptionDialog(null, "Are you sure you want to remove " + selectedUser + "?", "User Deletion",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

                if (c == 0) {
                    String nameToDelete = (String) table.getValueAt(table.getSelectedRow(), 1);
                    xmlParse.deleteUser(nameToDelete);
                    frameUM.remove(userScroll);
                    populateList();
                    userScroll = new JScrollPane(table);
                    userScroll.setBounds(35, 30, 500, 400);

                    frameUM.add(userScroll);
                }
            }
        });

        JButton banUser = new JButton("Ban User");
        banUser.setBounds(570, 230, 150, 45);
        banUser.setBorder(BorderFactory.createEtchedBorder(000000));
        banUser.setForeground(Color.black);
        banUser.setBackground(decode("#cc4949"));

        banUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(table.getSelectedRow());
                if (table.getSelectedRow() ==-1){
                    JOptionPane.showMessageDialog(null, "Select a row at the table to ban.", "User ban", JOptionPane.ERROR_MESSAGE, null);
                }
                else{
                    //username of user
                    String selectedUser = (String) table.getValueAt(table.getSelectedRow(), 1);
                    xmlParse.setBanStatus(selectedUser, "BANNED");
                    frameUM.remove(userScroll);
                    populateList();
                    userScroll = new JScrollPane(table);
                    userScroll.setBounds(35, 30, 500, 400);

                    frameUM.add(userScroll);
                }
            }
        });

        JButton unbanUser = new JButton("Unban User");
        unbanUser.setBounds(570, 280, 150, 45);
        unbanUser.setBorder(BorderFactory.createEtchedBorder(000000));
        unbanUser.setForeground(Color.black);
        unbanUser.setBackground(decode("#c2b225"));
        unbanUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() ==-1){
                    JOptionPane.showMessageDialog(null, "Select a row at the table to unban.", "User unban", JOptionPane.ERROR_MESSAGE, null);
                }
                else{
                    //username of user
                    String selectedUser = (String) table.getValueAt(table.getSelectedRow(), 1);
                    xmlParse.setBanStatus(selectedUser, "");
                    frameUM.remove(userScroll);
                    populateList();
                    userScroll = new JScrollPane(table);
                    userScroll.setBounds(35, 30, 500, 400);

                    frameUM.add(userScroll);
                }
            }
        });

        JButton refresh = new JButton("Refresh");
        refresh.setBounds(570, 400, 150, 45);
        refresh.setBorder(BorderFactory.createEtchedBorder(000000));
        refresh.setForeground(Color.black);
        refresh.setBackground(decode("#FF7F50"));

        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameUM.remove(userScroll);
                populateList();
                userScroll = new JScrollPane(table);
                userScroll.setBounds(35, 30, 500, 400);

                frameUM.add(userScroll);
            }
        });

        frameUM.add(portNumber);
        frameUM.add(serverSwitch);
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

        Server.updateUsersList(); // update list of users in case there are changes

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

        //Add data at column headers to table
        table = new JTable(userData, columnHeaders);
        changeTable(table,2);
        table.setRowHeight(30);

        Server.registeredUsersList.removeAll(Server.registeredUsersList);
    }


    public static void main(String[] args){
        try {
            UserManagement_GUI test = new UserManagement_GUI();
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            XMLParse.setEveryoneOffline();
        }*/
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

