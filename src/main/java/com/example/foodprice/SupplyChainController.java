package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

// Supply Chain page er main controller class
public class SupplyChainController implements Initializable {

    // FXML er UI element gulo (Containers r Buttons)
    @FXML private VBox entryListContainer;
    @FXML private VBox historyListContainer;
    @FXML private ComboBox<String> productFilterCombo;
    @FXML private Button btnNewEntry;

    // FXML er Top Cards er Label gulo
    @FXML private Label totalAmountLabel;
    @FXML private Label avgEfficiencyLabel;
    @FXML private Label avgWasteLabel;
    @FXML private Label supplyShockLabel;
    @FXML private Label historyCountLabel;

    // FXML er Stage onujayi count r percentage (share) dekhabar Label gulo
    @FXML private Label sourceCountLabel;
    @FXML private Label transportCountLabel;
    @FXML private Label warehouseCountLabel;
    @FXML private Label wholesaleCountLabel;
    @FXML private Label retailCountLabel;
    @FXML private Label sourceShareLabel;
    @FXML private Label transportShareLabel;
    @FXML private Label warehouseShareLabel;
    @FXML private Label wholesaleShareLabel;
    @FXML private Label retailShareLabel;

    // Constant string gulo ja stage er nam hisabe use hobe
    private static final String STAGE_SOURCE = "উৎস";
    private static final String STAGE_TRANSPORT = "পরিবহন";
    private static final String STAGE_WAREHOUSE = "গুদাম";
    private static final String STAGE_WHOLESALE = "পাইকারি";
    private static final String STAGE_RETAIL = "খুচরা";
    private static final String ALL_PRODUCTS = "সকল পণ্য";

    // Active entry r dropdown option rakhar jonno list
    private final List<SupplyEntry> cachedActiveEntries = new ArrayList<>();
    private final List<String> allProductOptions = new ArrayList<>();

