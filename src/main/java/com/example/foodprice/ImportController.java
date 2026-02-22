package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ImportController implements Initializable {

    @FXML private Label lblTotalImport;
    @FXML private Label lblAvgLandingCost;
    @FXML private Label lblVerifiedCount;
    @FXML private Label lblPendingCount;
    @FXML private Label lblResultInfo;

    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbCategoryFilter;
    @FXML private VBox recordsListContainer;
    @FXML private VBox emptyStateBox;

    private final List<ImportRecord> masterList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbCategoryFilter.setItems(FXCollections.observableArrayList("সকল ক্যাটাগরি"));
        cbCategoryFilter.setValue("সকল ক্যাটাগরি");
        tfSearch.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        loadAndRender();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleCategoryFilter() {
        applyFilters();
    }

    @FXML
    private void openNewImportDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/import_dialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("নতুন আমদানি রেকর্ড");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 860, 860));

            ImportDialogController controller = loader.getController();
            stage.showAndWait();

            if (controller.isSaveClicked() && controller.getCreatedRecord() != null) {
                masterList.add(0, controller.getCreatedRecord());
                DataManager.saveImportRecords(masterList);
                loadAndRender();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAndRender() {
        masterList.clear();
        masterList.addAll(DataManager.loadImportRecords());
        if (masterList.isEmpty()) {
            DataManager.saveImportRecords(masterList);
        }
        refreshCategoryFilter();
        updateSummaryCards();
        applyFilters();
    }

    private void refreshCategoryFilter() {
        String selected = cbCategoryFilter.getValue();
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        categories.add("সকল ক্যাটাগরি");
        for (ImportRecord record : masterList) {
            if (record.getCategory() != null && !record.getCategory().isBlank()) {
                categories.add(record.getCategory().trim());
            }
        }
        cbCategoryFilter.setItems(FXCollections.observableArrayList(categories));
        if (selected == null || !categories.contains(selected)) {
            cbCategoryFilter.setValue("সকল ক্যাটাগরি");
        } else {
            cbCategoryFilter.setValue(selected);
        }
    }

    private void updateSummaryCards() {
        double totalQty = 0;
        double totalCost = 0;
        int verified = 0;
        int pending = 0;

        for (ImportRecord record : masterList) {
            totalQty += record.getQuantityMt();
            totalCost += record.getLandingCost();
            if (record.isCustomsCleared() || "যাচাইকৃত".equals(record.getStatus())) {
                verified++;
            } else {
                pending++;
            }
        }

        double avgCost = masterList.isEmpty() ? 0 : totalCost / masterList.size();
        lblTotalImport.setText(String.format(Locale.US, "%.1fK MT", totalQty / 1000.0));
        lblAvgLandingCost.setText(String.format(Locale.US, "৳%.0f", avgCost));
        lblVerifiedCount.setText(String.valueOf(verified));
        lblPendingCount.setText(String.valueOf(pending));
    }

    private void applyFilters() {
        String query = tfSearch.getText() == null ? "" : tfSearch.getText().trim().toLowerCase(Locale.ROOT);
        String category = cbCategoryFilter.getValue();

        List<ImportRecord> filtered = masterList.stream()
                .filter(r -> query.isEmpty()
                        || contains(r.getProduct(), query)
                        || contains(r.getSource(), query)
                        || contains(r.getPort(), query)
                        || contains(r.getDate(), query)
                        || contains(r.getImporterName(), query)
                        || contains(r.getLicenseNumber(), query)
                        || contains(r.getLcNumber(), query))
                .filter(r -> "সকল ক্যাটাগরি".equals(category) || safe(r.getCategory()).equals(category))
                .collect(Collectors.toList());

        if (lblResultInfo != null) {
            lblResultInfo.setText(filtered.size() + " রেকর্ড");
        }
        renderCards(filtered);
    }

    private void renderCards(List<ImportRecord> list) {
        recordsListContainer.getChildren().clear();
        for (ImportRecord r : list) {
            VBox card = new VBox(12);
            card.getStyleClass().add("import-data-card");
            card.getStyleClass().add((r.isCustomsCleared() || "যাচাইকৃত".equals(r.getStatus()))
                    ? "import-data-card-verified"
                    : "import-data-card-pending");

            HBox top = new HBox(10);
            top.setAlignment(Pos.CENTER_LEFT);
            top.getStyleClass().add("data-card-top");
            Label product = new Label(safe(r.getProduct()));
            product.getStyleClass().add("data-card-title");
            Label category = new Label(safe(r.getCategory()));
            category.getStyleClass().add("data-card-category");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label status = new Label(statusText(r));
            status.getStyleClass().addAll("status-chip", statusClass(r));
            top.getChildren().addAll(product, category, sp, status);

            HBox row1 = new HBox(24);
            row1.getStyleClass().add("data-card-metrics");
            row1.getChildren().addAll(
                    metric("উৎস", safe(r.getSource())),
                    metric("পরিমাণ", String.format(Locale.US, "%.0f MT", r.getQuantityMt())),
                    metric("FOB", String.format(Locale.US, "৳%.2f", r.getFobPrice())),
                    metric("ল্যান্ডিং কস্ট", String.format(Locale.US, "৳%.2f", r.getLandingCost())),
                    metric("বন্দর", safe(r.getPort())),
                    metric("তারিখ", safe(r.getDate()))
            );

            HBox row2 = new HBox(24);
            row2.getStyleClass().add("data-card-metrics");
            row2.setPadding(new Insets(2, 0, 0, 0));
            row2.getChildren().addAll(
                    metric("আমদানিকারক", safe(r.getImporterName())),
                    metric("লাইসেন্স", safe(r.getLicenseNumber())),
                    metric("LC নম্বর", safe(r.getLcNumber())),
                    metric("শুল্ক পরিশোধ", String.format(Locale.US, "৳%.2f", r.getDutyPaid()))
            );

            card.getChildren().addAll(top, row1, row2);
            recordsListContainer.getChildren().add(card);
        }

        boolean hasData = !list.isEmpty();
        emptyStateBox.setManaged(!hasData);
        emptyStateBox.setVisible(!hasData);
        recordsListContainer.setManaged(hasData);
        recordsListContainer.setVisible(hasData);
    }

    private VBox metric(String title, String value) {
        VBox box = new VBox(2);
        box.getStyleClass().add("metric-box");
        Label t = new Label(title);
        t.getStyleClass().add("metric-title");
        Label v = new Label(value);
        v.getStyleClass().add("metric-value");
        box.getChildren().addAll(t, v);
        return box;
    }

    private String statusClass(ImportRecord record) {
        return (record.isCustomsCleared() || "যাচাইকৃত".equals(record.getStatus()))
                ? "status-verified"
                : "status-pending";
    }

    private String statusText(ImportRecord record) {
        return (record.isCustomsCleared() || "যাচাইকৃত".equals(record.getStatus()))
                ? "যাচাইকৃত"
                : "অমিমাংসিত";
    }

    private boolean contains(String value, String query) {
        return safe(value).toLowerCase(Locale.ROOT).contains(query);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
