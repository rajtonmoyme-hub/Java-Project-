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

// Ponno (Products) er page control korar main class
public class ProductsController implements Initializable {

    // FXML theke UI element gulo link kora hocche
    @FXML private GridPane productsGrid;
    @FXML private TextField tfSearch; // FXML এ fx:id="tfSearch" দিন
    @FXML private ComboBox<String> cbCategoryFilter; // FXML এ fx:id="cbCategoryFilter" দিন
    @FXML private Button btnAddProduct;

    // Data load korar por main list ekhane save thakbe
    private List<Product> masterList = new ArrayList<>(); // মূল ডাটা রাখার জন্য

    // Page open howar sathe sathe ei method ta prothome call hobe
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ১. ক্যাটাগরি ড্রপডাউন সেটআপ (Dropdown e ki ki category thakbe ta set kora hocche)
        ObservableList<String> categories = FXCollections.observableArrayList(
                "সকল ক্যাটাগরি", "চাল", "গম", "তেল", "চিনি", "পেঁয়াজ",
                "আলু", "ডাল", "সবজি", "মাছ", "মাংস", "অন্যান্য"
        );
        cbCategoryFilter.setItems(categories);
        cbCategoryFilter.setValue("সকল ক্যাটাগরি"); // Default value set kora

        // Jodi user admin na hoye sadharon user hoy, tahole add product button hide kora hocche
        if (UserSession.isUser() && btnAddProduct != null) {
            btnAddProduct.setVisible(false);
            btnAddProduct.setManaged(false);
        }

        // ২. data load and display korbe
        loadAndRender();

