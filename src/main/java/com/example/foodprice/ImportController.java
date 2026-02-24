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

// Ei class ta Import page er sob kaj control kore
public class ImportController implements Initializable {

    // Eikhane UI er elements gulo ke FXML er sathe connect kora hocche
    @FXML private Label lblTotalImport;
    @FXML private Label lblAvgLandingCost;
    @FXML private Label lblVerifiedCount;
    @FXML private Label lblPendingCount;
    @FXML private Label lblResultInfo;

    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbCategoryFilter;
    @FXML private VBox recordsListContainer;
    @FXML private VBox emptyStateBox;
    @FXML private javafx.scene.control.Button btnAddImport;

    // Sob import record er list e save thakbe
    private final List<ImportRecord> masterList = new ArrayList<>();

    // Page ta load holei ei initialize method ta automatically run hobe
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Jodi login kora manush ta sadharon 'User' hoy, tahole notun import add korar button ta hide kore dibe
        if (UserSession.isUser() && btnAddImport != null) {
            btnAddImport.setVisible(false);
            btnAddImport.setManaged(false);
        }

        // Category dropdown er initial setup
        cbCategoryFilter.setItems(FXCollections.observableArrayList("সকল ক্যাটাগরি"));
        cbCategoryFilter.setValue("সকল ক্যাটাগরি");

        // Search box e kichu type korlei jeno sathe sathe filter hoy, tar jonno ekta listener add kora hoyeche
        tfSearch.textProperty().addListener((obs, oldText, newText) -> applyFilters());

