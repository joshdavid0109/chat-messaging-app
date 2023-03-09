package gui_classes.clientside;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import shared_classes.XMLParse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class SearchingContacts extends JFrame{
    private final JTextField searchField;
    private final JTextArea resultArea;

    private ArrayList<String> similarNames;

    public SearchingContacts(){
        super("Search");

        //Create search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(new EmptyBorder(10,10,10,10));
        searchPanel.setLayout(new BorderLayout());

        JLabel searchLabel = new JLabel("Contacts: ");
        searchPanel.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField(20);
        searchPanel.add(searchField,BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contactName = searchField.getText().trim();

                //Search for the exact name in XML file
                String info = XMLParse.searchXML(contactName);

                if (info.isEmpty()) {
                    // Search for similar names if exact match not found
                    similarNames = findSimilarNames(contactName);
                    if (similarNames.isEmpty()) {
                        resultArea.setText(contactName + " not found.");
                    } else if (similarNames.size() == 1) {
                        // If only one similar name found, show its info
                        info = XMLParse.searchXML(similarNames.get(0));
                        resultArea.setText(info);
                    } else {
                        // If multiple similar names found, show a list of suggestions
                        String suggestion = "Did you mean:";
                        for (String name : similarNames) {
                            suggestion += " " + name + ",";
                        }
                        suggestion = suggestion.substring(0, suggestion.length() - 1); // Remove last comma
                        resultArea.setText(suggestion);
                    }
                } else {
                    resultArea.setText(info);
                }
            }
        });
        searchPanel.add(searchButton, BorderLayout.EAST);

        //Create result area
        resultArea = new JTextArea(10,20);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        Container contentPane = getContentPane();
        contentPane.add(searchPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private ArrayList<String> findSimilarNames(String input) {
        similarNames = new ArrayList<>();
        //this one kinopya lang po namin sa internet sir,
        // i dont even know how this works
        for (String name : XMLParse.getAllContactNames()) {
            int[][] dp = new int[input.length() + 1][name.length() + 1];
            for (int i = 0; i <= input.length(); i++) {
                dp[i][0] = i;
            }
            for (int j = 0; j <= name.length(); j++) {
                dp[0][j] = j;
            }
            for (int i = 1; i <= input.length(); i++) {
                for (int j = 1; j <= name.length(); j++) {
                    int cost = (input.charAt(i - 1) == name.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(dp[i - 1][j] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost));
                }
            }
            int distance = dp[input.length()][name.length()];
            if (distance <= 2) { // Set the maximum allowed edit distance to 2
                similarNames.add(name);
            }
        }
        return similarNames;
    }

}