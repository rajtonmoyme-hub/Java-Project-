package com.example.foodprice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FarmersController implements Initializable {

    @FXML private GridPane farmersGrid; // IMPORTANT: In FXML, empty this grid

    // Navigation Buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnProducts;
    @FXML private Button btnWarehouse;

    // List to hold data
    private List<Farmer> farmerList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDummyData();
        renderFarmers();
    }

    // 1. Load Initial Data (The ones from your screenshot)
    private void loadDummyData() {
        farmerList.add(new Farmer("new", "📞 hethrh", "yy, e5yy, সিলেট", "জমি: 5.464e+67 একর", "y5y5eryy", "৳0", 50, false));
        farmerList.add(new Farmer("মো: আব্দুর রহমান", "📞 01711234567", "মধুপুর, বড়াইগ্রাম, রাজশাহী", "জমি: 5.5 একর", "ধান, গম, পাট", "৳125000", 35, true));
        farmerList.add(new Farmer("ফাতেমা বেগম", "📞 01812345678", "বালিয়াডাঙ্গী, চিরিরবন্দর, রংপুর", "জমি: 3.2 একর", "আলু, সবজি, মরিচ", "৳85000", 45, true));
        farmerList.add(new Farmer("জাহিদ হোসেন", "📞 01913456789", "হরিপুর, দেওয়ানগঞ্জ, ময়মনসিংহ", "জমি: 8 একর", "ধান, ভুট্টা, সরিষা", "৳250000", 28, true));
        farmerList.add(new Farmer("রোকেয়া খাতুন", "📞 01614567890", "নীলগঞ্জ, কলাপাড়া, বরিশাল", "জমি: 2 একর", "মাছ, ধান", "৳45000", 65, false));
    }

    // 2. Open Dialog
    @FXML
    void openRegistrationDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/farmer_dialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("নতুন কৃষক নিবন্ধন");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            FarmerDialogController controller = loader.getController();
            dialogStage.showAndWait(); // Wait for user to close

            // 3. If Registered, Add to List & Refresh
            if (controller.isSaveClicked()) {
                farmerList.add(0, controller.getNewFarmer()); // Add to top
                renderFarmers();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 4. Render Cards Dynamically (This replaces hardcoded FXML cards)
    private void renderFarmers() {
        farmersGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        for (Farmer f : farmerList) {
            VBox card = createFarmerCard(f);
            farmersGrid.add(card, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    // Helper to build the UI card using Java Code
    private VBox createFarmerCard(Farmer f) {
        VBox card = new VBox(12);
        card.getStyleClass().add("farmer-card");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        StackPane icon = new StackPane();
        icon.getStyleClass().add("profile-icon");
        SVGPath svg = new SVGPath();
        svg.setContent("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z");
        svg.setFill(javafx.scene.paint.Color.WHITE);
        svg.setScaleX(0.8); svg.setScaleY(0.8);
        icon.getChildren().add(svg);

        VBox info = new VBox();
        Label name = new Label(f.getName());
        name.setStyle("-fx-font-weight:bold; -fx-font-size:14px");
        Label phone = new Label(f.getPhone());
        phone.setStyle("-fx-text-fill:#888; -fx-font-size:11px");
        info.getChildren().addAll(name, phone);

        header.getChildren().addAll(icon, info);
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        header.getChildren().add(r);

        if (f.isVerified()) {
            Label badge = new Label("✔ যাচাইকৃত");
            badge.getStyleClass().add("badge-verified-green");
            header.getChildren().add(badge);
        }

        // Location & Land
        VBox details = new VBox(5);
        details.getChildren().add(createIconText("M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z", f.getLocation()));
        details.getChildren().add(createIconText("M21 5c-1.11-.35-2.33-.5-3.5-.5-1.95 0-4.05.4-5.5 1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45 4.9 1 6v14.65c0 .25.25.5.5.5.1 0 .15-.05.25-.05C3.1 20.45 5.05 20 6.5 20c1.95 0 4.05.4 5.5 1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65 0 3.35.3 4.75 1.05.1.05.15.05.25.05.25 0 .5-.25.5-.5V6c-.6-.45-1.25-.75-2-1zm0 13.5c-1.1-.35-2.3-.5-3.5-.5-1.7 0-4.15.65-5.5 1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2 0 2.4.15 3.5.5v11.5z", f.getLandAmount(), "#00A65A"));

        // Crops
        HBox cropsBox = new HBox(5);
        for(String crop : f.getCrops().split(",")) {
            Label l = new Label(crop.trim());
            l.getStyleClass().add("crop-tag");
            cropsBox.getChildren().add(l);
        }

        // Stats
        HBox stats = new HBox(10);
        VBox sales = new VBox();
        sales.getStyleClass().add("data-box");
        HBox.setHgrow(sales, Priority.ALWAYS);
        Label lblSales = new Label("মোট বিক্রি"); lblSales.getStyleClass().add("text-label");
        Label valSales = new Label(f.getTotalSales()); valSales.getStyleClass().add("text-value");
        sales.getChildren().addAll(lblSales, valSales);

        VBox score = new VBox();
        HBox.setHgrow(score, Priority.ALWAYS);
        Label lblScore = new Label("শোষণ সূচক"); lblScore.getStyleClass().add("text-label");
        Label valScore = new Label(String.valueOf(f.getScore()));

        if(f.getScore() > 60) {
            score.getStyleClass().add("score-box-bad");
            valScore.getStyleClass().add("text-score-bad");
        } else {
            score.getStyleClass().add("score-box-good");
            valScore.getStyleClass().add("text-score-good");
        }
        score.getChildren().addAll(lblScore, valScore);
        stats.getChildren().addAll(sales, score);

        // Edit Button
        Button btnEdit = new Button("✎  সম্পাদনা");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.getStyleClass().add("btn-edit");

        card.getChildren().addAll(header, details, cropsBox, stats, btnEdit);
        return card;
    }

    private HBox createIconText(String svgContent, String text) {
        return createIconText(svgContent, text, "#718096");
    }

    private HBox createIconText(String svgContent, String text, String color) {
        HBox hb = new HBox(5);
        hb.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        SVGPath path = new SVGPath();
        path.setContent(svgContent);
        path.setFill(javafx.scene.paint.Color.web(color));
        path.setScaleX(0.6); path.setScaleY(0.6);
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill:#666");
        hb.getChildren().addAll(path, lbl);
        return hb;
    }

    // Standard Navigation
    @FXML void goToDashboard(ActionEvent event) { navigate(event, "/dashboard.fxml"); }
    @FXML void goToProducts(ActionEvent event) { navigate(event, "/products.fxml"); }
    @FXML void goToWarehouse(ActionEvent event) { navigate(event, "/warehouse.fxml"); }

    private void navigate(ActionEvent event, String fxmlFile) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlFile);
            if (fxmlLocation == null) return;
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) { e.printStackTrace(); }
    }
}