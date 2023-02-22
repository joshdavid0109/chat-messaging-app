package gui_classes.clientside;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SearchingContacts extends JFrame{
    private final JTextField searchField;
    private final JTextArea resultArea;

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

                //Search for the name in XML file
                String info = searchXML(contactName);

                if(info.isEmpty()){
                    resultArea.setText(contactName + "  not found.");
                }else{
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
    }

    public String searchXML(String name){
        try{
            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder =
                    documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");

            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("User");

            for(int i = 0; i < nodeList.getLength(); i++){
                Node nNode = nodeList.item(i);

                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) nNode;

                    String userName = element
                            .getElementsByTagName("name")
                            .item(0)
                            .getTextContent();

                    if(userName.equalsIgnoreCase(name)){
                        String info = "Name: " + userName + "\n";
                        info += "User name: " +element.getElementsByTagName("Username")
                                .item(0)
                                .getTextContent() +"\n";

                        info += "Status: " +element.getElementsByTagName("status")
                                .item(0)
                                .getTextContent() +"\n";

                        return info;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args){
        new SearchingContacts();
    }

}