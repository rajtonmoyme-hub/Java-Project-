package com.example.foodprice;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private static final List<String> FIXED_DIVISIONS = List.of(
            "ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা",
            "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"
    );

    @FXML private Label lblBannerDate;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblFarmerCount;
    @FXML private Label lblTotalWarehouses;
    @FXML private Label lblTotalStock;
    @FXML private Label lblRiskProducts;
    @FXML private Label lblProductsTrend;
    @FXML private Label lblFarmersTrend;
    @FXML private Label lblStockTrend;
    @FXML private Label lblRiskBadge;

    @FXML private BarChart<String, Number> regionChart;
    @FXML private VBox importListContainer;
    @FXML private VBox alertListContainer;
    @FXML private VBox warehouseProgressContainer;

    // --- RBAC এর জন্য বাটন ID ---
    @FXML private Button btnQuickAddProduct;
    @FXML private Button btnQuickAddFarmer;
    @FXML private Button btnQuickAddWarehouse;
    @FXML private Button btnQuickImportRecord;

    private Timeline autoRefresh;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SidebarController.activeButtonId = "btnDashboard";
        regionChart.setAnimated(false);
        CategoryAxis xAxis = (CategoryAxis) regionChart.getXAxis();
        xAxis.setAnimated(false);
        xAxis.setCategories(javafx.collections.FXCollections.observableArrayList(FIXED_DIVISIONS));
        lblBannerDate.setText(formatBanglaDate(LocalDate.now()));

        // --- RBAC লজিক (ইউজার হলে বাটনগুলো হাইড হবে) ---
        if (UserSession.isUser()) {
            if (btnQuickAddProduct != null) {
                btnQuickAddProduct.setVisible(false);
                btnQuickAddProduct.setManaged(false);
            }
            if (btnQuickAddWarehouse != null) {
                btnQuickAddWarehouse.setVisible(false);
                btnQuickAddWarehouse.setManaged(false);
            }
            if (btnQuickImportRecord != null) {
                btnQuickImportRecord.setVisible(false);
                btnQuickImportRecord.setManaged(false);
            }
        }

        refreshDashboardData();
        startAutoRefresh();
    }

    @FXML
    void handleRefresh() {
        refreshDashboardData();
    }

    @FXML
    void quickAddProduct(ActionEvent event) {
        if (UserSession.isUser()) return;
        openDialogOverPage(event, "/products.fxml", "/product_dialog.fxml", "নতুন পণ্য যোগ করুন", "btnProducts");
    }

    @FXML
    void quickAddFarmer(ActionEvent event) {
        openDialogOverPage(event, "/farmers.fxml", "/farmer_dialog.fxml", "নতুন কৃষক নিবন্ধন", "btnFarmers");
    }

    @FXML
    void quickAddWarehouse(ActionEvent event) {
        if (UserSession.isUser()) return;
        openDialogOverPage(event, "/warehouse.fxml", "/warehouse_dialog.fxml", "নতুন গুদাম যোগ করুন", "btnWarehouse");
    }

    @FXML
    void goToImport(ActionEvent event) {
        if (UserSession.isUser()) return;
        navigate(event, "/import.fxml");
    }

    private void openDialogOverPage(ActionEvent event,
                                    String pageFxmlPath,
                                    String dialogFxmlPath,
                                    String dialogTitle,
                                    String activeButtonId) {
        try {
            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SidebarController.activeButtonId = activeButtonId;

            URL pageLocation = getClass().getResource(pageFxmlPath);
            if (pageLocation == null) {
                return;
            }
            Parent pageRoot = FXMLLoader.load(pageLocation);
            Scene currentScene = ownerStage.getScene();
            if (currentScene == null) {
                ownerStage.setScene(new Scene(pageRoot, 1400, 900));
            } else {
                currentScene.setRoot(pageRoot);
            }

            URL dialogLocation = getClass().getResource(dialogFxmlPath);
            if (dialogLocation == null) {
                return;
            }
            Parent dialogRoot = FXMLLoader.load(dialogLocation);
            Stage dialogStage = new Stage();
            dialogStage.setTitle(dialogTitle);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(ownerStage);
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.showAndWait();

            // Dialog close হওয়ার পর target page নতুন data সহ reload
            Parent refreshedPageRoot = FXMLLoader.load(pageLocation);
            Scene reloadedScene = ownerStage.getScene();
            if (reloadedScene == null) {
                ownerStage.setScene(new Scene(refreshedPageRoot, 1400, 900));
            } else {
                reloadedScene.setRoot(refreshedPageRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshDashboardData() {
        List<Product> products = DataManager.loadProducts();
        List<Farmer> farmers = DataManager.loadFarmers();
        List<Warehouse> warehouses = DataManager.loadWarehouses();
        List<ImportRecord> imports = DataManager.loadImportRecords(); // Assuming you have this
        List<SupplyEntry> supplies = DataManager.loadSupplyEntries();

        int totalProducts = products.size();
        int totalFarmers = farmers.size();
        int totalWarehouses = warehouses.size();

        double totalStockMt = warehouses.stream().mapToDouble(Warehouse::getCurrentStock).sum();
        double totalCapacityMt = warehouses.stream().mapToDouble(Warehouse::getCapacity).sum();
        double avgPriceChange = products.stream()
                .filter(p -> p.getPrevPrice() > 0)
                .mapToDouble(p -> ((p.getCurrentPrice() - p.getPrevPrice()) / p.getPrevPrice()) * 100.0)
                .average()
                .orElse(0.0);

        long riskyProducts = products.stream().filter(this::isRiskyProduct).count();
        long verifiedFarmers = farmers.stream().filter(Farmer::isVerified).count();

        double currentMonthImport = sumMonthlyImport(imports, LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        LocalDate prevMonth = LocalDate.now().minusMonths(1);
        double previousMonthImport = sumMonthlyImport(imports, prevMonth.getYear(), prevMonth.getMonthValue());
        double stockChangePct = previousMonthImport > 0 ? ((currentMonthImport - previousMonthImport) / previousMonthImport) * 100.0 : 0.0;

        List<AlertItem> alerts = buildAlerts(products, warehouses, supplies, imports);

        lblTotalProducts.setText(toBanglaDigits(String.valueOf(totalProducts)));
        lblFarmerCount.setText(toBanglaDigits(String.valueOf(totalFarmers)));
        lblTotalWarehouses.setText(toBanglaDigits(String.valueOf(totalWarehouses)));
        lblTotalStock.setText(toBanglaDigits(formatNumber(totalStockMt)));
        lblRiskProducts.setText(toBanglaDigits(String.valueOf(riskyProducts)));
        lblRiskBadge.setText(toBanglaDigits(String.valueOf(alerts.size())));

        setTrendLabel(lblProductsTrend, avgPriceChange, true);

        String farmerTrendText = "↗ " + toBanglaDigits(String.valueOf(verifiedFarmers)) + "/" + toBanglaDigits(String.valueOf(totalFarmers)) + " যাচাইকৃত";
        lblFarmersTrend.setText(farmerTrendText);
        setLabelTrendClass(lblFarmersTrend, true);

        lblStockTrend.setText((stockChangePct >= 0 ? "↗ +" : "↘ ") + toBanglaDigits(String.format("%.1f", Math.abs(stockChangePct))) + "%");
        setLabelTrendClass(lblStockTrend, stockChangePct >= 0);

        renderRegionChart(warehouses);
        renderImportTable(imports);
        renderAlertTable(alerts);
        renderWarehouseProgress(warehouses, totalCapacityMt, totalStockMt);
    }

    private void renderRegionChart(List<Warehouse> warehouses) {
        regionChart.getData().clear();

        Map<String, Double> regionStock = new LinkedHashMap<>();
        for (Warehouse w : warehouses) {
            String region = safeText(w.getRegion(), "অনির্ধারিত");
            regionStock.merge(region, Math.max(0, w.getCurrentStock()), Double::sum);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (String division : FIXED_DIVISIONS) {
            series.getData().add(new XYChart.Data<>(division, regionStock.getOrDefault(division, 0.0)));
        }

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("ডাটা নেই", 0));
        }

        regionChart.getData().add(series);
    }

    private void renderImportTable(List<ImportRecord> imports) {
        importListContainer.getChildren().clear();

        List<ImportRecord> sorted = new ArrayList<>(imports);
        sorted.sort(Comparator.comparing((ImportRecord r) -> safeDate(r.getDate())).reversed());

        int limit = Math.min(5, sorted.size());
        for (int i = 0; i < limit; i++) {
            ImportRecord r = sorted.get(i);
            String recordId = String.format("IMP-%d-%03d", LocalDate.now().getYear(), i + 1);
            String qty = toBanglaDigits(formatNumber(r.getQuantityMt())) + " MT";
            String date = safeDate(r.getDate()) != null ? safeDate(r.getDate()).format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)) : "-";
            String statusText = r.isCustomsCleared() ? "সম্পন্ন" : "প্রক্রিয়াধীন";
            String statusClass = r.isCustomsCleared() ? "status-done" : "status-pending";

            importListContainer.getChildren().add(createTableRow(
                    toBanglaDigits(recordId),
                    safeText(r.getProduct(), "-"),
                    qty,
                    safeText(r.getSource(), "-"),
                    date,
                    statusText,
                    statusClass
            ));
        }

        if (limit == 0) {
            Label empty = new Label("কোন আমদানি রেকর্ড নেই");
            empty.getStyleClass().add("table-text");
            importListContainer.getChildren().add(empty);
        }
    }

    private HBox createTableRow(String id, String prod, String amt, String src, String date, String status, String statusClass) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setAlignment(Pos.CENTER_LEFT);

        Label l1 = new Label(id); l1.setPrefWidth(120); l1.getStyleClass().add("table-text");
        Label l2 = new Label(prod); l2.setPrefWidth(100); l2.getStyleClass().add("table-text");
        Label l3 = new Label(amt); l3.setPrefWidth(100); l3.getStyleClass().add("table-text");
        Label l4 = new Label(src); l4.setPrefWidth(100); l4.getStyleClass().add("table-text");
        Label l5 = new Label(date); l5.setPrefWidth(120); l5.getStyleClass().add("table-text");
        Label l6 = new Label(status); l6.getStyleClass().add(statusClass);

        row.getChildren().addAll(l1, l2, l3, l4, l5, l6);
        return row;
    }

    private void renderAlertTable(List<AlertItem> alerts) {
        alertListContainer.getChildren().clear();

        int limit = Math.min(6, alerts.size());
        for (int i = 0; i < limit; i++) {
            AlertItem a = alerts.get(i);
            alertListContainer.getChildren().add(createAlertRow(a.title, a.subtitle));
        }

        if (limit == 0) {
            alertListContainer.getChildren().add(createAlertRow("কোন ঝুঁকি নেই", "বর্তমানে সকল ডাটা স্বাভাবিক রয়েছে"));
        }
    }

    private HBox createAlertRow(String title, String sub) {
        HBox row = new HBox(8);
        row.getStyleClass().add("alert-item");
        row.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(3, Color.web("#EF4444"));
        VBox texts = new VBox(2);

        Label t = new Label(title); t.getStyleClass().add("alert-title");
        Label s = new Label(sub); s.getStyleClass().add("alert-sub");
        texts.getChildren().addAll(t, s);

        row.getChildren().addAll(dot, texts);
        return row;
    }

    private void renderWarehouseProgress(List<Warehouse> warehouses, double totalCapacity, double totalStock) {
        warehouseProgressContainer.getChildren().clear();

        List<Warehouse> sorted = new ArrayList<>(warehouses);
        sorted.sort(Comparator.comparingDouble((Warehouse w) -> usagePct(w.getCurrentStock(), w.getCapacity())).reversed());

        int limit = Math.min(6, sorted.size());
        for (int i = 0; i < limit; i++) {
            Warehouse w = sorted.get(i);
            double usage = usagePct(w.getCurrentStock(), w.getCapacity());
            String fillClass = usage >= 90 ? "prog-fill-red" : "prog-fill-green";
            String percentClass = usage >= 90 ? "percent-red" : "percent-green";
            warehouseProgressContainer.getChildren().add(createProgressRow(
                    safeText(w.getName(), "গুদাম"),
                    toBanglaDigits(formatNumber(w.getCurrentStock())) + " / " + toBanglaDigits(formatNumber(w.getCapacity())) + " MT",
                    (int) Math.round(usage),
                    fillClass,
                    percentClass
            ));
        }

        if (limit == 0) {
            warehouseProgressContainer.getChildren().add(createProgressRow("ডাটা নেই", "0 / 0 MT", 0, "prog-fill-green", "percent-green"));
        }

        lblTotalStock.setText(toBanglaDigits(formatNumber(totalStock)));
        lblTotalWarehouses.setText(toBanglaDigits(String.valueOf(warehouses.size())));
    }

    private VBox createProgressRow(String title, String val, int percent, String fillClass, String textClass) {
        VBox box = new VBox(5);
        box.getStyleClass().add("progress-container");

        HBox top = new HBox();
        Label t = new Label(title); t.getStyleClass().add("progress-title");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label v = new Label(val); v.getStyleClass().add("progress-val");
        Label p = new Label("  " + toBanglaDigits(String.valueOf(percent)) + "%");
        p.getStyleClass().addAll("progress-percent", textClass);
        top.getChildren().addAll(t, spacer, v, p);

        StackPane track = new StackPane();
        track.getStyleClass().add("prog-bg");
        Region fill = new Region();
        fill.getStyleClass().add(fillClass);
        fill.prefWidthProperty().bind(track.widthProperty().multiply(Math.max(0, Math.min(100, percent)) / 100.0));
        fill.setMinHeight(8);
        fill.setPrefHeight(8);
        track.setMinHeight(8);
        track.setPrefHeight(8);
        track.getChildren().add(fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        box.getChildren().addAll(top, track);
        return box;
    }

    private List<AlertItem> buildAlerts(List<Product> products, List<Warehouse> warehouses, List<SupplyEntry> supplies, List<ImportRecord> imports) {
        List<AlertItem> alerts = new ArrayList<>();

        for (Product p : products) {
            if (isRiskyProduct(p)) {
                double change = p.getPrevPrice() > 0 ? ((p.getCurrentPrice() - p.getPrevPrice()) / p.getPrevPrice()) * 100.0 : 0.0;
                alerts.add(new AlertItem(
                        safeText(p.getNameBn(), safeText(p.getNameEn(), "পণ্য")) + " — মূল্য ঝুঁকি",
                        "বর্তমান: " + toBanglaDigits(formatNumber(p.getCurrentPrice())) + ", পরিবর্তন: " + toBanglaDigits(String.format("%.1f", change)) + "%"
                ));
            }
        }

        for (Warehouse w : warehouses) {
            double usage = usagePct(w.getCurrentStock(), w.getCapacity());
            if (usage >= 90) {
                alerts.add(new AlertItem(
                        safeText(w.getName(), "গুদাম") + " — উচ্চ ব্যবহার",
                        "বর্তমান: " + toBanglaDigits(formatNumber(w.getCurrentStock())) + " MT, ধারণক্ষমতা: " + toBanglaDigits(formatNumber(w.getCapacity())) + " MT"
                ));
            }
        }

        for (SupplyEntry s : supplies) {
            if (s.getDelayHours() > 24 || s.getWastePercent() > 15) {
                alerts.add(new AlertItem(
                        safeText(s.getProduct(), "পণ্য") + " — সরবরাহ ঝুঁকি",
                        safeText(s.getRegion(), "অঞ্চল") + ", বিলম্ব: " + toBanglaDigits(formatNumber(s.getDelayHours())) + " ঘন্টা"
                ));
            }
        }

        for (ImportRecord r : imports) {
            if (!r.isCustomsCleared()) {
                alerts.add(new AlertItem(
                        safeText(r.getProduct(), "আমদানি") + " — ক্লিয়ার হয়নি",
                        "উৎস: " + safeText(r.getSource(), "-") + ", তারিখ: " + safeText(r.getDate(), "-")
                ));
            }
        }

        return alerts;
    }

    private boolean isRiskyProduct(Product p) {
        if (p == null) return false;
        boolean lowStock = p.getStock() > 0 && p.getStock() < 100;
        boolean sharpChange = false;
        if (p.getPrevPrice() > 0) {
            double change = Math.abs((p.getCurrentPrice() - p.getPrevPrice()) / p.getPrevPrice()) * 100.0;
            sharpChange = change >= 20;
        }
        return lowStock || sharpChange;
    }

    private double sumMonthlyImport(List<ImportRecord> imports, int year, int month) {
        return imports.stream()
                .filter(i -> {
                    LocalDate d = safeDate(i.getDate());
                    return d != null && d.getYear() == year && d.getMonthValue() == month;
                })
                .mapToDouble(ImportRecord::getQuantityMt)
                .sum();
    }

    private double usagePct(double stock, double capacity) {
        return capacity > 0 ? (stock / capacity) * 100.0 : 0.0;
    }

    private void setTrendLabel(Label label, double percent, boolean includePlus) {
        String sign = percent >= 0 ? "↗ " : "↘ ";
        String prefix = includePlus && percent >= 0 ? "+" : "";
        label.setText(sign + prefix + toBanglaDigits(String.format("%.1f", Math.abs(percent))) + "%");
        setLabelTrendClass(label, percent >= 0);
    }

    private void setLabelTrendClass(Label label, boolean positive) {
        label.getStyleClass().remove("trend-up");
        label.getStyleClass().remove("trend-down");
        label.getStyleClass().add(positive ? "trend-up" : "trend-down");
    }

    private String safeText(String value, String fallback) {
        if (value == null) return fallback;
        String v = value.trim();
        return v.isEmpty() ? fallback : v;
    }

    private LocalDate safeDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String formatBanglaDate(LocalDate date) {
        String day = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH));
        String month = date.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH));
        String mappedDay = mapDay(day);
        String mappedMonth = mapMonth(month);
        return mappedDay + ", " + toBanglaDigits(String.valueOf(date.getDayOfMonth())) + " " + mappedMonth + ", " + toBanglaDigits(String.valueOf(date.getYear()));
    }

    private String mapDay(String dayEn) {
        switch (dayEn) {
            case "Monday": return "সোমবার";
            case "Tuesday": return "মঙ্গলবার";
            case "Wednesday": return "বুধবার";
            case "Thursday": return "বৃহস্পতিবার";
            case "Friday": return "শুক্রবার";
            case "Saturday": return "শনিবার";
            default: return "রবিবার";
        }
    }

    private String mapMonth(String monthEn) {
        switch (monthEn) {
            case "January": return "জানুয়ারি";
            case "February": return "ফেব্রুয়ারি";
            case "March": return "মার্চ";
            case "April": return "এপ্রিল";
            case "May": return "মে";
            case "June": return "জুন";
            case "July": return "জুলাই";
            case "August": return "আগস্ট";
            case "September": return "সেপ্টেম্বর";
            case "October": return "অক্টোবর";
            case "November": return "নভেম্বর";
            default: return "ডিসেম্বর";
        }
    }

    private String toBanglaDigits(String value) {
        if (value == null) return "";
        return value.replace('0', '০').replace('1', '১').replace('2', '২').replace('3', '৩')
                .replace('4', '৪').replace('5', '৫').replace('6', '৬')
                .replace('7', '৭').replace('8', '৮').replace('9', '৯');
    }

    private String formatNumber(double value) {
        if (Math.abs(value) >= 1000) return String.format("%,.0f", value);
        return String.format("%.0f", value);
    }

    private void startAutoRefresh() {
        if (autoRefresh != null) autoRefresh.stop();
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(5), e -> refreshDashboardData()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

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
        if ("/products.fxml".equals(fxmlPath)) SidebarController.activeButtonId = "btnProducts";
        else if ("/warehouse.fxml".equals(fxmlPath)) SidebarController.activeButtonId = "btnWarehouse";
        else if ("/farmers.fxml".equals(fxmlPath)) SidebarController.activeButtonId = "btnFarmers";
        else if ("/import.fxml".equals(fxmlPath)) SidebarController.activeButtonId = "btnImport";
        else SidebarController.activeButtonId = "btnDashboard";
    }

    private static class AlertItem {
        private final String title;
        private final String subtitle;

        private AlertItem(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }
}