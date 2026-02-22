package com.example.foodprice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private LineChart<String, Number> priceChart;

    // Summary Cards Labels - Functional IDs
    @FXML private Label lblTotalProducts;    // Card 1
    @FXML private Label lblPriceChange;     // Card 2
    @FXML private Label lblAlertCount;      // Card 3
    @FXML private Label lblWarehouseUsage;  // Card 4
    @FXML private Label lblTotalStock;      // Card 5
    @FXML private Label lblFarmerCount;     // Card 6
    @FXML private Label lblImportVolume;    // Card 7
    @FXML private Label lblStability;       // Card 8

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initChart();
        refreshStatistics(); // কার্ডের তথ্যগুলো আপডেট করবে
    }

    /**
     * কার্ডের ভ্যালুগুলো রিয়েল-টাইমে আপডেট করার লজিক
     */
    private void refreshStatistics() {
        // ১. কৃষকের সংখ্যা সরাসরি ডাটা ফাইল থেকে লোড করা হচ্ছে
        List<Farmer> farmers =  DataManager.loadFarmers();
        lblFarmerCount.setText(String.valueOf(farmers.size()));

        // ২. অন্যান্য ভ্যালুগুলো ডিফল্ট সেট করা (আপনার অ্যাডমিন প্যানেলের লজিক অনুযায়ী এখানে আপডেট হবে)
        lblTotalProducts.setText("১২");
        lblPriceChange.setText("৯.৩%");
        lblAlertCount.setText("৩");
        lblWarehouseUsage.setText("৭৭%");
        lblTotalStock.setText("২৯.২K");
        lblImportVolume.setText("১২.৫K");
        lblStability.setText("৭২%");
    }

    private void initChart() {
        priceChart.getData().clear();
        XYChart.Series<String, Number> riceSeries = new XYChart.Series<>();
        riceSeries.setName("চাল");
        riceSeries.getData().add(new XYChart.Data<>("জানু", 108));
        riceSeries.getData().add(new XYChart.Data<>("জুন", 113));

        XYChart.Series<String, Number> oilSeries = new XYChart.Series<>();
        oilSeries.setName("তেল");
        oilSeries.getData().add(new XYChart.Data<>("জানু", 160));
        oilSeries.getData().add(new XYChart.Data<>("জুন", 164));

        priceChart.getData().addAll(riceSeries, oilSeries);
    }

    @FXML void goToProducts(ActionEvent event) { navigate(event, "/products.fxml"); }
    @FXML void goToWarehouse(ActionEvent event) { navigate(event, "/warehouse.fxml"); }
    @FXML void goToFarmers(ActionEvent event) { navigate(event, "/farmers.fxml"); }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            setActiveSidebarButton(fxmlPath);
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) return;
            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1400, 900));
            } else {
                currentScene.setRoot(root);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setActiveSidebarButton(String fxmlPath) {
        if ("/products.fxml".equals(fxmlPath)) {
            SidebarController.activeButtonId = "btnProducts";
        } else if ("/warehouse.fxml".equals(fxmlPath)) {
            SidebarController.activeButtonId = "btnWarehouse";
        } else if ("/farmers.fxml".equals(fxmlPath)) {
            SidebarController.activeButtonId = "btnFarmers";
        } else if ("/dashboard.fxml".equals(fxmlPath)) {
            SidebarController.activeButtonId = "btnDashboard";
        }
    }
}
