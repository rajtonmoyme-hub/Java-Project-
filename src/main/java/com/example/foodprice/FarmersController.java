package com.example.foodprice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FarmersController implements Initializable {

    @FXML private GridPane farmersGrid;

    @FXML private Button btnDashboard;
    @FXML private Button btnProducts;
    @FXML private Button btnWarehouse;

    private List<Farmer> farmerList = new ArrayList<>();
    private static final String FILE_NAME = "farmers.json";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFromFile();
        renderFarmers();
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            loadDummyData();
            saveToFile();
            return;
        }

        try {
            String json = Files.readString(Paths.get(FILE_NAME), StandardCharsets.UTF_8);
            Type listType = new TypeToken<ArrayList<Farmer>>(){}.getType();
            List<Farmer> loaded = gson.fromJson(json, listType);

            if (loaded != null && !loaded.isEmpty()) {
                farmerList.clear();
                farmerList.addAll(loaded);
            } else {
                loadDummyData();
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Failed to load " + FILE_NAME + " → using dummy data");
            e.printStackTrace();
            loadDummyData();
            saveToFile();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_NAME, StandardCharsets.UTF_8)) {
            gson.toJson(farmerList, writer);
            System.out.println("Saved " + farmerList.size() + " farmers to: " +
                    new File(FILE_NAME).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save " + FILE_NAME);
            e.printStackTrace();
        }
    }

    private void loadDummyData() {
        farmerList.add(new Farmer("new", "📞 hethrh", "yy, e5yy, সিলেট",
                "জমি: 5.464e+67 একর", "y5y5eryy", "৳0", 50, false));

        farmerList.add(new Farmer("মো: আব্দুর রহমান", "📞 01711234567",
                "মধুপুর, বড়াইগ্রাম, রাজশাহী", "জমি: 5.5 একর",
                "ধান, গম, পাট", "৳125000", 35, true));

        farmerList.add(new Farmer("ফাতেমা বেগম", "📞 01812345678",
                "বালিয়াডাঙ্গী, চিরিরবন্দর, রংপুর", "জমি: 3.2 একর",
                "আলু, সবজি, মরিচ", "৳85000", 45, true));

        farmerList.add(new Farmer("জাহিদ হোসেন", "📞 01913456789",
                "হরিপুর, দেওয়ানগঞ্জ, ময়মনসিংহ", "জমি: 8 একর",
                "ধান, ভুট্টা, সরিষা", "৳250000", 28, true));

        farmerList.add(new Farmer("রোকেয়া খাতুন", "📞 01614567890",
                "নীলগঞ্জ, কলাপাড়া, বরিশাল", "জমি: 2 একর",
                "মাছ, ধান", "৳45000", 65, false));
    }

    @FXML
    void openRegistrationDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/farmer_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("নতুন কৃষক নিবন্ধন");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            FarmerDialogController controller = loader.getController();
            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                Farmer newFarmer = controller.getNewFarmer();
                farmerList.add(0, newFarmer);
                renderFarmers();
                saveToFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderFarmers() {
        farmersGrid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Farmer farmer : farmerList) {
            VBox card = createFarmerCard(farmer);
            farmersGrid.add(card, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createFarmerCard(Farmer f) {
        VBox card = new VBox(12);
        card.getStyleClass().add("farmer-card");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("profile-icon");

        SVGPath personIcon = new SVGPath();
        personIcon.setContent("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z");
        personIcon.setFill(Color.WHITE);
        personIcon.setScaleX(0.8);
        personIcon.setScaleY(0.8);
        iconContainer.getChildren().add(personIcon);

        VBox namePhone = new VBox(2);
        Label lblName = new Label(f.getName());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblPhone = new Label(f.getPhone());
        lblPhone.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        namePhone.getChildren().addAll(lblName, lblPhone);

        header.getChildren().addAll(iconContainer, namePhone);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        if (f.isVerified()) {
            Label verifiedBadge = new Label("✔ যাচাইকৃত");
            verifiedBadge.getStyleClass().add("badge-verified-green");
            header.getChildren().add(verifiedBadge);
        }

        // Details
        VBox details = new VBox(6);
        details.getChildren().addAll(
                createIconText("M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z", f.getLocation()),
                createIconText("M21 5c-1.11-.35-2.33-.5-3.5-.5-1.95 0-4.05.4-5.5 1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45 4.9 1 6v14.65c0 .25.25.5.5.5.1 0 .15-.05.25-.05C3.1 20.45 5.05 20 6.5 20c1.95 0 4.05.4 5.5 1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65 0 3.35.3 4.75 1.05.1.05.15.05.25.05.25 0 .5-.25.5-.5V6c-.6-.45-1.25-.75-2-1zm0 13.5c-1.1-.35-2.3-.5-3.5-.5-1.7 0-4.15.65-5.5 1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2 0 2.4.15 3.5.5v11.5z",
                        f.getLandAmount(), "#2E7D32")
        );

        // Crops tags (FlowPane → auto-wraps)
        FlowPane cropsContainer = new FlowPane(6, 6);
        cropsContainer.setAlignment(Pos.CENTER_LEFT);

        if (f.getCrops() != null && !f.getCrops().isBlank()) {
            for (String crop : f.getCrops().split(",")) {
                String trimmed = crop.trim();
                if (!trimmed.isEmpty()) {
                    Label tag = new Label(trimmed);
                    tag.getStyleClass().add("crop-tag");
                    cropsContainer.getChildren().add(tag);
                }
            }
        }

        // Stats
        HBox stats = new HBox(12);

        VBox salesBox = createStatBox("মোট বিক্রি", f.getTotalSales(), null);
        VBox scoreBox = createStatBox("শোষণ সূচক", String.valueOf(f.getScore()),
                f.getScore() > 60 ? "bad" : "good");

        stats.getChildren().addAll(salesBox, scoreBox);

        // Edit button (placeholder)
        Button btnEdit = new Button("✎ সম্পাদনা");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.getStyleClass().add("btn-edit");

        card.getChildren().addAll(header, details, cropsContainer, stats, btnEdit);

        return card;
    }

    private VBox createStatBox(String title, String value, String styleClass) {
        VBox box = new VBox(2);
        box.getStyleClass().add("data-box");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("text-label");

        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("text-value");

        if (styleClass != null) {
            box.getStyleClass().add("score-box-" + styleClass);
            lblValue.getStyleClass().add("text-score-" + styleClass);
        }

        box.getChildren().addAll(lblTitle, lblValue);
        return box;
    }

    private HBox createIconText(String svgPath, String text) {
        return createIconText(svgPath, text, "#546E7A");
    }

    private HBox createIconText(String svgPath, String text, String color) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web(color));
        icon.setScaleX(0.65);
        icon.setScaleY(0.65);

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #455A64; -fx-font-size: 13px;");

        row.getChildren().addAll(icon, label);
        return row;
    }

    // ────────────────────────────────────────────────
    // Navigation
    // ────────────────────────────────────────────────

    @FXML void goToDashboard(ActionEvent event) { navigate(event, "/dashboard.fxml"); }
    @FXML void goToProducts(ActionEvent event)   { navigate(event, "/products.fxml");   }
    @FXML void goToWarehouse(ActionEvent event)  { navigate(event, "/warehouse.fxml");  }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("FXML not found: " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}