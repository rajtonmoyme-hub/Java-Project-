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

// Warehouse (গুদাম) page er main controller class
public class WarehouseController implements Initializable {

    // FXML er UI element gulov load
    @FXML private GridPane warehousesGrid; // Gudam gulo grid akare dekhanor jonno
    @FXML private TextField tfSearch; // Search box
    @FXML private ComboBox<String> cbRegionFilter; // Division/Region onujayi filter korar dropdown

    // RBAC: Add Warehouse Button ID (Notun gudam add korar button)
    @FXML private Button btnAddWarehouse;

    // Summary Card Labels (Oporer card er text gulo)
    @FXML private Label lblTotalWarehouses, lblTotalCapacity, lblAvgUsage, lblHighRiskCount;

    // Database theke load kora main list ekhane thakbe
    private List<Warehouse> masterWarehouseList = new ArrayList<>();

    // Page load howar sathe sathe ei method ta call hobe
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Dropdown e ki ki division thakbe ta set kora hocche
        ObservableList<String> regions = FXCollections.observableArrayList(
                "সকল অঞ্চল", "ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"
        );
        cbRegionFilter.setItems(regions);
        cbRegionFilter.setValue("সকল অঞ্চল"); // Default value


        // Normal user jeno notun gudam add korte na pare tai button lukiye fela hocche
        if (UserSession.isUser() && btnAddWarehouse != null) {
            btnAddWarehouse.setVisible(false);
            btnAddWarehouse.setManaged(false);
        }

        // Data load kore screen e sajano
        loadAndRender();

        // Search box e type korar sathe sathe list jeno filter hoy tar jonno listener add kora
        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    // Database (DataManager) theke gudamer list ene UI update kora
    private void loadAndRender() {
        masterWarehouseList = DataManager.loadWarehouses();
        updateSummaryCards(); // Oporer card gulo update kora
        applyFilters(); // List e data dekhano
    }

    // Oporer Summary Card gulor hisab nikash kora (Total, Capacity, Avg, Risk)
    private void updateSummaryCards() {
        // Jodi kono data na thake tahole shob 0 dekhabe
        if (masterWarehouseList == null || masterWarehouseList.isEmpty()) {
            lblTotalWarehouses.setText("0"); lblTotalCapacity.setText("0 MT");
            lblAvgUsage.setText("0%"); lblHighRiskCount.setText("0");
            return;
        }

        int totalCount = masterWarehouseList.size();
        double totalCap = 0, totalStock = 0;
        int highRisk = 0; // Koyta gudam risk a ase tar count

        // Protita gudam loop kore total capacity r stock jog kora hocche
        for (Warehouse w : masterWarehouseList) {
            totalCap += w.getCapacity();
            totalStock += w.getCurrentStock();
            // Gudam jodi 90% ba tar beshi fill hoye jay tahole seta Risk hisabe dhora hobe
            if (w.getCapacity() > 0 && (w.getCurrentStock() / w.getCapacity()) >= 0.9) {
                highRisk++;
            }
        }

        // Average koto percent use hocche tar hisab
        double avgUsage = (totalCap > 0) ? (totalStock / totalCap) * 100 : 0;

        // Label gulote data set kora
        lblTotalWarehouses.setText(String.valueOf(totalCount));
        // 1000 er beshi hole 'K' use kora, jemon 1500 ke 1.5K MT dekhabe
        lblTotalCapacity.setText(totalCap >= 1000 ? String.format("%.1fK MT", totalCap / 1000) : String.format("%.0f MT", totalCap));
        lblAvgUsage.setText(String.format("%.1f%%", avgUsage));
        lblHighRiskCount.setText(String.valueOf(highRisk));
    }

    // Search ba Dropdown change hole ei method gulo call hoy
    @FXML void handleSearch() { applyFilters(); }
    @FXML void handleRegionFilter() { applyFilters(); }

    // Search text r Region onujayi data filter korar main logic
    private void applyFilters() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String selectedRegion = cbRegionFilter.getValue();

        List<Warehouse> filtered = masterWarehouseList.stream()
                // Nam diye khonja
                .filter(w -> w.getName().toLowerCase().contains(searchText))
                // Region/Division diye khonja
                .filter(w -> selectedRegion.equals("সকল অঞ্চল") || w.getRegion().equals(selectedRegion))
                .collect(Collectors.toList());

