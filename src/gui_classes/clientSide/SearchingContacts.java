package gui_classes.clientSide;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




public class SearchingContacts extends JFrame implements ActionListener {
    private JTextField searchField;
    private JTextArea resultsArea;


    public SearchingContacts() {

        setTitle("Search");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);


        JMenu messageMenu = new JMenu("Message");
        menuBar.add(messageMenu);


        // Set up the search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());

        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Set up the results panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        resultsPanel.add(resultsScrollPane, BorderLayout.CENTER);

        // Add the search and results panels to the frame
        add(searchPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);


        setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Exit")) {
            System.exit(0);
        } else if (command.equals("Search")) {
            String query = searchField.getText();

            resultsArea.setText("Results for \"" + query);
        }
    }


    public static void main (String[]args){
            new SearchingContacts();
    }
}