    // Page load howar sathe sathe prothome ei method ta call hobe
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // User jodi normal user hoy (Admin na hoy), tahole "New Entry" button hide kora hobe
        if (UserSession.isUser() && btnNewEntry != null) {
            btnNewEntry.setVisible(false);
            btnNewEntry.setManaged(false);
        }
        // UI te data load r render kora
        loadAndRender();
    }

    // Database theke data load kore UI te sajate ei method kaj kore
    private void loadAndRender() {
        if (entryListContainer == null || historyListContainer == null) {
            return;
        }

        // Ager data clear kora
        entryListContainer.getChildren().clear();
        historyListContainer.getChildren().clear();

        // DataManager theke Active r History data load kora hocche
        List<SupplyEntry> activeEntries = DataManager.loadSupplyEntries();
        List<SupplyEntry> historyEntries = DataManager.loadSupplyHistory();

        // Cache (list) e active entry gulo save kora
        cachedActiveEntries.clear();
        cachedActiveEntries.addAll(activeEntries);
        // Dropdown er filter toiri kora
        setupProductFilter(activeEntries);
        // Active entry gulo screen e dekhano
        renderActiveEntries();

        // History entry gulo loop kore screen e dekhano
        for (SupplyEntry historyEntry : historyEntries) {
            historyListContainer.getChildren().add(createHistoryRow(historyEntry));
        }

        // History na thakle empty message dekhano
        if (historyEntries.isEmpty()) {
            historyListContainer.getChildren().add(createEmptyState("এখনও কোনো completed history নেই"));
        }

        // History er count update kora
        if (historyCountLabel != null) {
            historyCountLabel.setText(historyEntries.size() + " টি সম্পন্ন");
        }

        // Oporer  Card gulo dynamically update kora
        updateTopCards(activeEntries);
    }

    // Dropdown theke kono product select korle active list filter hobe
    @FXML
    private void handleProductFilterChange() {
        renderActiveEntries();
    }

    // Dropdown e click korle option gulo refresh kora
    @FXML
    private void handleProductFilterShowing() {
        setupProductFilter(cachedActiveEntries);
    }

    // Oporer card gulote total amount, efficiency r stage er hisab update korar method
    private void updateTopCards(List<SupplyEntry> list) {
        double totalAmount = 0;
        double totalEfficiency = 0;
        double totalWaste = 0;
        int supplyShockCount = 0; // Kono somossa ba delay hoise kina tar count

        int sourceCount = 0;
        int transportCount = 0;
        int warehouseCount = 0;
        int wholesaleCount = 0;
        int retailCount = 0;

        // Sob active entry loop kore calculate kora hocche
        for (SupplyEntry entry : list) {
            totalAmount += parseDoubleSafe(entry.getAmount());
            totalEfficiency += entry.getEfficiency();
            totalWaste += entry.getWastePercent();

            // Delay 24h er beshi, waste 10% er beshi ba efficiency 50% er kom hole supply shock dhora hoy
            if (entry.getDelayHours() >= 24 || entry.getWastePercent() >= 10     || entry.getEfficiency() < 50) {
                supplyShockCount++;
            }

            // Kon stage e koyta entry ase tar count
            String stage = entry.getStage() == null ? "" : entry.getStage().trim();
            if (STAGE_SOURCE.equals(stage)) {
                sourceCount++;
            } else if (STAGE_TRANSPORT.equals(stage)) {
                transportCount++;
            } else if (STAGE_WAREHOUSE.equals(stage)) {
                warehouseCount++;
            } else if (STAGE_WHOLESALE.equals(stage)) {
                wholesaleCount++;
            } else if (STAGE_RETAIL.equals(stage)) {
                retailCount++;
            }
        }

        // Average hisab kora
        int size = list.size();
        double avgEfficiency = size == 0 ? 0 : totalEfficiency / size;
        double avgWaste = size == 0 ? 0 : totalWaste / size;

        // Label e value gulo set kora
        if (totalAmountLabel != null) totalAmountLabel.setText(formatOneDecimal(totalAmount) + " MT");
        if (avgEfficiencyLabel != null) avgEfficiencyLabel.setText(formatOneDecimal(avgEfficiency) + "%");
        if (avgWasteLabel != null) avgWasteLabel.setText(formatOneDecimal(avgWaste) + "%");
        if (supplyShockLabel != null) supplyShockLabel.setText(String.valueOf(supplyShockCount));

        setStageCountText(sourceCountLabel, sourceCount);
        setStageCountText(transportCountLabel, transportCount);
        setStageCountText(warehouseCountLabel, warehouseCount);
        setStageCountText(wholesaleCountLabel, wholesaleCount);
        setStageCountText(retailCountLabel, retailCount);

        setStageShareText(sourceShareLabel, sourceCount, size);
        setStageShareText(transportShareLabel, transportCount, size);
        setStageShareText(warehouseShareLabel, warehouseCount, size);
        setStageShareText(wholesaleShareLabel, wholesaleCount, size);
        setStageShareText(retailShareLabel, retailCount, size);
    }

    // Notun entry add korar popup open korar button click event
    @FXML
    void openNewEntryDialog(ActionEvent event) {
        if (UserSession.isUser()) {
            return; // Normal user access pabe na
        }
        openEntryDialog((Node) event.getSource(), null, -1);
    }

    // Kono exist entry edit korar popup open korar method
    private void openEditEntryDialog(Node source, SupplyEntry entry, int index) {
        if (UserSession.isUser()) {
            return; // Normal user access pabe na
        }
        openEntryDialog(source, entry, index);
    }

    // Notun ba edit korar jonno asol (main) dialog open korar method
    private void openEntryDialog(Node source, SupplyEntry entry, int index) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/supply_dialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(entry == null ? "নতুন সাপ্লাই চেইন এন্ট্রি" : "সাপ্লাই এন্ট্রি সম্পাদনা");
            stage.initModality(Modality.APPLICATION_MODAL); // Background block rakhar jonno
            stage.initOwner(source.getScene().getWindow());
            stage.setScene(new Scene(root));

            SupplyDialogController controller = loader.getController();
            if (entry != null) {
                controller.setEditEntry(entry, index); // Edit hole existing data pass kora
            }

            stage.showAndWait();

            // Popup save kore close korle page ta refresh kora
            if (controller.isSaveClicked()) {
                loadAndRender();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ekta active entry ke complete kore history te pathanor method
    private void completeEntry(int index) {
        if (UserSession.isUser()) {
            return; // Normal user er power nai
        }
        List<SupplyEntry> activeEntries = DataManager.loadSupplyEntries();
        if (index < 0 || index >= activeEntries.size()) {
            return;
        }

        // Active list theke remove kora
        SupplyEntry entry = activeEntries.remove(index);

        // Notun completed object toiri kora (date r time shoho)
        SupplyEntry completedEntry = new SupplyEntry(
                entry.getProduct(),
                entry.getStage(),
                entry.getLocation(),
                entry.getRegion(),
                entry.getAmount(),
                entry.getPrice(),
                entry.getTransportMode(),
                entry.getEfficiency(),
                entry.getWastePercent(),
                entry.getDelayHours(),
                true, // Status done
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );

        // History list e add kora
        List<SupplyEntry> historyEntries = DataManager.loadSupplyHistory();
        historyEntries.add(0, completedEntry); // List er ekdom upore add kora

        // Save kora database e
        DataManager.saveSupplyEntries(activeEntries);
        DataManager.saveSupplyHistory(historyEntries);

        // Screen refresh kora
        loadAndRender();
    }

    // Active entry list er jonno ekta row (HBox) design toiri korar method
    private HBox createEntryRow(SupplyEntry entry, int index) {
        HBox row = new HBox(20);
        row.getStyleClass().add("entry-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Stage onujayi icon r color set kora
        String colorHex = "#3B82F6"; // Default blue
        String svgIcon = "M20 8h-3V4H3v4H2v2h1v10h18V10h1V8zm-10 8H8v-2h2v2zm0-4H8v-2h2v2zm4 4h-2v-2h2v2zm0-4h-2v-2h2v2zm4 4h-2v-2h2v2zm0-4h-2v-2h2v2z";

            if (STAGE_SOURCE.equals(entry.getStage())) {
            colorHex = "#10B981"; // Green
            svgIcon = "M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm4 8H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm4 8h-2v-2h2v2zm0-4h-2v-2h2v2zm0-4h-2V9h2v2zm4 8h-2v-2h2v2zm0-4h-2v-2h2v2zm0-4h-2V9h2v2z";
        } else if (STAGE_WAREHOUSE.equals(entry.getStage())) {
            colorHex = "#F59E0B"; // Orange
            svgIcon = "M12 3L2 12h3v8h14v-8h3L12 3zm0 4.84l4.5 4.05V18H7.5v-6.11L12 7.84z";
        } else if (STAGE_WHOLESALE.equals(entry.getStage())) {
            colorHex = "#8B5CF6"; // Purple
            svgIcon = "M18.6 6.62c-.51-.22-1.09-.39-1.74-.5-1.2-.2-2.86-.2-4.86-.2s-3.66 0-4.86.2c-.65.11-1.23.28-1.74.5C4.22 7.14 4 8.04 4 9.12V15c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V9.12c0-1.08-.22-1.98-1.4-2.5zM12 15c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z";
        } else if (STAGE_RETAIL.equals(entry.getStage())) {
            colorHex = "#EC4899"; // Pink
            svgIcon = "M12 2L4.5 20.29l.71.71L12 18l6.79 3 .71-.71z";
        }

        // Icon er box toiri
        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: " + colorHex + "33; -fx-background-radius: 12; -fx-min-width: 45; -fx-min-height: 45;");
        SVGPath icon = new SVGPath();
        icon.setContent(svgIcon);
        icon.setFill(Color.web(colorHex));
        icon.setScaleX(1.1);
        icon.setScaleY(1.1);
        iconBox.getChildren().add(icon);

        // Title r sub title
        VBox info = new VBox(3);
        Label title = new Label(entry.getProduct() + "   " + entry.getStage());
        title.getStyleClass().add("entry-title");
        Label sub = new Label("📍 " + entry.getRegion() + " | " + entry.getLocation() + " | " + entry.getAmount() + " MT");
        sub.getStyleClass().add("entry-subtitle");
        info.getChildren().addAll(title, sub);

        // Delay r Waste dekhano
        HBox details = new HBox(12);
        details.setAlignment(Pos.CENTER_LEFT);
        Label delayLbl = new Label("🕒 " + formatOneDecimal(entry.getDelayHours()) + " ঘণ্টা");
        delayLbl.setTextFill(Color.web("#F59E0B"));
        Label wasteLbl = new Label(formatOneDecimal(entry.getWastePercent()) + "% নষ্ট");
        wasteLbl.setTextFill(Color.web("#EF4444"));
        details.getChildren().addAll(delayLbl, wasteLbl);

        // Faka jayga
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Price dekhano
        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        Label price = new Label("৳" + entry.getPrice());
        price.getStyleClass().add("entry-price");
        Label unit = new Label("প্রতি কেজি");
        unit.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");
        priceBox.getChildren().addAll(price, unit);

        // Edit r Complete korar button
        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        Button editBtn = new Button("সম্পাদনা");
        editBtn.getStyleClass().addAll("entry-action-btn", "btn-edit-entry");
        editBtn.setOnAction(e -> openEditEntryDialog((Node) e.getSource(), entry, index));

        Button completeBtn = new Button("Complete");
        completeBtn.getStyleClass().addAll("entry-action-btn", "btn-complete-entry");
        completeBtn.setOnAction(e -> {
            completeEntry(index);
            // Done korar por message dekhano
            Alert doneAlert = new Alert(Alert.AlertType.INFORMATION);
            doneAlert.setHeaderText(null);
            doneAlert.setTitle("সফল");
            doneAlert.setContentText("এন্ট্রিটি history তে সরানো হয়েছে।");
            doneAlert.showAndWait();
        });

        // Admin holei sudhu button gulo dekhabe, normal user hole dekhabe na
        if (UserSession.isAdmin()) {
            actionBox.getChildren().addAll(editBtn, completeBtn);
            row.getChildren().addAll(iconBox, info, details, spacer, priceBox, actionBox);
        } else {
            row.getChildren().addAll(iconBox, info, details, spacer, priceBox);
        }
        return row;
    }

    // History list er jonno ekta choto row toiri korar method
    private HBox createHistoryRow(SupplyEntry entry) {
        HBox row = new HBox(12);
        row.getStyleClass().add("history-row");
        row.setAlignment(Pos.CENTER_LEFT);

        Label item = new Label(entry.getProduct() + " - " + entry.getStage());
        item.getStyleClass().add("history-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Complete korar somoy thakle seta dekhano, naile "-" dekhano
        String completedAt = entry.getCompletedAt() == null || entry.getCompletedAt().isBlank()
                ? "-"
                : entry.getCompletedAt();
        Label meta = new Label(entry.getRegion() + " | " + completedAt);
        meta.getStyleClass().add("history-meta");

        row.getChildren().addAll(item, spacer, meta);
        return row;
    }

    // Kono data na thakle ekta text bananor method
    private Label createEmptyState(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("empty-state-label");
        return label;
    }

    // Filter kora onujayi active entry gulo UI te show korar method
    private void renderActiveEntries() {
        if (entryListContainer == null) {
            return;
        }

        entryListContainer.getChildren().clear();
        // Index gulo filter kore ana
        List<Integer> order = getFilteredIndexesBySelectedProduct(cachedActiveEntries);
        // Seti onujayi list draw kora
        for (Integer idx : order) {
            entryListContainer.getChildren().add(createEntryRow(cachedActiveEntries.get(idx), idx));
        }

        if (order.isEmpty()) {
            entryListContainer.getChildren().add(createEmptyState("এই পণ্যের কোনো এন্ট্রি পাওয়া যায়নি"));
        }
    }

    // Dropdown e je product select ase sei onujayi index ber korar logic
    private List<Integer> getFilteredIndexesBySelectedProduct(List<SupplyEntry> entries) {
        List<Integer> filtered = new ArrayList<>();
        String selectedProduct = productFilterCombo == null ? null : productFilterCombo.getValue();
        // Sob product select thakle kono filter chara puro list return kora
        if (selectedProduct == null || selectedProduct.isBlank() || ALL_PRODUCTS.equals(selectedProduct)) {
            for (int i = 0; i < entries.size(); i++) {
                filtered.add(i);
            }
            return filtered;
        }

        // Exact r partial match khoja
        String query = selectedProduct.trim().toLowerCase(Locale.ROOT);
        List<Integer> exact = new ArrayList<>();
        List<Integer> partial = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            String entryProduct = entries.get(i).getProduct() == null ? "" : entries.get(i).getProduct().trim();
            String entryProductLower = entryProduct.toLowerCase(Locale.ROOT);
            if (entryProductLower.equals(query)) {
                exact.add(i);
            } else if (entryProductLower.contains(query)) {
                partial.add(i);
            }
        }

        // Exact match prothome dekhano hobe
        filtered.addAll(exact);
        filtered.addAll(partial);
        return filtered;
    }

    // Active entry theke product er nam niye dropdown er list toiri kora
    private void setupProductFilter(List<SupplyEntry> entries) {
        if (productFilterCombo == null) {
            return;
        }

        String selected = productFilterCombo.getValue();
        // Duplicate product thekate Set use kora hoise
        LinkedHashSet<String> products = new LinkedHashSet<>();

        for (SupplyEntry entry : entries) {
            addProductOption(products, entry.getProduct());
        }

        allProductOptions.clear();
        allProductOptions.add(ALL_PRODUCTS); // "সকল পণ্য" option ta prothome add kora
        allProductOptions.addAll(products);

        productFilterCombo.setItems(FXCollections.observableArrayList(allProductOptions));
        productFilterCombo.setEditable(false);

        // Agte theke kisu select kora na thakle default bhabe "সকল পণ্য" dekhabe
        if (selected == null || !allProductOptions.contains(selected)) {
            selected = ALL_PRODUCTS;
        }
        productFilterCombo.setValue(selected);
    }

    // Empty na hole list e nam add kora
    private void addProductOption(LinkedHashSet<String> products, String value) {
        if (value == null) {
            return;
        }
        String v = value.trim();
        if (!v.isEmpty()) {
            products.add(v);
        }
    }

    // Stage er count set korar helper method
    private void setStageCountText(Label label, int count) {
        if (label != null) label.setText(count + " এন্ট্রি");
    }

    // Stage er percentage set korar helper method
    private void setStageShareText(Label label, int count, int total) {
        if (label != null) {
            double share = total == 0 ? 0 : (count * 100.0) / total;
            label.setText(formatOneDecimal(share) + "%");
        }
    }

    // String theke safe bhabe Number (double) e convert kora
    private double parseDoubleSafe(String raw) {
        if (raw == null) return 0;
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException ignored) {
            return 0; // Error asle by default 0 return korbe
        }
    }

    // Doshomik (Decimal) er por 1 ghor dekhabar jonno format kora
    private String formatOneDecimal(double value) {
        return String.format(Locale.US, "%.1f", value);
    }
}