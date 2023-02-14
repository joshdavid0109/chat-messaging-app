import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI extends JFrame implements ActionListener {
    Container container = getContentPane();
    JPanel chatPanel = new JPanel();
    JTextArea chatArea = new JTextArea();
    JTextField inputField = new JTextField();
    JPanel sidePanel = new JPanel();
    JList<String> serverList = new JList<>();
    JList<String> userList = new JList<>();
    ChatGUI() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
    }
    private void setLayoutManager() {
        container.setLayout(new BorderLayout());
        chatPanel.setLayout(new BorderLayout());
        sidePanel.setLayout(new BorderLayout());

        chatArea.setEditable(false);
    }
    public void setLocationAndSize() {

    }
    public void addComponentsToContainer() {
        container.add(chatPanel, BorderLayout.CENTER);
        container.add(sidePanel, BorderLayout.EAST);

        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(inputField, BorderLayout.SOUTH);

        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sidePanel.add(new JScrollPane(serverList), BorderLayout.CENTER);

        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sidePanel.add(new JScrollPane(userList), BorderLayout.SOUTH);
    }

    public static void main (String[] args) {
        ChatGUI frame = new ChatGUI();
        frame.setTitle("Budget Discord");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setBounds(500, 250, 960, 540);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
