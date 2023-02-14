import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI extends JFrame implements ActionListener {
    Container container = getContentPane();
    ChatGUI() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
    }
    private void setLayoutManager() {
        container.setLayout(null);
    }
    public void setLocationAndSize() {

    }
    public void addComponentsToContainer() {

    }

    public static void main (String[] args) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
