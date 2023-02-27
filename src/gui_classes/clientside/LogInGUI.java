package gui_classes.clientside;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInGUI extends JDialog {
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton okButton;
    private JButton cancelButton;
    private int result;

    public static final int OK = 1;
    public static final int CANCEL = 0;

    public LogInGUI(JFrame parent) {
        super(parent, "Login", true);
        JPanel panel = new JPanel(new GridLayout(3, 2));
        usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = OK;
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = CANCEL;
                dispose();
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(okButton);
        panel.add(cancelButton);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    public int showDialog() {
        setVisible(true);
        return result;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /*public static void main(String[] args) {
        new LogInGUI(null);
    }*/
}
