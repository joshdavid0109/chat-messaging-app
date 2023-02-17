import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame implements ActionListener {
    Container container = getContentPane();
    JTextField userTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");
    JCheckBox showPassword = new JCheckBox("Show Password");
    JLabel titleLabel = new JLabel("Budget Discord");
    JLabel usernameLabel = new JLabel("Username");
    JLabel passwordLabel = new JLabel("Password");

    LoginGUI() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();

        pack();
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

    public static void main(String[] args) {
        LoginGUI frame = new LoginGUI();
        frame.setTitle("Budget Discord");
        frame.setVisible(true);
        frame.setBounds(500, 250, 960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}