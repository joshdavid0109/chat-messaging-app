module com.example.guifinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.xml;
    requires java.desktop;

    opens com.example.guifinal to javafx.fxml;
    exports fxGUI;
    opens fxGUI to javafx.fxml;
}