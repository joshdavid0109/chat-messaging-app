package gui_classes.clientside;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;


public class SearchingContacts extends JFrame{
    private final JTextField searchField;
    private final JLabel resultLabel;
    private final HashMap<String, String> contacts;


    public SearchingContacts() {

        super("Search");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchListener());
        resultLabel = new JLabel();

        panel.add(searchField);
        panel.add(searchButton);
        panel.add(resultLabel);
        add(panel);

        contacts = parseXMLFile("res/user.xml");

    }

    private HashMap<String, String> parseXMLFile(String userFile) {
        HashMap<String, String> contacts = new HashMap<>();
        try{
            File file = new File(userFile);
            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder =
                    documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(file);

            NodeList nodeList = doc.getElementsByTagName("User");
            for(int i=0; i < nodeList.getLength(); i++){
                Element element = (Element) nodeList.item(i);
                String name = element.getElementsByTagName("name")
                        .item(0)
                        .getTextContent();
                String status = element.getElementsByTagName("status")
                        .item(0)
                        .getTextContent();
                contacts.put(name,status);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return contacts;
    }

    private void searchName(String contactName){
        if(contacts.containsKey(contactName)){
            String attribute = contacts.get(contactName);
            resultLabel.setText(attribute);
        }else{
            resultLabel.setText("User Not Found");
        }
    }

    private class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            String name = searchField.getText();
            searchName(name);
        }
    }

    public static void main(String[] args) {
        SearchingContacts search = new SearchingContacts();
        search.setVisible(true);
    }
}