        // Filter howar por je result aslo seta grid e show kora
        renderWarehouses(filtered);
    }

    // Filter kora gudam gulo grid e sajanor method
    private void renderWarehouses(List<Warehouse> list) {
        warehousesGrid.getChildren().clear(); // Ager data clear kora
        int col = 0, row = 0;

        for (Warehouse w : list) {
            VBox card = createWarehouseCard(w); // Ekta gudamer jonno card toiri kora
            warehousesGrid.add(card, col, row);
            col++;
            // Ek row te 3 ta card boshar por nicher row te jabe
            if (col == 3) { col = 0; row++; }
        }
    }

    // Ekta gudam (Warehouse) er jonno UI Card toiri korar logic
    private VBox createWarehouseCard(Warehouse w) {
        VBox card = new VBox(15);
        // Gudam koto percent fill ase tar hisab
        double usage = (w.getCapacity() > 0) ? (w.getCurrentStock() / w.getCapacity()) * 100 : 0;
        // 90% er beshi fill hole risk true hobe
        boolean isRisk = usage >= 90;

        // Risk thakle card er CSS class change hobe (red border or glow er jonno)
        card.getStyleClass().addAll("warehouse-card", isRisk ? "warehouse-card-risk" : "warehouse-card-normal");

        // Card er Header (Icon, Nam r Type)
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().addAll("icon-container", isRisk ? "icon-red" : "icon-orange");
        SVGPath svg = new SVGPath();
        // Gudam er icon SVG
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

        // Risk thakle card er opore dan dike "⚠ ঝুঁকি" (Risk badge) dekhabe
        if (isRisk) {
            Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); // Faka jayga toiri kora
            Label riskBadge = new Label("⚠ ঝুঁকি");
            riskBadge.getStyleClass().add("badge-risk");
            header.getChildren().addAll(r, riskBadge);
        }

        // Progress bar er section
        VBox progressSection = new VBox(5);
        Label usageLbl = new Label(String.format("ব্যবহার: %.1f%%", usage));
        usageLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        StackPane track = new StackPane();
        track.getStyleClass().add("progress-bg"); // Progress bar er background
        Region fill = new Region();
        // Risk thakle red fill hobe, naile green fill hobe
        fill.getStyleClass().add(isRisk ? "progress-fill-red" : "progress-fill-green");
        fill.setPrefWidth(220 * (usage / 100.0)); // Percentage onujayi width set kora
        track.getChildren().add(fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        progressSection.getChildren().addAll(usageLbl, track);

        // Stats section (Current stock r Total capacity)
        HBox stats = new HBox(10);
        VBox sBox = createStatBox("মজুদ", w.getCurrentStock() + " MT");
        VBox cBox = createStatBox("ধারণক্ষমতা", w.getCapacity() + " MT");
        HBox.setHgrow(sBox, Priority.ALWAYS); HBox.setHgrow(cBox, Priority.ALWAYS);
        stats.getChildren().addAll(sBox, cBox);

        // Location dekhanor text
        Label loc = new Label("📍 " + w.getRegion());
        loc.getStyleClass().add("location-text");

        // --- RBAC: Action Buttons Logic (Edit r Delete button) ---
        HBox actions = new HBox(10);
        Button btnEdit = new Button("✎  সম্পাদনা");
        btnEdit.getStyleClass().add("btn-edit");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnEdit, Priority.ALWAYS);

        // Edit button a click korle edit popup open hobe
        btnEdit.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_warehouse_dialog.fxml"));
                Parent root = loader.load();
                EditWarehouseController controller = loader.getController();
                // Kon gudam ta edit hobe tar data pass kora
                controller.setWarehouseData(w, masterWarehouseList.indexOf(w));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL); // Background block kora
                stage.setScene(new Scene(root));
                stage.showAndWait(); // User popup close kora porjonto wait kora
                // Update hole grid abar reload kora
                if (controller.isUpdateClicked()) loadAndRender();
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        Button btnDelete = new Button();
        btnDelete.getStyleClass().add("btn-delete");
        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        btnDelete.setGraphic(trashIcon);

        // Delete button a click korle confirmation massage dekhabe
        btnDelete.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "আপনি কি এটি নিশ্চিতভাবে মুছতে চান?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    masterWarehouseList.remove(w); // List theke remove kora
                    DataManager.saveWarehouses(masterWarehouseList); // Database save kora
                    loadAndRender(); // UI refresh kora
                }
            });
        });

        actions.getChildren().addAll(btnEdit, btnDelete);


        // Admin hole button shoho card toiri hobe, ar normal user hole button chara.
        if (UserSession.isAdmin()) {
            card.getChildren().addAll(header, progressSection, stats, loc, actions);
        } else {
            card.getChildren().addAll(header, progressSection, stats, loc);
        }

        return card;
    }

    // Choto stats (mojud ba capacity) er box bananor helper method
    private VBox createStatBox(String lbl, String val) {
        VBox box = new VBox(); box.getStyleClass().add("stat-box");
        Label l = new Label(lbl); l.setStyle("-fx-font-size:10px; -fx-text-fill:#94A3B8");
        Label v = new Label(val); v.setStyle("-fx-font-weight:bold; -fx-font-size:14px; -fx-text-fill: #1E293B;");
        box.getChildren().addAll(l, v);
        return box;
    }

    // Notun gudam add korar dialog ba popup open korar method
    @FXML
    void openWarehouseDialog(ActionEvent event) {
        if (UserSession.isUser()) {
            return; // Normal user access pabe na
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/warehouse_dialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("নতুন গুদাম যোগ করুন");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            WarehouseDialogController controller = loader.getController();
            stage.showAndWait();
            // Save click korle UI reload kora
            if (controller.isSaveClicked()) loadAndRender();
        } catch (IOException e) { e.printStackTrace(); }
    }
}