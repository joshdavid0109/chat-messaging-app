package fxGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("simpGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        primaryStage.setResizable(false);
        primaryStage.setTitle("gui");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
