package com.example.foodprice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Start app from user login page.
        URL fxmlLocation = getClass().getResource("/user_login.fxml");

        if (fxmlLocation == null) {
            System.err.println("ERROR: Cannot find user_login.fxml.");
            System.err.println("Please make sure 'user_login.fxml' is directly inside 'src/main/resources/'");
            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Food Price Control & Agriculture Product Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
