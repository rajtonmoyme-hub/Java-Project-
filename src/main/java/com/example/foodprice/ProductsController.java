package com.example.foodprice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ProductsController {

    @FXML
    private Button btnDashboard;

    @FXML
    void goToDashboard(ActionEvent event) {
        try {
            // Load the Dashboard FXML
            URL fxmlLocation = getClass().getResource("/dashboard.fxml");
            if (fxmlLocation == null) {
                System.err.println("❌ Error: dashboard.fxml not found!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Get current stage and set new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToWarehouse(ActionEvent event) {
        try {
            URL fxmlLocation = getClass().getResource("/warehouse.fxml");
            if (fxmlLocation == null) {
                System.err.println("❌ Error: warehouse.fxml not found!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToFarmers(ActionEvent event) {
        try {
            URL fxmlLocation = getClass().getResource("/farmers.fxml");
            if (fxmlLocation == null) {
                System.err.println("❌ Error: farmers.fxml not found!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}