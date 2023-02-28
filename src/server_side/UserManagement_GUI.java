package server_side;

import org.xml.sax.SAXException;
import shared_classes.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

public class UserManagement_GUI {
    User user;
    JList<User> list;
    DefaultTableModel model;
    JScrollPane userScroll = new JScrollPane();
    JTable table;
    int port;

    public UserManagement_GUI(/*User user*/) { this.user = user; }

    public void run() throws IOException, SAXException, ParserConfigurationException {
        JFrame frameUM = new JFrame();
        frameUM.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                boolean validPort = false;
                while (!validPort) {
                    try {
                        port = Integer.parseInt(JOptionPane.showInputDialog(new JPanel(), "Input port: ", JOptionPane.YES_NO_CANCEL_OPTION));
                        if (port == JOptionPane.CANCEL_OPTION) {
                            break;
                        }
                        validPort = true;
                    }  catch(NumberFormatException e) {

                        JOptionPane.showMessageDialog(new JPanel(), "VALID NUMBER PLEASE", "Errror Message", JOptionPane.ERROR_MESSAGE);
                        System.out.println(e.getMessage());
                    } catch (NullPointerException e) {
                        System.out.println(e.getMessage());
                        JOptionPane.showMessageDialog(new JPanel(), "INPUT A VALID PORT", "Error message", JOptionPane.ERROR_MESSAGE);
                        System.out.println(e.getMessage());
                    } catch(Exception e) {
                        JOptionPane.showMessageDialog(new JPanel(), "TRY AGAIN.", "Error message", JOptionPane.ERROR_MESSAGE);

                        System.out.println(e.getMessage());
                    }
                    Server server = new Server(port);
                    break;
                }
                //Pag nagrun, hindi na mapindot other features.. HEHE IDK
            }
        });



        JButton addUser = new JButton("Add User");
        addUser.setBounds(570, 80, 150, 45);
        addUser.setBorder(BorderFactory.createEtchedBorder(000000));
        addUser.setForeground(Color.black);
        addUser.setBackground(Color.WHITE);

        JButton deleteUser = new JButton("Delete User");
        deleteUser.setBounds(570, 135, 150, 45);
        deleteUser.setBorder(BorderFactory.createEtchedBorder(000000));
        deleteUser.setForeground(Color.black);
        deleteUser.setBackground(Color.WHITE);

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
                userScroll.removeAll();
                populateList();
                userScroll = new JScrollPane(table);
                userScroll.setBounds(35, 30, 500, 400);


                frameUM.add(userScroll);
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
        Server.getRegisteredUsers();


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
        UserManagement_GUI test = new UserManagement_GUI();
        test.run();
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

class MyTableCellRenderer extends DefaultTableCellRenderer   {
    @Override
    public Color getBackground() {
        return super.getBackground();
    }


}
