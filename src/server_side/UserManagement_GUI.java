package server_side;

import shared_classes.User;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

public class UserManagement_GUI {
    User user;
    JList<User> list;

    public UserManagement_GUI(/*User user*/) { this.user = user; }

    public void run() {
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


        populateList();
        JPanel listOfUsers = new JPanel();
        listOfUsers.add(new JScrollPane(list));
        listOfUsers.setBounds(200, 190, 200, 500);


        frameUM.add(startServer);
        frameUM.add(addUser);
        frameUM.add(deleteUser);
        frameUM.add(banUser);
        frameUM.add(unbanUser);
        frameUM.add(listOfUsers);
    }

    private void populateList() {
        list = new JList<>();
        Server.getRegisteredUsers();
        list.setListData(Server.registeredUsersList.toArray(new User[0]));
    }

    public static void main(String[] args) {
        UserManagement_GUI test = new UserManagement_GUI();
        test.run();
    }

}
