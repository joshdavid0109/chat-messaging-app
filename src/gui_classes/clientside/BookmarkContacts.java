package gui_classes.clientside;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server_side.Server;
import shared_classes.XMLParse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class BookmarkContacts extends JPanel {
    private final ArrayList<String> bookmarkedContacts;
    private final JList<String> contactList;
    private final JLabel bookmarkedContactsLabel;

    public BookmarkContacts() {
        bookmarkedContacts = new ArrayList<>();
        contactList = new JList<>();
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton bookmarkButton = new JButton("Bookmark");
        bookmarkButton.setBackground(Color.black);
        bookmarkButton.setOpaque(false);
        bookmarkButton.setBorder(BorderFactory.createEmptyBorder());
        bookmarkButton.setFocusable(false);

        bookmarkedContactsLabel = new JLabel("Bookmarked Contacts:");

        bookmarkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedContact = contactList.getSelectedValue();
                if (selectedContact != null) {
                    if (bookmarkedContacts.contains(selectedContact)) {
                        bookmarkedContacts.remove(selectedContact);
                        bookmarkButton.setText("Bookmark");
                    } else {
                        bookmarkedContacts.add(selectedContact);
                        bookmarkButton.setText("Unbookmark");
                    }
                    updateBookmarkedContactsLabel();
                    updateContactList(XMLParse.getAllContactNames().toArray(new String[0]));
                }
            }
        });

        contactList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String selectedContact = contactList.getSelectedValue();
                if (selectedContact != null) {
                    if (bookmarkedContacts.contains(selectedContact)) {
                        bookmarkButton.setText("Unbookmark");
                    } else {
                        bookmarkButton.setText("Bookmark");
                    }
                }
            }
        });

        add(new JScrollPane(contactList));
        add(bookmarkButton);
        add(bookmarkedContactsLabel);
    }

    private void updateContactList(String[] allContacts) {
        ArrayList<String> contactsList = new ArrayList<>();

        bookmarkedContacts.sort(String.CASE_INSENSITIVE_ORDER);
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

}

