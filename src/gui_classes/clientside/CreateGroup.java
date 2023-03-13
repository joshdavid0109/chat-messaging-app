package gui_classes.clientside;

import shared_classes.Group;
import shared_classes.User;
import shared_classes.XMLParse;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CreateGroup extends JDialog implements Runnable{
    private JButton addButton;
    private JList<String> usersList;
    private JButton saveButton;
    private JList<String> selectedUsersList;
    private JButton removeButton;
    private JPanel panel;
    private JScrollPane usersPane;
    private JLabel groupNameLabel;
    private JTextField groupNameTF;
    private ArrayList<String> selectedUsers;

    public CreateGroup(JFrame parent, ObjectOutputStream out, User user) {
        super(parent, "Create Group", true);
        selectedUsers = new ArrayList<>();
        saveButton.setUI(new BasicButtonUI());

        Font f = new Font("Verdana", Font.BOLD, 15);
        groupNameLabel.setFont(f);
        groupNameLabel.setText("Group Name");
        groupNameTF.setFont(f);

        usersList.setListData(XMLParse.usersList(user));
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setFixedCellHeight(30);
        usersList.setCellRenderer(getRenderer());

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String selectedUser = usersList.getSelectedValue();

                boolean check =false;
                if (selectedUsers.size()>0) {
                    String temp = "";
                    for (int i = 0; i < selectedUsers.size(); i++) {
                        temp = selectedUsers.get(i);
                        if (temp.equals(selectedUser)) {
                            check =true;
                            break;
                        }
                    }
                    if (!check) {
                        selectedUsers.add(selectedUser);
                        String[] t = selectedUsers.toArray(new String[0]);
                        selectedUsersList.setSelectedIndex(-1);
                        selectedUsersList.setListData(t);
                        selectedUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        selectedUsersList.setFixedCellHeight(30);
                        selectedUsersList.setCellRenderer(getRenderer());
                    }
                } else {

                    selectedUsers.add(selectedUser);
                    String[] temp = selectedUsers.toArray(new String[0]);
                    selectedUsersList.setListData(temp);
                    selectedUsersList.setFocusable(false);
                    selectedUsersList.setSelectedIndex(-1);
                    selectedUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    selectedUsersList.setFixedCellHeight(30);
                    selectedUsersList.setCellRenderer(getRenderer());
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = selectedUsersList.getSelectedValue();
                for (int i =0; i< selectedUsers.size();i++) {
                    String temp= selectedUsers.get(i);
                    if (temp.equals(selectedUser)) {
                        selectedUsers.remove(i);
                        break;
                    }
                }
                String [] temp = selectedUsers.toArray(new String[selectedUsers.size()]);
                selectedUsersList.setListData(temp);
                selectedUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                selectedUsersList.setFixedCellHeight(30);
                selectedUsersList.setCellRenderer(getRenderer());
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(groupNameTF.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null,"please enter a group name");
                    return;
                }
                else{
                    selectedUsers.add(0, user.getName() + " @" + user.getUsername());

                    Group grupow = new Group(groupNameTF.getText(), selectedUsers, user);
                    try {
                        out.writeObject(grupow);
                        out.flush();
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                }


                //XMLParse.addGroup(selectedUsers, groupNameTF.getText());




/*                try {
                    selectedUsers.add(0, user.getName() + " - " + user.getUsername());
                    out.writeObject(new Group(groupNameLabel.getText(), selectedUsers));
                    out.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }*/
                dispose();
            }
        });


        panel.setBackground(Color.decode("#3e444f"));
        getContentPane().add(panel);
        setSize(500, 500);
        setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,Color.BLACK));
                return listCellRendererComponent;
            }
        };
    }

    @Override
    public void run() {
       this.run();
    }


}
