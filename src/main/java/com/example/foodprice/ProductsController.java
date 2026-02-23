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

public class ProductsController implements Initializable {

    @FXML private GridPane productsGrid;
    @FXML private TextField tfSearch; // FXML এ fx:id="tfSearch" দিন
    @FXML private ComboBox<String> cbCategoryFilter; // FXML এ fx:id="cbCategoryFilter" দিন
    @FXML private Button btnAddProduct;

    private List<Product> masterList = new ArrayList<>(); // মূল ডাটা রাখার জন্য

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ১. ক্যাটাগরি ড্রপডাউন সেটআপ
        ObservableList<String> categories = FXCollections.observableArrayList(
                "সকল ক্যাটাগরি", "চাল", "গম", "তেল", "চিনি", "পেঁয়াজ",
                "আলু", "ডাল", "সবজি", "মাছ", "মাংস", "অন্যান্য"
        );
        cbCategoryFilter.setItems(categories);
        cbCategoryFilter.setValue("সকল ক্যাটাগরি");

        if (UserSession.isUser() && btnAddProduct != null) {
            btnAddProduct.setVisible(false);
            btnAddProduct.setManaged(false);
        }

        // ২. ডাটা লোড এবং প্রদর্শন
        loadAndRender();

        // ৩. সার্চ ফিল্ডে টাইপ করার সাথে সাথে ফিল্টার হবে (Real-time Search)
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts();
        });
    }

    private void loadAndRender() {
        masterList = DataManager.loadProducts();
        renderProducts(masterList);
    }

    /**
     * ক্যাটাগরি কম্বোবক্স চেঞ্জ হলে এই মেথড কল হবে (FXML-এ onAction এ দিন)
     */
    @FXML
    void handleCategoryFilter(ActionEvent event) {
        filterProducts();
    }

    /**
     * সার্চ টেক্সট এবং ক্যাটাগরি অনুযায়ী ফিল্টার করার মেইন লজিক
     */
    private void filterProducts() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String selectedCategory = cbCategoryFilter.getValue();

        List<Product> filteredList = masterList.stream()
                .filter(p -> (p.getNameBn().toLowerCase().contains(searchText) ||
                        p.getNameEn().toLowerCase().contains(searchText)))
                .filter(p -> selectedCategory.equals("সকল ক্যাটাগরি") ||
                        p.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());

        renderProducts(filteredList);
    }

    /**
     * গ্রিড ভিউ রিফ্রেশ করার মেথড
     */
    private void renderProducts(List<Product> listToShow) {
        productsGrid.getChildren().clear();
        int col = 0;
        int row = 0;

        for (Product p : listToShow) {
            VBox card = createProductCard(p);
            productsGrid.add(card, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    void openProductDialog(ActionEvent event) {
        if (UserSession.isUser()) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/product_dialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("নতুন পণ্য যোগ করুন");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setScene(new Scene(root));

            ProductDialogController controller = loader.getController();
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                loadAndRender(); // নতুন ডাটা লোড করে দেখাবে
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductCard(Product p) {
        VBox card = new VBox(15);
        card.getStyleClass().add("product-card");

        // Header Section
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
        HBox.setHgrow(r, Priority.ALWAYS);
        header.getChildren().add(r);

        // Badge logic
        if (p.getPrevPrice() > 0 && ((p.getCurrentPrice() - p.getPrevPrice()) / p.getPrevPrice()) > 0.2) {
            Label badge = new Label("অস্বাভাবিক");
            badge.getStyleClass().add("badge-abnormal");
            header.getChildren().add(badge);
        }

        // Stats Section
        HBox stats = new HBox(10);
        VBox priceBox = createStatBox("বর্তমান দাম", "৳" + p.getCurrentPrice());

        double diff = p.getCurrentPrice() - p.getPrevPrice();
        VBox changeBox = createStatBox("পরিবর্তন", (diff >= 0 ? "↗ " : "↘ ") + Math.abs(diff));
        Label changeVal = (Label) changeBox.getChildren().get(1);
        changeVal.setTextFill(diff > 0 ? Color.web("#DC3545") : Color.web("#00A65A"));

        HBox.setHgrow(priceBox, Priority.ALWAYS);
        HBox.setHgrow(changeBox, Priority.ALWAYS);
        stats.getChildren().addAll(priceBox, changeBox);

        Label stockLbl = new Label("মজুদ: " + p.getStock() + " " + p.getUnit());
        stockLbl.getStyleClass().add("stock-text");

        // Buttons
        HBox actions = new HBox(10);
        Button btnEdit = new Button("✎  সম্পাদনা");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.getStyleClass().add("btn-edit");
        HBox.setHgrow(btnEdit, Priority.ALWAYS);

        btnEdit.setOnAction(e -> {
            if (UserSession.isUser()) {
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_product_dialog.fxml"));
                Parent root = loader.load();
                EditProductController controller = loader.getController();

                // ফিল্টার থাকা অবস্থায় সঠিক ইন্ডেক্স খুঁজে বের করা
                int originalIndex = masterList.indexOf(p);
                controller.setProductData(p, originalIndex);

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
        btnDel.setGraphic(trash);
        btnDel.setOnAction(e -> {
            if (UserSession.isUser()) {
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "আপনি কি এটি মুছতে চান?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    masterList.remove(p);
                    DataManager.saveProducts(masterList);
                    loadAndRender();
                }
            });
        });

        if (UserSession.isAdmin()) {
            actions.getChildren().addAll(btnEdit, btnDel);
            card.getChildren().addAll(header, stats, stockLbl, actions);
        } else {
            card.getChildren().addAll(header, stats, stockLbl);
        }
        return card;
    }

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

    // Navigation Methods
    @FXML void goToDashboard(ActionEvent event) { navigate(event, "/dashboard.fxml"); }
    @FXML void goToWarehouse(ActionEvent event) { navigate(event, "/warehouse.fxml"); }
    @FXML void goToFarmers(ActionEvent event) { navigate(event, "/farmers.fxml"); }
    @FXML void goToSupplyChain(ActionEvent event) {navigate(event, "/supply_chain.fxml");}

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1400, 900));
            } else {
                currentScene.setRoot(root);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
