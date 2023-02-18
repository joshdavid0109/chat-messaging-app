package client_side;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class BookmarkContacts extends JPanel {
    private final ArrayList<String> bookmarkedContacts;
    private final JList<String> contactList;
    private final JLabel bookmarkedContactsLabel;

    public BookmarkContacts() {
        bookmarkedContacts = new ArrayList<>();
        contactList = new JList<>(getAllContacts());
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//        Icon bookmarkIcon = new ImageIcon(new ImageIcon("src/groupniSir/Updates/BookmarkIcon.png")
//                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        JButton bookmarkButton = new JButton("CLICK");
        bookmarkButton.setBackground(Color.black);
        bookmarkButton.setOpaque(false);
        bookmarkButton.setBorder(BorderFactory.createEmptyBorder());
        bookmarkButton.setFocusable(false);
        bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");

        bookmarkButton.addActionListener(e -> {
            String selectedContact = contactList.getSelectedValue();
            if (selectedContact != null) {
                if (bookmarkedContacts.remove(selectedContact)) {
                    bookmarkButton.setToolTipText("Add to bookmark");
                } else {
                    bookmarkedContacts.add(selectedContact);
                    bookmarkButton.setToolTipText("Remove from bookmark");
                }
                updateBookmarkedContactsLabel();
                updateContactList(getAllContacts());
            }
        });
        bookmarkButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                String selectedContact = contactList.getSelectedValue();
                if (selectedContact != null) {
                    bookmarkButton.setToolTipText(bookmarkedContacts.contains(selectedContact) ? "Remove from bookmark" : "Add to bookmark");
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                bookmarkButton.setToolTipText(null);
            }
        });

        add(new JScrollPane(contactList));
        add(bookmarkButton);
        add(bookmarkedContactsLabel);
    }

    private String[] getAllContacts() {

        String[] contacts;
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse("res/users.xml");
            NodeList userNodes = document.getElementsByTagName("User");

            contacts = new String[userNodes.getLength()];

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node userNode = userNodes.item(i);
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    String name = userElement.getElementsByTagName("name").item(0).getTextContent();
                    contacts[i] = "Name: " + name;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return contacts;
    }

    private void updateContactList(String[] allContacts) {
        ArrayList<String> contactsList = new ArrayList<>();

        for (String contact : bookmarkedContacts) {
            if (contactsList.contains(contact)) {
                continue;
            }
            contactsList.add(contact);
        }

        for (String contact : allContacts) {
            if (contactsList.contains(contact)) {
                continue;
            }
            contactsList.add(contact);
        }
        contactList.setListData(contactsList.toArray(new String[0]));
    }

    private void updateBookmarkedContactsLabel() {
        bookmarkedContactsLabel.setText("Bookmarked Contacts: " + bookmarkedContacts.toString());
    }

    // TEST
    public static void main(String[] args) {
        BookmarkContacts bookmarks = new BookmarkContacts();
        JFrame frame = new JFrame("Contacts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(bookmarks);
        frame.pack();
        frame.setVisible(true);
    }
}
