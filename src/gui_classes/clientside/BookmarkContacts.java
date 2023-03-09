package gui_classes.clientside;

import shared_classes.XMLParse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class BookmarkContacts extends JPanel implements Runnable{
    private ArrayList<String> bookmarkedContacts;
    private JList<String> contactList;
    private JLabel bookmarkedContactsLabel;

    public static void main(String[] args) {
        BookmarkContacts bookmarks = new gui_classes.clientside.BookmarkContacts();
        JFrame frame = new JFrame("Contacts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(bookmarks);
        frame.setVisible(true);
    }

    public void run() {
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

