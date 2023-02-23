package com.example.trial;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

public class Controller {

    @FXML
    private Button bookmarkButton;

    @FXML
    private Button createGroup;

    @FXML
    private ScrollPane listMemberAndGroupPane;

    @FXML
    private Button logOutButton;

    @FXML
    private ToggleButton membersToGroupButton;

    @FXML
    private ScrollPane messagePane;

    @FXML
    private Button sendButton;

    @FXML
    private TextField textField;

    @FXML
    private Text username;

    @FXML
    void sendButton(ActionEvent event) {

    }

}
