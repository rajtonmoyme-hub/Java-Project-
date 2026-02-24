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

// Ei class ta app er pasher menu (sidebar) control kore. Kon page theke kon page e jabe ta eikhan theke thik hoy.
public class SidebarController implements Initializable {

    // Sidebar er main container (box) ta eikhane connect kora hocche
    @FXML private VBox sidebarRoot;

    // যে বাটনগুলো ইউজারদের থেকে লুকানো হবে (FXML এ এই ID গুলো থাকতে হবে)
    @FXML private Button btnPriceTracker;
    @FXML private Button btnWeather;
    @FXML private Button btnAlerts;
    @FXML private Button btnForecast;
    @FXML private Button btnReports;
    @FXML private Button btnAudit;

    // Ekhon kon page ta (ba menu ta) open ache seta mone rakhar jonno ekta static variable, by default Dashboard set kora ache
    public static String activeButtonId = "btnDashboard";

    // Sidebar ta screen e load holei ei method ta automatically run hobe
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Role based visibility: Jodi sadharon user hoy, tahole nicher menu/button gulo hide kore dibe
        if (UserSession.isUser()) {
            hideMenu(btnPriceTracker);
            hideMenu(btnWeather);
            hideMenu(btnAlerts);
            hideMenu(btnForecast);
            hideMenu(btnReports);
            hideMenu(btnAudit);
        }

        // UI ta fully load howar por kon menu ta active ache seta highlight korar jonno Platform.runLater use kora hoyeche
        Platform.runLater(() -> {
            if (sidebarRoot != null) {
                // activeButtonId diye oi button ta khuje ber korche
                Node activeNode = sidebarRoot.lookup("#" + activeButtonId);
                // Jodi button ta pawa jay, tahole tar sathe "active" namer CSS class ta add kore dicche jate color/design change hoy
                if (activeNode instanceof Button) {
                    activeNode.getStyleClass().add("active");
                }
            }
        });
    }

    // Kono ekta button (menu) screen theke hide korar helper method. Eta jagao nibe na (setManaged) ar dekhao jabe na (setVisible)
    private void hideMenu(Button btn) {
        if (btn != null) {
            btn.setVisible(false);
            btn.setManaged(false);
        }
    }

    // Menu er button gulote click korle ei nicher method gulo run hobe ar kon page e jabe tar path bole dibe
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

    // Asol page change korar (navigation) kaj ta eikhane hoy
    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            // Je button e click kora hoyeche seta ke identify korche
            Button clickedButton = (Button) event.getSource();
            // activeButtonId ke notun click kora button er id diye update kore dicche
            activeButtonId = clickedButton.getId();

            // Notun page er fxml file ta khuje ber korche
            URL fxmlLocation = getClass().getResource(fxmlPath);

            // Jodi file ta na pawa jay tahole ekta error popup/alert dekhabe
            if (fxmlLocation == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Navigation Error");
                alert.setContentText("Page not found: " + fxmlPath);
                alert.showAndWait();
                return;
            }

            // Notun page er UI ta load korche
            Parent root = FXMLLoader.load(fxmlLocation);

            // Current window (Stage) ta khuje ber korche jate oikhanei notun page ta show kora jay
            Stage stage = (Stage) clickedButton.getScene().getWindow();
            Scene currentScene = stage.getScene();

            // Jodi kono karone scene na thake tahole notun kore 1400x900 size er scene banabe, na hole current scene er root ta change kore dibe
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1400, 900));
            } else {
                currentScene.setRoot(root);
            }

            // Notun page ta ashar somoy ekta smooth fade animation (halka theke gari) hobar jonno nicher code tuku add kora hoyeche
            root.setOpacity(0); // Prothome page ta invisible (0) thakbe
            FadeTransition fade = new FadeTransition(Duration.millis(240), root); // 240 millisecond dhore animation hobe
            fade.setFromValue(0); // 0 opacity theke suru hobe
            fade.setToValue(1); // 1 opacity (fully visible) porjonto jabe
            fade.play(); // Animation ta start korche

        } catch (IOException e) {
            e.printStackTrace(); // Kono vul hole console e error print korbe
        }
    }
}