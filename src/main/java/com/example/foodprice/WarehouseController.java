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

public class WarehouseController implements Initializable {

    @FXML private GridPane warehousesGrid;
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbRegionFilter;

    // Summary Card Labels
    @FXML private Label lblTotalWarehouses;
    @FXML private Label lblTotalCapacity;
    @FXML private Label lblAvgUsage;
    @FXML private Label lblHighRiskCount;

    private List<Warehouse> masterWarehouseList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ১. অঞ্চলের ড্রপডাউন সেটআপ
        ObservableList<String> regions = FXCollections.observableArrayList(
                "সকল অঞ্চল", "ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"
        );
        cbRegionFilter.setItems(regions);
        cbRegionFilter.setValue("সকল অঞ্চল");

        // ২. ডাটা লোড এবং সামারি কার্ড আপডেট
        loadAndRender();

        // ৩. রিয়েল-টাইম সার্চ লিসেনার (টাইপ করার সাথে সাথে ফিল্টার হবে)
        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    /**
     * ডাটা লোড করে গ্রিড এবং সামারি কার্ড রেন্ডার করে
     */
    private void loadAndRender() {
        masterWarehouseList = DataManager.loadWarehouses();
        updateSummaryCards();
        applyFilters(); // বর্তমান ফিল্টার অনুযায়ী গ্রিড দেখাবে
    }

    /**
     * উপরের ৪টি কার্ডের ডাটা ক্যালকুলেশন
     */
    private void updateSummaryCards() {
        if (masterWarehouseList == null || masterWarehouseList.isEmpty()) {
            lblTotalWarehouses.setText("0");
            lblTotalCapacity.setText("0 MT");
            lblAvgUsage.setText("0%");
            lblHighRiskCount.setText("0");
            return;
        }

        int totalCount = masterWarehouseList.size();
        double totalCap = 0;
        double totalStock = 0;
        int highRisk = 0;

        for (Warehouse w : masterWarehouseList) {
            totalCap += w.getCapacity();
            totalStock += w.getCurrentStock();
            // ৯০% এর বেশি ব্যবহার হলে ঝুঁকি ধরা হচ্ছে
            if (w.getCapacity() > 0 && (w.getCurrentStock() / w.getCapacity()) >= 0.9) {
                highRisk++;
            }
        }

        double avgUsage = (totalCap > 0) ? (totalStock / totalCap) * 100 : 0;

        lblTotalWarehouses.setText(String.valueOf(totalCount));
        // ১০০০ এর বেশি হলে K MT ফরম্যাটে দেখানো
        lblTotalCapacity.setText(totalCap >= 1000 ? String.format("%.1fK MT", totalCap / 1000) : String.format("%.0f MT", totalCap));
        lblAvgUsage.setText(String.format("%.1f%%", avgUsage));
        lblHighRiskCount.setText(String.valueOf(highRisk));
    }

    @FXML void handleSearch() { applyFilters(); }
    @FXML void handleRegionFilter() { applyFilters(); }

    /**
     * সার্চ এবং রিজিয়ন ফিল্টার লজিক
     */
    private void applyFilters() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String selectedRegion = cbRegionFilter.getValue();

        List<Warehouse> filtered = masterWarehouseList.stream()
                .filter(w -> w.getName().toLowerCase().contains(searchText))
                .filter(w -> selectedRegion.equals("সকল অঞ্চল") || w.getRegion().equals(selectedRegion))
                .collect(Collectors.toList());

