package bookmarkContacts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class bookmarkContacts extends JPanel {
    private ArrayList<String> bookmarkedContacts;
    private JList<String> contactList;
    private JButton bookmarkButton;
    private JLabel bookmarkedContactsLabel;
    private JScrollPane contactListScrollPane;

    public bookmarkContacts() {
        bookmarkedContacts = new ArrayList<>();
        contactList = new JList<>(getAllContacts());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactListScrollPane = new JScrollPane(contactList);

        Icon icon = new ImageIcon(new ImageIcon("src/bookmarkContacts/bookmarkIcon.png")
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        bookmarkButton = new JButton(icon);
        bookmarkButton.setBackground(Color.black);
        bookmarkButton.setOpaque(false);

        bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");

        bookmarkButton.addActionListener(event -> {
            int selectedIndex = contactList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedContact = contactList.getModel().getElementAt(selectedIndex);
                bookmarkedContacts.add(selectedContact);
                updateBookmarkedContactsLabel();
            }
        });

        add(contactListScrollPane);
        add(bookmarkButton);
        add(bookmarkedContactsLabel);
    }

    private String[] getAllContacts() {

        String[] contacts = null;
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("users.xml");
            NodeList userNodes = document.getElementsByTagName("User");

            contacts = new String[userNodes.getLength()];

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;

                    String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                    String age = userElement.getElementsByTagName("Age").item(0).getTextContent();

//                    String username = userElement.getElementsByTagName("Username").item(0).getTextContent();
//                    String password = userElement.getElementsByTagName("Password").item(0).getTextContent();


                    contacts[i] = "Name: " + name + ", Age: " + age ;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return contacts;
    }


    private void updateBookmarkedContactsLabel() {
        bookmarkedContactsLabel.setText("Bookmarked Contacts: " + bookmarkedContacts.toString());
    }


    public static void main(String[] args) {
        bookmarkContacts bookmarks = new bookmarkContacts();
        JFrame frame = new JFrame("Bookmark Contacts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(bookmarks);
        frame.pack();
        frame.setVisible(true);
    }


}