        // Data load kore UI te dekhabe
        loadAndRender();
    }

    // Search button e click korle ei method ta run hobe
    @FXML
    private void handleSearch() {
        applyFilters();
    }

    // Category dropdown theke kono item select korle ei method ta run hobe
    @FXML
    private void handleCategoryFilter() {
        applyFilters();
    }

    // Notun import record add korar jonno je popup/dialog ashe, ta ei method diye open hoy
    @FXML
    private void openNewImportDialog() {
        // Sadharon user ra ei popup open korte parbe na
        if (UserSession.isUser()) {
            return;
        }
        try {
            // Notun dialog er fxml load kora hocche
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/import_dialog.fxml"));
            Parent root = loader.load();

            // Dialog er jonno ekta notun window (Stage) banano hocche
            Stage stage = new Stage();
            stage.setTitle("নতুন আমদানি রেকর্ড");
            stage.initModality(Modality.APPLICATION_MODAL); // Ei popup bondho na kora porjonto onno kothao click kora jabe na
            stage.setScene(new Scene(root, 860, 860));

            ImportDialogController controller = loader.getController();
            stage.showAndWait(); // Popup ta show kore wait korbe

            // Jodi user save button e click kore ar notun record toiri hoy, tahole seta list e add korbe
            if (controller.isSaveClicked() && controller.getCreatedRecord() != null) {
                masterList.add(0, controller.getCreatedRecord()); // List er ekdom surute add korbe
                DataManager.saveImportRecords(masterList); // DataManager diye file e save korbe
                loadAndRender(); // Update kora data abar screen e dekhabe
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // File theke data read kore UI te show korar kaj kore ei method
    private void loadAndRender() {
        masterList.clear(); // Puraton data clear korche
        masterList.addAll(DataManager.loadImportRecords()); // Notun data list e rakhche

        // Jodi kono data na thake, tahole faka ekta list save korche
        if (masterList.isEmpty()) {
            DataManager.saveImportRecords(masterList);
        }

        // Dropdown, summary card ar UI er data update korche
        refreshCategoryFilter();
        updateSummaryCards();
        applyFilters();
    }

    // Sob record theke unique category gulo khuje ber kore dropdown list e add korche
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

        // Jodi ager select kora category ta ekhon list e na thake, tahole "সকল ক্যাটাগরি" select kore dibe
        if (selected == null || !categories.contains(selected)) {
            cbCategoryFilter.setValue("সকল ক্যাটাগরি");
        } else {
            cbCategoryFilter.setValue(selected);
        }
    }

    // Upore je 4ta summary card (Total Import, Avg Cost, Verified, Pending) thake, segulor hisab eikhane hoy
    private void updateSummaryCards() {
        double totalQty = 0;
        double totalCost = 0;
        int verified = 0;
        int pending = 0;

        // Sob record er upor loop chaliye jogfol ber korche
        for (ImportRecord record : masterList) {
            totalQty += record.getQuantityMt();
            totalCost += record.getLandingCost();

            // Record ta verified naki pending seta check korche
            if (record.isCustomsCleared() || "যাচাইকৃত".equals(record.getStatus())) {
                verified++;
            } else {
                pending++;
            }
        }

        // Gor (average) cost ber korche
        double avgCost = masterList.isEmpty() ? 0 : totalCost / masterList.size();

        // Label gulote data set korche (format kore)
        lblTotalImport.setText(String.format(Locale.US, "%.1fK MT", totalQty / 1000.0));
        lblAvgLandingCost.setText(String.format(Locale.US, "৳%.0f", avgCost));
        lblVerifiedCount.setText(String.valueOf(verified));
        lblPendingCount.setText(String.valueOf(pending));
    }

    // Search box e ja likhbe ar dropdown theke ja select korbe, sei onujayi list theke data khuje ber korbe
    private void applyFilters() {
        String query = tfSearch.getText() == null ? "" : tfSearch.getText().trim().toLowerCase(Locale.ROOT);
        String category = cbCategoryFilter.getValue();

        // Stream api use kore list theke data filter korche
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

        // Koto gulo record pawa gelo seta dekhabe
        if (lblResultInfo != null) {
            lblResultInfo.setText(filtered.size() + " রেকর্ড");
        }

        // Filter kora data gulo card hisebe UI te bananor jonno ei method ta call korche
        renderCards(filtered);
    }

    // Filter kora record gulo diye screen e ek ekta design kora box (card) banabe
    private void renderCards(List<ImportRecord> list) {
        recordsListContainer.getChildren().clear(); // Agger sob card muche felche

        for (ImportRecord r : list) {
            VBox card = new VBox(12); // Ekta notun vertical box (card) banachhe
            card.getStyleClass().add("import-data-card");

            // Status er upor vitti kore card er design/color set korche
            card.getStyleClass().add((r.isCustomsCleared() || "যাচাইকৃত".equals(r.getStatus()))
                    ? "import-data-card-verified"
                    : "import-data-card-pending");

            // Card er top section (Title, category ar status thakbe)
            HBox top = new HBox(10);
            top.setAlignment(Pos.CENTER_LEFT);
            top.getStyleClass().add("data-card-top");

            Label product = new Label(safe(r.getProduct()));
            product.getStyleClass().add("data-card-title");

            Label category = new Label(safe(r.getCategory()));
            category.getStyleClass().add("data-card-category");

            // Dui pashe space deyar jonno Region use korche
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label status = new Label(statusText(r));
            status.getStyleClass().addAll("status-chip", statusClass(r));

            top.getChildren().addAll(product, category, sp, status);

            // Card er vitorer prothom row (jemon source, amount, fob eigula dekhabe)
            HBox row1 = new HBox(24);
            row1.getStyleClass().add("data-card-metrics");
            row1.getChildren().addAll(
                    metric("উৎস", safe(r.getSource())),
                    metric("পরিমাণ", String.format(Locale.US, "%.2f MT", r.getQuantityMt())),
                    metric("FOB", String.format(Locale.US, "৳%.2f", r.getFobPrice())),
                    metric("ল্যান্ডিং কস্ট", String.format(Locale.US, "৳%.2f", r.getLandingCost())),
                    metric("বন্দর", safe(r.getPort())),
                    metric("তারিখ", safe(r.getDate()))
            );

            // Card er vitorer ditiyo row (importer er nam, LC, license eigula dekhabe)
            HBox row2 = new HBox(24);
            row2.getStyleClass().add("data-card-metrics");
            row2.setPadding(new Insets(2, 0, 0, 0));
            row2.getChildren().addAll(
                    metric("আমদানিকারক", safe(r.getImporterName())),
                    metric("লাইসেন্স", safe(r.getLicenseNumber())),
                    metric("LC নম্বর", safe(r.getLcNumber())),
                    metric("শুল্ক পরিশোধ", String.format(Locale.US, "৳%.2f", r.getDutyPaid()))
            );

            // Top, row1, row2 - ei sobkichu card er moddhe dhukiye dilo
            card.getChildren().addAll(top, row1, row2);

            // Card ta main list container e add korlo
            recordsListContainer.getChildren().add(card);
        }

        // Jodi kono data na thake tahole empty state (faka chobi/text) dekhabe
        boolean hasData = !list.isEmpty();
        emptyStateBox.setManaged(!hasData);
        emptyStateBox.setVisible(!hasData);
        recordsListContainer.setManaged(hasData);
        recordsListContainer.setVisible(hasData);
    }

    // Title ar Value diye choto ekta box (metric) bananor helper method
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

    // Status onujayi CSS class er nam pawar helper method
    private String statusClass(ImportRecord record) {
        return (record.isCustomsCleared() || "যাচাইকৃত".equals(record.getStatus()))
                ? "status-verified"
                : "status-pending";
    }

    // Status onujayi ki text dekhabe seta set korar helper method
    private String statusText(ImportRecord record) {
        return (record.isCustomsCleared() || "যাচাইকৃত".equals(record.getStatus()))
                ? "যাচাইকৃত"
                : "অমিমাংসিত";
    }

    // Kon kichu kono text e ache naki seta check korar ekta choto (helper) method
    private boolean contains(String value, String query) {
        return safe(value).toLowerCase(Locale.ROOT).contains(query);
    }

    // Null asle jate error na dey, tar jonno faka string e convert korar helper method
    private String safe(String value) {
        return value == null ? "" : value;
    }
}