        renderWarehouses(filtered);
    }

    private void renderWarehouses(List<Warehouse> list) {
        warehousesGrid.getChildren().clear();
        int col = 0, row = 0;

        for (Warehouse w : list) {
            VBox card = createWarehouseCard(w);
            warehousesGrid.add(card, col, row);
            col++;
            if (col == 3) { col = 0; row++; }
        }
    }

    /**
     * ডাইনামিক কার্ড জেনারেশন (ইমেজের ডিজাইন অনুযায়ী)
     */
    private VBox createWarehouseCard(Warehouse w) {
        VBox card = new VBox(15);
        double usage = (w.getCapacity() > 0) ? (w.getCurrentStock() / w.getCapacity()) * 100 : 0;
        boolean isRisk = usage >= 90;

        card.getStyleClass().addAll("warehouse-card", isRisk ? "warehouse-card-risk" : "warehouse-card-normal");

        // Header Section
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().addAll("icon-container", isRisk ? "icon-red" : "icon-orange");
        SVGPath svg = new SVGPath();
        svg.setContent("M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-6l-2-2z");
        svg.setFill(Color.WHITE);
        iconBox.getChildren().add(svg);

        VBox titleBox = new VBox();
        Label name = new Label(w.getName());
        name.setStyle("-fx-font-weight:bold; -fx-font-size:16px; -fx-text-fill: #1E293B;");
        Label type = new Label(w.getType());
        type.setStyle("-fx-text-fill:#94A3B8; -fx-font-size:12px;");
        titleBox.getChildren().addAll(name, type);
        header.getChildren().addAll(iconBox, titleBox);

        if (isRisk) {
            Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
            Label riskBadge = new Label("⚠ ঝুঁকি");
            riskBadge.getStyleClass().add("badge-risk");
            header.getChildren().addAll(r, riskBadge);
        }

        // Progress Bar
        VBox progressSection = new VBox(5);
        Label usageLbl = new Label(String.format("ব্যবহার: %.1f%%", usage));
        usageLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        StackPane track = new StackPane(); track.getStyleClass().add("progress-bg");
        Region fill = new Region();
        fill.getStyleClass().add(isRisk ? "progress-fill-red" : "progress-fill-green");
        fill.setPrefWidth(220 * (usage / 100.0));
        track.getChildren().add(fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        progressSection.getChildren().addAll(usageLbl, track);

        // Stats
        HBox stats = new HBox(10);
        VBox sBox = createStatBox("মজুদ", w.getCurrentStock() + " MT");
        VBox cBox = createStatBox("ধারণক্ষমতা", w.getCapacity() + " MT");
        HBox.setHgrow(sBox, Priority.ALWAYS); HBox.setHgrow(cBox, Priority.ALWAYS);
        stats.getChildren().addAll(sBox, cBox);

        Label loc = new Label("📍 " + w.getRegion());
        loc.getStyleClass().add("location-text");

        // Action Buttons (Edit & Delete)
        HBox actions = new HBox(10);
        Button btnEdit = new Button("✎  সম্পাদনা");
        btnEdit.getStyleClass().add("btn-edit");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnEdit, Priority.ALWAYS);

        // এডিট বাটন লজিক
        btnEdit.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_warehouse_dialog.fxml"));
                Parent root = loader.load();
                EditWarehouseController controller = loader.getController();
                controller.setWarehouseData(w, masterWarehouseList.indexOf(w));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
                if (controller.isUpdateClicked()) loadAndRender();
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        // ডিলিট বাটন লজিক (Trash Icon)
        Button btnDelete = new Button();
        btnDelete.getStyleClass().add("btn-delete");
        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        btnDelete.setGraphic(trashIcon);
        btnDelete.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "আপনি কি এটি নিশ্চিতভাবে মুছতে চান?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    masterWarehouseList.remove(w);
                    DataManager.saveWarehouses(masterWarehouseList);
                    loadAndRender();
                }
            });
        });

        actions.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().addAll(header, progressSection, stats, loc, actions);
        return card;
    }

    private VBox createStatBox(String lbl, String val) {
        VBox box = new VBox(); box.getStyleClass().add("stat-box");
        Label l = new Label(lbl); l.setStyle("-fx-font-size:10px; -fx-text-fill:#94A3B8");
        Label v = new Label(val); v.setStyle("-fx-font-weight:bold; -fx-font-size:14px; -fx-text-fill: #1E293B;");
        box.getChildren().addAll(l, v);
        return box;
    }

    @FXML
    void openWarehouseDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/warehouse_dialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("নতুন গুদাম যোগ করুন");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            WarehouseDialogController controller = loader.getController();
            stage.showAndWait();
            if (controller.isSaveClicked()) loadAndRender();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // নেভিগেশন মেথডসমূহ
    @FXML void goToDashboard(ActionEvent event) { navigate(event, "/dashboard.fxml"); }
    @FXML void goToProducts(ActionEvent event) { navigate(event, "/products.fxml"); }
    @FXML void goToFarmers(ActionEvent event) { navigate(event, "/farmers.fxml"); }

    private void navigate(ActionEvent event, String path) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) { e.printStackTrace(); }
    }
}