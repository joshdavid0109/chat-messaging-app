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

    LoginGUI() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
    }
    private void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        userTextField.setBounds(300, 150, 360, 30);
        passwordField.setBounds(300, 200, 360, 30);
        loginButton.setBounds(300, 270, 360, 30);
        titleLabel.setBounds(440,110,360,30);
        showPassword.setBounds(425,235,360,30);
    }

    public void addComponentsToContainer() {
        container.add(userTextField);
        container.add(passwordField);
        container.add(loginButton);
        container.add(titleLabel);
        container.add(showPassword);
    }

    public static void main(String[] args) {

        LoginGUI frame = new LoginGUI();
        frame.setTitle("Budget Discord");
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setBounds(500, 250, 960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}