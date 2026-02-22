package com.example.foodprice;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private VBox sidebarRoot; // FXML এ এই আইডিটি থাকতে হবে

    // কোন পেজটি অ্যাক্টিভ তা মনে রাখার জন্য স্ট্যাটিক ভেরিয়েবল
    public static String activeButtonId = "btnDashboard";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // পেজ লোড হওয়ার পর সঠিক বাটনটিকে অ্যাক্টিভ (সবুজ) করবে
        Platform.runLater(() -> {
            if (sidebarRoot != null) {
                Node activeNode = sidebarRoot.lookup("#" + activeButtonId);
                if (activeNode instanceof Button) {
                    activeNode.getStyleClass().add("active");
                }
            }
        });
    }

    @FXML void goToDashboard(ActionEvent event) { navigate(event, "/dashboard.fxml"); }
    @FXML void goToProducts(ActionEvent event) { navigate(event, "/products.fxml"); }
    @FXML void goToPriceTracker(ActionEvent event) { navigate(event, "/price_tracker.fxml"); }
    @FXML void goToSupplyChain(ActionEvent event) { navigate(event, "/supply_chain.fxml"); }
    @FXML void goToWarehouse(ActionEvent event) { navigate(event, "/warehouse.fxml"); }
    @FXML void goToImport(ActionEvent event) { navigate(event, "/import.fxml"); }
    @FXML void goToFarmers(ActionEvent event) { navigate(event, "/farmers.fxml"); }
    @FXML void goToWeather(ActionEvent event) { navigate(event, "/weather.fxml"); }
    @FXML void goToAlerts(ActionEvent event) { navigate(event, "/alerts.fxml"); }
    @FXML void goToForecast(ActionEvent event) { navigate(event, "/forecast.fxml"); }
    @FXML void goToReports(ActionEvent event) { navigate(event, "/reports.fxml"); }
    @FXML void goToAudit(ActionEvent event) { navigate(event, "/audit.fxml"); }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            // যে বাটনে ক্লিক করা হয়েছে, তার আইডি সেভ করে রাখা হচ্ছে
            Button clickedButton = (Button) event.getSource();
            activeButtonId = clickedButton.getId();

            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Navigation Error");
                alert.setContentText("Page not found: " + fxmlPath);
                alert.showAndWait();
                return;
            }
            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) clickedButton.getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1400, 900));
            } else {
                currentScene.setRoot(root);
            }

            root.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(240), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
