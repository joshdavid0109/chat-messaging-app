package gui_classes.serverside;

import shared_classes.User;

import javax.swing.*;
import java.awt.*;

public class ServerMain implements Runnable{
    User user;


    public ServerMain(User user) {
        this.user = user;
    }
    @Override
    public void run() {

        JLabel headerName = new JLabel();
        headerName.setText(user.username());
        headerName.setForeground(Color.WHITE);
        headerName.setFont(new Font("Arial", Font.BOLD, 12));
        headerName.setBounds(920, -15, 200, 75);

        JLabel headerStatus = new JLabel();
        headerStatus.setText("Online");
        headerStatus.setForeground(Color.GREEN);
        headerStatus.setFont(new Font("Arial", Font.BOLD, 10));
        headerStatus.setBounds(920, 0, 200, 75);

        JLabel broadCast = new JLabel();
        broadCast.setText("BROADCAST");
        broadCast.setForeground(Color.WHITE);
        broadCast.setFont(new Font("Arial", Font.BOLD, 18));
        broadCast.setBounds(100, 0, 200, 75);

        JLabel currentUserName = new JLabel();
        currentUserName.setText(user.name());
        currentUserName.setForeground(Color.WHITE);
        currentUserName.setFont(new Font("Arial", Font.BOLD, 18));
        currentUserName.setBounds(30, 0, 200, 75);

        JLabel currentUserStatus = new JLabel();
        currentUserStatus.setText("online");
        currentUserStatus.setForeground(Color.GREEN);
        currentUserStatus.setFont(new Font("Arial", Font.PLAIN, 18));
        currentUserStatus.setBounds(270, 0, 200, 75);

        JLabel listOfMembersName = new JLabel();
        listOfMembersName.setText("MEMBERS");
        listOfMembersName.setForeground(Color.WHITE);
        listOfMembersName.setFont(new Font("Arial", Font.BOLD, 18));
        listOfMembersName.setBounds(90, 0, 200, 75);

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

        frame.add(privateMessagePanel);
        privateMessagePanel.add(currentUserName);
        privateMessagePanel.add(currentUserStatus);

        frame.add(listOfMembers);
        listOfMembers.add(listOfMembersName);
    }
}
