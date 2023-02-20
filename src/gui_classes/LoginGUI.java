package gui_classes;

import gui_classes.clientSide.ClientMain;
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
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

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

                    String username, password;
                    users = document.getElementsByTagName("User");
                    Element u = null;

                    for (User user: Server.registeredUsersList) {
                        if (user.username().equals(userTextField.getText())) {
                            if (user.password().equals(passwordField.getText())) {
                                ClientMain clientMain = new ClientMain(user);
                                Server.loginHandlerArraylist.add(new ClientHandler(socket));
                                clientMain.run();
                                break;
                            }
                            break;
                        }
                    }

                    /*for (int i = 0; i < users.getLength(); i++) {
                        u = (Element) users.item(i);
                        String uName = u.getElementsByTagName("Username").item(0).getTextContent();

                        if (uName.equals(username)) {
                            for (int j = 0; j < users.getLength(); j++) {
                                printWriter.println("\nPassword: ");
                                password = bufferedReader.readLine();
                                String pass = u.getElementsByTagName("Password").item(0).getTextContent();
                                String nameNode = u.getElementsByTagName("name").item(0).getTextContent();

                                if (pass.equals(password)) {
                                    name = nameNode;
                                    loginStatus = true;

                                    if (u.getElementsByTagName("BanStatus").item(0).getTextContent().equalsIgnoreCase("Banned")) {
                                        printWriter.println("Sorry. Your account is currently banned from the system.");
                                        break;
                                    }



                                    // Add users to lists
                              *//*      Server.loginHandlerArraylist.add(new ClientHandler(socket, printWriter, bufferedReader));
                                User user = new User(u.getAttribute("User"), u.getElementsByTagName("name").item(0).getTextContent(), u.getElementsByTagName("Age").item(0).getTextContent(),
                                        u.getElementsByTagName("Username").item(0).getTextContent(),
                                        u.getElementsByTagName("Password").item(0).getTextContent(), u.getElementsByTagName("status").item(0).getTextContent(), u.getElementsByTagName("BanStatus").item(0).getTextContent());

                                // IP, USER HASHMAP
                                Server.loggedInUserHashMap.put(new ClientHandler(socket, printWriter, bufferedReader), user);

                                u.getElementsByTagName("status").item(0).setTextContent("online");
                                Server.updateXML(users, document);

*//*
                                    System.out.println("Login Successful!");

                                    System.out.println(u.getElementsByTagName("name").item(0).getTextContent() + " " +  u.getElementsByTagName("status").item(0).getTextContent());


                                *//*ClientMain clientMain = new ClientMain(user);
                                clientMain.run();*//*

                     *//*joinServer(user, users);
                                broadcast(name + ": ");*//*
                                    break;
                                }
                                printWriter.println("Invalid password.");
                            }
                            break;
                        } else if (i == users.getLength() - 1)
                            printWriter.println("User is not existing");
                    }*/
                }
            });


        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }


        this.setTitle("Budget Discord");
        this.setVisible(true);

        this.setBounds(500, 250, 960, 540);
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