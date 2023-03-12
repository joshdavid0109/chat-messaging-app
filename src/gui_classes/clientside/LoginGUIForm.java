package gui_classes.clientside;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.LoginCredentials;
import shared_classes.User;
import shared_classes.XMLParse;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static server_side.Server.*;
import static server_side.Server.loggedInUserHashMap;

public class LoginGUIForm extends JDialog implements Runnable{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JRadioButton showPassword;
    private JPanel panel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    private File file;

    public static final int OK = 1;
    public static final int CANCEL = 1;


    public LoginGUIForm(JFrame parent, ObjectOutputStream out) {
        super(parent, "Login", true);
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
                    try {
                        out.writeObject(new LoginCredentials(getUsername(), getPassword()));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    dispose();
            }
        });


        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setVisible(true);

    }

    public LoginGUIForm(Object parent) {
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }


    @Override
    public void run() {
        new LoginGUIForm(null);
    }

    public static void main(String[] args) {
        new LoginGUIForm(null);
    }
}
