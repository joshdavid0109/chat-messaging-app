import javax.swing.*;

public class LoginGui {
    public static void main(String[] args) {

        gui frame = new gui();
        frame.setTitle("Budget Discord");
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setBounds(500, 250, 960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

    }
}