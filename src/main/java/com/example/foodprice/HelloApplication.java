package com.example.foodprice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Look for the file at the root of resources
        URL fxmlLocation = getClass().getResource("/dashboard.fxml");

        // 2. Debug check: If file is missing, print an error and stop
        if (fxmlLocation == null) {
            System.err.println("❌ ERROR: Cannot find dashboard.fxml.");
            System.err.println("👉 Please make sure 'dashboard.fxml' is directly inside 'src/main/resources/'");
            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1400, 900);
        stage.setTitle("Smart Food Price System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}