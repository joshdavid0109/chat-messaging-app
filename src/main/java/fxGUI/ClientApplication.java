package fxGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

    // read the file simpGUI.fxml
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("E:\\Program Files\\IdeaProjects\\last2days\\src\\main\\resources\\com\\example\\guifinal\\simpGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        stage.setResizable(false);
        stage.setTitle("gui");
        stage.setScene(scene);
        stage.show();
    }

    // main method para ma run program
    public static void main(String[] args) {
        launch();
    }
}