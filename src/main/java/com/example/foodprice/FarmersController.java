package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FarmersController implements Initializable {

    @FXML private GridPane farmersGrid;
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbRegionFilter;

    // Summary Card Labels
    @FXML private Label lblTotalFarmers;
    @FXML private Label lblVerifiedFarmers;
    @FXML private Label lblTotalLand;
    @FXML private Label lblAvgScore;

    private List<Farmer> masterFarmerList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ১. ড্রপডাউন সেটআপ
        ObservableList<String> regions = FXCollections.observableArrayList(
                "সকল অঞ্চল", "ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"
        );
        cbRegionFilter.setItems(regions);
        cbRegionFilter.setValue("সকল অঞ্চল");

        // ২. ডাটা লোড করা
        loadAndRender();

        // ৩. রিয়েল-টাইম সার্চ লিসেনার (টাইপ করার সাথে সাথে ফিল্টার হবে)
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void loadAndRender() {
        masterFarmerList = DataManager.loadFarmers();
        if (masterFarmerList.isEmpty()) {
            loadDummyData();
            DataManager.saveFarmers(masterFarmerList);
        }
        applyFilters(); // বর্তমান ফিল্টার অনুযায়ী রেন্ডার করবে
    }

    @FXML
    void handleRegionFilter(ActionEvent event) {
        applyFilters();
    }

    /**
     * সার্চ এবং অঞ্চল ফিল্টার করার মেইন লজিক
     */
    private void applyFilters() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String selectedRegion = cbRegionFilter.getValue();

        List<Farmer> filteredList = masterFarmerList.stream()
                .filter(f -> f.getName().toLowerCase().contains(searchText) ||
                        f.getPhone().contains(searchText))
                .filter(f -> selectedRegion.equals("সকল অঞ্চল") ||
                        (f.getDivision() != null && f.getDivision().equals(selectedRegion)))
                .collect(Collectors.toList());

        updateSummaryCards(filteredList);
        renderFarmers(filteredList);
    }

    private void updateSummaryCards(List<Farmer> list) {
        if (list == null || list.isEmpty()) {
            lblTotalFarmers.setText("0");
            lblVerifiedFarmers.setText("0");
            lblTotalLand.setText("0 একর");
            lblAvgScore.setText("0");
            return;
        }

        int total = list.size();
        int verifiedCount = (int) list.stream().filter(Farmer::isVerified).count();
        double totalLand = list.stream().mapToDouble(f -> {
            try { return Double.parseDouble(f.getLandAmount().replaceAll("[^0-9.]", "")); }
            catch (Exception e) { return 0; }
        }).sum();
        int avgScore = (int) list.stream().mapToInt(Farmer::getScore).average().orElse(0);

        lblTotalFarmers.setText(String.valueOf(total));
        lblVerifiedFarmers.setText(String.valueOf(verifiedCount));
        lblTotalLand.setText(String.format("%.1f একর", totalLand));
        lblAvgScore.setText(String.valueOf(avgScore));
    }

    private void renderFarmers(List<Farmer> listToShow) {
        farmersGrid.getChildren().clear();
        int col = 0, row = 0;
        for (Farmer farmer : listToShow) {
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
        personIcon.setScaleX(0.8); personIcon.setScaleY(0.8);
        iconContainer.getChildren().add(personIcon);

        VBox namePhone = new VBox(2);
        Label lblName = new Label(f.getName());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblPhone = new Label(f.getPhone());
        lblPhone.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        namePhone.getChildren().addAll(lblName, lblPhone);

        header.getChildren().addAll(iconContainer, namePhone);
        if (f.isVerified()) {
            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Label badge = new Label("✔ যাচাইকৃত");
            badge.getStyleClass().add("badge-verified-green");
            header.getChildren().addAll(spacer, badge);
        }

        // Details (Location & Land) - এখানে আপনার এররটি ফিক্স করা হয়েছে
        VBox details = new VBox(6);
        details.getChildren().addAll(
                createIconText("M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z", f.getLocation()),
                createIconText("M21 5c-1.11-.35-2.33-.5-3.5-.5-1.95 0-4.05.4-5.5 1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45 4.9 1 6v14.65c0 .25.25.5.5.5.1 0 .15-.05.25-.05C3.1 20.45 5.05 20 6.5 20c1.95 0 4.05.4 5.5 1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65 0 3.35.3 4.75 1.05.1.05.15.05.25.05.25 0 .5-.25.5-.5V6c-.6-.45-1.25-.75-2-1zm0 13.5c-1.1-.35-2.3-.5-3.5-.5-1.7 0-4.15.65-5.5 1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2 0 2.4.15 3.5.5v11.5z", f.getLandAmount(), "#10B981")
        );

        // Crops FlowPane
        FlowPane cropsContainer = new FlowPane(6, 6);
        if (f.getCrops() != null && !f.getCrops().isEmpty()) {
            for (String crop : f.getCrops().split(",")) {
                Label tag = new Label(crop.trim());
                tag.getStyleClass().add("crop-tag");
                cropsContainer.getChildren().add(tag);
            }
        }

        // Stats (Sales & Score)
        HBox stats = new HBox(12);
        VBox salesBox = createStatBox("মোট বিক্রি", f.getTotalSales(), null);
        VBox scoreBox = createStatBox("শোষণ সূচক", String.valueOf(f.getScore()), f.getScore() > 60 ? "bad" : "good");
        stats.getChildren().addAll(salesBox, scoreBox);

        // Actions (Edit & Delete)
        HBox actions = new HBox(10);
        Button btnEdit = new Button("✎  সম্পাদনা");
        btnEdit.getStyleClass().add("btn-edit");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnEdit, Priority.ALWAYS);
        btnEdit.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_farmer_dialog.fxml"));
                Parent root = loader.load();
                EditFarmerController controller = loader.getController();
                controller.setFarmerData(f, masterFarmerList.indexOf(f));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
                if (controller.isUpdateClicked()) loadAndRender();
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        Button btnDel = new Button();
        btnDel.getStyleClass().add("btn-delete");
        SVGPath trash = new SVGPath();
        trash.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        trash.setFill(Color.web("#EF4444"));
        btnDel.setGraphic(trash);
        btnDel.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "আপনি কি এটি নিশ্চিতভাবে মুছতে চান?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    masterFarmerList.remove(f);
                    DataManager.saveFarmers(masterFarmerList);
                    loadAndRender();
                }
            });
        });

        actions.getChildren().addAll(btnEdit, btnDel);
        card.getChildren().addAll(header, details, cropsContainer, stats, actions);
        return card;
    }

    private VBox createStatBox(String title, String value, String style) {
        VBox box = new VBox(2); box.getStyleClass().add("data-box"); HBox.setHgrow(box, Priority.ALWAYS);
        Label t = new Label(title); t.getStyleClass().add("text-label");
        Label v = new Label(value); v.getStyleClass().add("text-value");
        if (style != null) { box.getStyleClass().add("score-box-" + style); v.getStyleClass().add("text-score-" + style); }
        box.getChildren().addAll(t, v);
        return box;
    }

    // মেথড ওভারলোডিং (এরর ফিক্স করার জন্য)
    private HBox createIconText(String svg, String txt) {
        return createIconText(svg, txt, "#64748B");
    }

    private HBox createIconText(String svg, String txt, String color) {
        HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
        SVGPath icon = new SVGPath(); icon.setContent(svg); icon.setFill(Color.web(color));
        icon.setScaleX(0.7); icon.setScaleY(0.7);
        Label l = new Label(txt); l.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
        row.getChildren().addAll(icon, l);
        return row;
    }

    @FXML
    void openRegistrationDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/farmer_dialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("নতুন কৃষক নিবন্ধন");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            FarmerDialogController controller = loader.getController();
            stage.showAndWait();
            if (controller.isSaveClicked()) loadAndRender();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadDummyData() {
        masterFarmerList.add(new Farmer("মো: আব্দুর রহমান", "01711234567", "123456", "রাজশাহী", "বড়াইগ্রাম", "মধুপুর", "মধুপুর", "5.5 একর", "বিকাশ", "123", "ধান, গম", "৳১,২৫,০০০", 35, true));
    }

    @FXML void goToDashboard(ActionEvent event) { navigate(event, "/dashboard.fxml"); }
    @FXML void goToProducts(ActionEvent event)   { navigate(event, "/products.fxml");   }
    @FXML void goToWarehouse(ActionEvent event)  { navigate(event, "/warehouse.fxml");  }

    private void navigate(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) { e.printStackTrace(); }
    }
}