        // ৩. search feild e type korar sathe sathe filter shuru korbe
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(); // Text change holei data filter hobe
        });
    }

    // DataManager theke sob product load kore UI te render korar method
    private void loadAndRender() {
        masterList = DataManager.loadProducts();
        renderProducts(masterList);
    }


    @FXML
    void handleCategoryFilter(ActionEvent event) {
        filterProducts(); // Category change hole abar list filter kora
    }

    //search Filter
    private void filterProducts() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String selectedCategory = cbCategoryFilter.getValue();

        // Master list theke search text r category match kore notun list banano hocche
        List<Product> filteredList = masterList.stream()
                .filter(p -> (p.getNameBn().toLowerCase().contains(searchText) ||
                        p.getNameEn().toLowerCase().contains(searchText)))
                .filter(p -> selectedCategory.equals("সকল ক্যাটাগরি") ||
                        p.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());

        // Filter kora result gulo abar grid e show kora hocche
        renderProducts(filteredList);
    }

    // card refresh korar method
    private void renderProducts(List<Product> listToShow) {
        productsGrid.getChildren().clear(); // Ager sob data muche fela
        int col = 0;
        int row = 0;

        // Loop chalaiye ekta ekta kore product card toiri kora hocche
        for (Product p : listToShow) {
            VBox card = createProductCard(p);
            productsGrid.add(card, col, row); // Grid e card ta add kora

            col++;
            // Protita row te 3 ta kore card thakbe, 3 ta hole porer row te jabe
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    // Notun product add korar dialog (popup) open korar method
    @FXML
    void openProductDialog(ActionEvent event) {
        if (UserSession.isUser()) {
            return; // Normal user access pabe na
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/product_dialog.fxml"));
            Parent root = loader.load();

            // Dialog er jonno notun stage (window) toiri kora hocche
            Stage stage = new Stage();
            stage.setTitle("নতুন পণ্য যোগ করুন");
            stage.initModality(Modality.APPLICATION_MODAL); // Background block korar jonno
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setScene(new Scene(root));

            ProductDialogController controller = loader.getController();
            stage.showAndWait(); // User popup theke ber na howa porjonto wait korbe

            // Save click korle notun data shoho list abar reload kora hobe
            if (controller.isSaveClicked()) {
                loadAndRender(); // নতুন ডাটা লোড করে দেখাবে
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ekta single product er details dekhate ekta choto Card (box) toiri korar method
    private VBox createProductCard(Product p) {
        VBox card = new VBox(15);
        card.getStyleClass().add("product-card");

        // Header Section (Icon, Nam r Category er jonno)
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("card-icon");
        SVGPath svg = new SVGPath();
        svg.setContent("M21 16.5c0 .38-.21.71-.53.88l-7.9 4.44c-.16.12-.36.18-.57.18-.21 0-.41-.06-.57-.18l-7.9-4.44A.991.991 0 0 1 3 16.5v-9c0-.38.21-.71.53-.88l7.9-4.44c.16-.12.36-.18.57-.18.21 0 .41.06.57.18l7.9 4.44c.32.17.53.5.53.88v9z");
        svg.setFill(Color.WHITE);
        iconBox.getChildren().add(svg);

        VBox nameBox = new VBox();
        Label nameBn = new Label(p.getNameBn());
        nameBn.getStyleClass().add("product-title");
        Label category = new Label(p.getCategory());
        category.getStyleClass().add("product-category");
        nameBox.getChildren().addAll(nameBn, category);

        header.getChildren().addAll(iconBox, nameBox);
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS); // Faka jayga toiri kora
        header.getChildren().add(r);

        // Badge logic (Jodi dam 20% er beshi bere jay tahole lal badge dekhabe)
        if (p.getPrevPrice() > 0 && ((p.getCurrentPrice() - p.getPrevPrice()) / p.getPrevPrice()) > 0.2) {
            Label badge = new Label("অস্বাভাবিক");
            badge.getStyleClass().add("badge-abnormal");
            header.getChildren().add(badge);
        }

        // Stats Section (Daam r koto tuku daam poriborton hoise tar list)
        HBox stats = new HBox(10);
        VBox priceBox = createStatBox("বর্তমান দাম", "৳" + p.getCurrentPrice());

        // Daam barchhe na komse ta hisab kora hocche
        double diff = p.getCurrentPrice() - p.getPrevPrice();
        VBox changeBox = createStatBox("পরিবর্তন", (diff >= 0 ? "↗ " : "↘ ") + Math.abs(diff));
        Label changeVal = (Label) changeBox.getChildren().get(1);
        // Daam barle lal, r komle sobuj rong kora hocche
        changeVal.setTextFill(diff > 0 ? Color.web("#DC3545") : Color.web("#00A65A"));

        HBox.setHgrow(priceBox, Priority.ALWAYS);
        HBox.setHgrow(changeBox, Priority.ALWAYS);
        stats.getChildren().addAll(priceBox, changeBox);

        // Stock dekhano hocche
        Label stockLbl = new Label("মজুদ: " + p.getStock() + " " + p.getUnit());
        stockLbl.getStyleClass().add("stock-text");

        // Buttons (Edit r Delete)
        HBox actions = new HBox(10);
        Button btnEdit = new Button("✎  সম্পাদনা");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.getStyleClass().add("btn-edit");
        HBox.setHgrow(btnEdit, Priority.ALWAYS);

        // Edit button click korle ki hobe tar logic
        btnEdit.setOnAction(e -> {
            if (UserSession.isUser()) {
                return; // User access pabe na
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_product_dialog.fxml"));
                Parent root = loader.load();
                EditProductController controller = loader.getController();

                // filter thaka obosthai right index khuje ber kora
                int originalIndex = masterList.indexOf(p);
                controller.setProductData(p, originalIndex);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();

                // Update korar por grid refresh kora hocche
                if (controller.isUpdateClicked()) loadAndRender();
            } catch (IOException ex) { ex.printStackTrace(); }
        });

        // Delete button toiri r tar action set kora
        Button btnDel = new Button();
        btnDel.getStyleClass().add("btn-delete");
        SVGPath trash = new SVGPath();
        trash.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        btnDel.setGraphic(trash);
        btnDel.setOnAction(e -> {
            if (UserSession.isUser()) {
                return; // User access pabe na
            }
            // Delete korar age ekta confirmation message dekhano hocche
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "আপনি কি এটি মুছতে চান?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    masterList.remove(p);
                    DataManager.saveProducts(masterList); // Database e save kora
                    loadAndRender(); // List theke soraye notun kore render kora
                }
            });
        });

        // Admin hole edit, delete button soho card ta toiri kora hobe, user hole button chara
        if (UserSession.isAdmin()) {
            actions.getChildren().addAll(btnEdit, btnDel);
            card.getChildren().addAll(header, stats, stockLbl, actions);
        } else {
            card.getChildren().addAll(header, stats, stockLbl);
        }
        return card;
    }

    // Helper method: Choto stats (daam, change) er box bananor jonno
    private VBox createStatBox(String labelText, String valueText) {
        VBox vbox = new VBox();
        vbox.getStyleClass().add("stat-box");
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("stat-label");
        Label val = new Label(valueText);
        val.getStyleClass().add("stat-value");
        vbox.getChildren().addAll(lbl, val);
        return vbox;
    }

    // Navigation Methods - Sidebar ba menu theke onno page e jawar jonno
    @FXML void goToDashboard(ActionEvent event) { navigate(event, "/dashboard.fxml"); }
    @FXML void goToWarehouse(ActionEvent event) { navigate(event, "/warehouse.fxml"); }
    @FXML void goToFarmers(ActionEvent event) { navigate(event, "/farmers.fxml"); }
    @FXML void goToSupplyChain(ActionEvent event) {navigate(event, "/supply_chain.fxml");}

    // Ek page theke arek page e navigate korar main method
    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            // Background scene set ba update kora hocche
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1400, 900));
            } else {
                currentScene.setRoot(root);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}