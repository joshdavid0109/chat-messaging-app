package server_side;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import shared_classes.User;
import shared_classes.XMLParse;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUserHandler extends JDialog implements Runnable{
    private JTextField usernameTF;
    private JTextField ageTF;
    private JPasswordField passwordPF;
    private JButton confirmButton;
    private JPasswordField confirmPasswordPF;
    private JPanel addUserPanel;
    private JLabel usernameAuth;
    private JLabel ageAuth;
    private JLabel passAuth;
    private JLabel conPassAuth;
    private JTextField nameTF;
    private JLabel nameAuth;

    public AddUserHandler(JFrame parent) {
        super(parent, "Login", true);
        XMLParse parse = new XMLParse("users.xml");


        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username, name, age, password, confirmPassword;
                username = usernameTF.getText();
                name = nameTF.getText();
                age = ageTF.getText();
                password = passwordPF.getText();
                confirmPassword = confirmPasswordPF.getText();

                try {
                    boolean loginstat = false;
                    while (!loginstat){
                        if (username.equals("") || name.equals("") || isDuplicate(username) || !isNumeric(age) || (password.length() > 16 || password.length() < 8) ||
                                !password.equals(confirmPassword)) {
                            if (username.equals(""))
                                usernameAuth.setText("Username field is empty.");
                            else if (isDuplicate(username))
                                usernameAuth.setText("Username is already taken");
                            if (age.equals(""))
                                ageAuth.setText("Age field is empty");
                            else if (!isNumeric(age))
                                ageAuth.setText("Invalid age");
                            if (name.equals(""))
                                nameAuth.setText("Name field is empty.");
                            if (password.equals(""))
                                passAuth.setText("Password field is empty.");
                            else if (password.length() > 16 || password.length() < 8)
                                passAuth.setText("Password must contain 8-16 characters");
                            if (!password.equals(confirmPassword))
                                conPassAuth.setText("Password does not match.");
                        } else {
                            UUID randomID = UUID.randomUUID();
                            parse.addUser(new User(randomID.toString(), name, age, username, password, "offline", ""));
                            System.out.println("user " + name + " added");
                            loginstat = true;
                            dispose();
                        }
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });



        getContentPane().add(addUserPanel);
        setSize(500, 600);
        setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void run() {
        new AddUserHandler(null);
    }

    public static void main(String[] args) {
        new AddUserHandler(null);
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private boolean passWordValidation(String password) {

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }

        Matcher m = p.matcher(password);

        return m.matches();
    }

    private static boolean isDuplicate(String userName) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document users = documentBuilder.parse(new File("res/users.xml"));

        NodeList nodeList = users.getElementsByTagName("Username");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Objects.equals(node.getTextContent(), userName)) {
                return true;
            }
        }

        return false;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
