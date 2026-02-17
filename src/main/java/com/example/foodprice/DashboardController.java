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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private LineChart<String, Number> priceChart;

    // Summary Cards Labels (যাতে আপনি কোড থেকে সংখ্যা বদলাতে পারেন)
    @FXML private Label lblTotalProducts;
    @FXML private Label lblPriceChange;
    @FXML private Label lblAlertCount;
    @FXML private Label lblWarehouseUsage;
    @FXML private Label lblTotalStock;
    @FXML private Label lblFarmerCount;
    @FXML private Label lblImportVolume;
    @FXML private Label lblStability;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initChart();
        // এখানে আপনি চাইলে ডিফল্ট কিছু ভ্যালু সেট করতে পারেন
        // lblTotalProducts.setText("১০");
    }

    private void initChart() {
        XYChart.Series<String, Number> riceSeries = new XYChart.Series<>();
        riceSeries.setName("চাল");
        riceSeries.getData().add(new XYChart.Data<>("জানু", 108));
        riceSeries.getData().add(new XYChart.Data<>("ফেব", 110));
        riceSeries.getData().add(new XYChart.Data<>("মার্চ", 112));
        riceSeries.getData().add(new XYChart.Data<>("এপ্রিল", 115));
        riceSeries.getData().add(new XYChart.Data<>("মে", 114));
        riceSeries.getData().add(new XYChart.Data<>("জুন", 113));

        XYChart.Series<String, Number> oilSeries = new XYChart.Series<>();
        oilSeries.setName("তেল");
        oilSeries.getData().add(new XYChart.Data<>("জানু", 160));
        oilSeries.getData().add(new XYChart.Data<>("ফেব", 162));
        oilSeries.getData().add(new XYChart.Data<>("মার্চ", 165));
        oilSeries.getData().add(new XYChart.Data<>("এপ্রিল", 168));
        oilSeries.getData().add(new XYChart.Data<>("মে", 165));
        oilSeries.getData().add(new XYChart.Data<>("জুন", 164));

        XYChart.Series<String, Number> onionSeries = new XYChart.Series<>();
        onionSeries.setName("পেঁয়াজ");
        onionSeries.getData().add(new XYChart.Data<>("জানু", 60));
        onionSeries.getData().add(new XYChart.Data<>("ফেব", 65));
        onionSeries.getData().add(new XYChart.Data<>("মার্চ", 70));
        onionSeries.getData().add(new XYChart.Data<>("এপ্রিল", 75));
        onionSeries.getData().add(new XYChart.Data<>("মে", 55));
        onionSeries.getData().add(new XYChart.Data<>("জুন", 50));

        priceChart.getData().addAll(riceSeries, oilSeries, onionSeries);
    }

    // Navigation Methods
    @FXML
    void goToProducts(ActionEvent event) { navigate(event, "/products.fxml"); }

    @FXML
    void goToWarehouse(ActionEvent event) { navigate(event, "/warehouse.fxml"); }

    @FXML
    void goToFarmers(ActionEvent event) { navigate(event, "/farmers.fxml"); }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) return;
            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1400, 900));
        } catch (IOException e) { e.printStackTrace(); }
    }
}