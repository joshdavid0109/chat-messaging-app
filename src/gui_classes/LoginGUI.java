package gui_classes;

import gui_classes.clientside.ClientMain;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.ClientHandler;
import server_side.Server;
import shared_classes.User;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginGUI extends JFrame implements ActionListener, Runnable {
    Container container = getContentPane();
    JTextField userTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");
    JCheckBox showPassword = new JCheckBox("Show Password");
    JLabel titleLabel = new JLabel("Budget Discord");
    JLabel usernameLabel = new JLabel("Username");
    JLabel passwordLabel = new JLabel("Password");
    Socket socket;

    public LoginGUI(){

    }

    public LoginGUI(Socket socket) {
        this.socket = socket;
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        pack();

    }

    public void run(){
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(Server.f);



            showPassword.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED)
                        passwordField.setEchoChar('\u0000');
                    else
                        passwordField.setEchoChar((Character) UIManager.get("PasswordField.echoChar"));
                }
            });

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NodeList users = document.getElementsByTagName("User");
                    PrintWriter printWriter = null;
                    try {
                        printWriter = new PrintWriter(socket.getOutputStream());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    String username, password;
                    Frame frame;
                            users = document.getElementsByTagName("User");
                    Element u;

                    int i = 0;
                    for (User user: Server.registeredUsersList) {
                        u = (Element) users.item(i);

                        if (user.username().equals(userTextField.getText())) {
                            if (user.password().equals(passwordField.getText())) {
                                try {
                                    Server.loginHandlerArraylist.add(new ClientHandler(socket));
                                    Server.loggedInUserHashMap.put(new ClientHandler(socket), user);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                u.getElementsByTagName("status").item(0).setTextContent("online");
                                ClientMain clientMain = new ClientMain(socket, user, printWriter);
                                clientMain.run();
                                dispose();
                                break;
                            } else
                                JOptionPane.showMessageDialog(container, "User/Password is invalid.");
                            break;
                        }
                        i++;

                        if (i == Server.registeredUsersList.size()-1)
                            JOptionPane.showMessageDialog(new JFrame(), "User is not existing."," ", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });


        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }


        this.setTitle("Budget Discord");
        this.setVisible(true);

        this.setBounds(500, 250, 930, 540);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);



    }
    private void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        usernameLabel.setBounds(305, 125, 360, 30);
        userTextField.setBounds(300, 150, 360, 30);
        passwordLabel.setBounds(305, 175,360,30);
        passwordField.setBounds(300, 200, 360, 30);
        loginButton.setBounds(300, 270, 360, 30);
        titleLabel.setBounds(440,100,360,30);
        showPassword.setBounds(425,235,360,30);

    }

    public void addComponentsToContainer() {
        container.add(userTextField);
        container.add(passwordField);
        container.add(loginButton);
        container.add(titleLabel);
        container.add(showPassword);
        container.add(usernameLabel);
        container.add(passwordLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}