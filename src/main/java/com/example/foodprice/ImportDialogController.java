package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.time.LocalDate;

// Ei class ta notun import record add korar je popup/dialog ashe, seta control kore
public class ImportDialogController {

    // FXML theke UI er sob input field gulo (Text, Dropdown, Date etc) eikhane connect kora hocche
    @FXML private TextField tfProductName;
    @FXML private ComboBox<String> cbCategory;
    @FXML private TextField tfSourceCountry;
    @FXML private TextField tfQuantityMt;
    @FXML private TextField tfFobPrice;
    @FXML private TextField tfLandingCost;
    @FXML private DatePicker dpImportDate;
    @FXML private ComboBox<String> cbEntryPort;
    @FXML private TextField tfImporterName;
    @FXML private TextField tfLicenseNumber;
    @FXML private TextField tfLcNumber;
    @FXML private TextField tfDutyPaid;
    @FXML private ToggleButton tgCustomsCleared;
    @FXML private Label titleLabel;

    // Save button e click poreche kina seta mone rakhar jonno variable
    private boolean saveClicked = false;
    // Notun toiri kora data ta ei variable e save thakbe
    private ImportRecord createdRecord;

    // Dialog/Popup ta open holei ei method ta prothome run hobe
    @FXML
    public void initialize() {
        // Dropdown e category ar port er list gulo add kore dicche
        cbCategory.setItems(FXCollections.observableArrayList("চাল", "গম", "চিনি", "তেল", "ডাল", "সবজি", "অন্যান্য"));
        cbEntryPort.setItems(FXCollections.observableArrayList("চট্টগ্রাম বন্দর", "মোংলা বন্দর", "পায়রা বন্দর", "বেনাপোল স্থলবন্দর"));

        // Kichu default value set kore dicche jate popup open holei ei value gulo select kora thake
        cbCategory.setValue("চাল");
        cbEntryPort.setValue("চট্টগ্রাম বন্দর");
        dpImportDate.setValue(LocalDate.now()); // Ajker tarikh set kore dicche
        tgCustomsCleared.setSelected(false);
    }

    // Onno kono class theke save click hoyechilo kina seta janar jonno ei method
    public boolean isSaveClicked() {
        return saveClicked;
    }

    // Onno kono class theke notun toiri kora record ta pawar jonno ei method
    public ImportRecord getCreatedRecord() {
        return createdRecord;
    }

    // Save button e click korle ei method ta cholbe
    @FXML
    private void handleSave() {
        // Prothome check korche sob input thik ache kina, vul thakle warning dekhabe ar code ekhane thekei return korbe (niche jabe na)
        if (!isValidInput()) {
            showWarning("অসম্পূর্ণ তথ্য", "পণ্যের নাম, উৎস দেশ, পরিমাণ, FOB ও ল্যান্ডিং কস্ট সঠিকভাবে দিন।");
            return;
        }

        // Toggle button on thakle status hobe "যাচাইকৃত", na hole "অমিমাংসিত"
        String status = tgCustomsCleared.isSelected() ? "যাচাইকৃত" : "অমিমাংসিত";

        // UI er sob input field theke data niye ekta notun ImportRecord toiri korche
        createdRecord = new ImportRecord(
                tfProductName.getText().trim(),
                tfSourceCountry.getText().trim(),
                cbCategory.getValue(),
                parseDouble(tfQuantityMt.getText()),
                parseDouble(tfFobPrice.getText()),
                parseDouble(tfLandingCost.getText()),
                cbEntryPort.getValue(),
                dpImportDate.getValue().toString(),
                tfImporterName.getText().trim(),
                tfLicenseNumber.getText().trim(),
                tfLcNumber.getText().trim(),
                parseDouble(tfDutyPaid.getText()),
                tgCustomsCleared.isSelected(),
                status
        );

        // Save kora hoyeche seta true kore dilo
        saveClicked = true;
        // Save hoye gele popup ba dialog ta bondho kore dibe
        closeStage();
    }

    // Cancel button e click korle dialog bondho kore dibe
    @FXML
    private void handleCancel() {
        closeStage();
    }

    // Close (X) button e click korle dialog bondho kore dibe
    @FXML
    private void handleClose() {
        closeStage();
    }

    // Input gulo thik ache kina seta check korar method. Faka kina ba number er jaygay text diyeche kina segula check kore
    private boolean isValidInput() {
        return !tfProductName.getText().trim().isEmpty()
                && !tfSourceCountry.getText().trim().isEmpty()
                && isDouble(tfQuantityMt.getText())
                && isDouble(tfFobPrice.getText())
                && isDouble(tfLandingCost.getText())
                && dpImportDate.getValue() != null;
    }

    // Kono text asholei number (double) kina seta check korar ekta helper method
    private boolean isDouble(String text) {
        try {
            Double.parseDouble(text.trim());
            return true; // Number e convert kora gele true dibe
        } catch (Exception e) {
            return false; // Convert korte fail korle false dibe (mane number na)
        }
    }

    // Text ke number e (double) convert korar method, text faka thakle error er bodole 0 return korbe
    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return Double.parseDouble(text.trim());
    }

    // Vul input dile je warning popup/alert ashe, seta ei method theke toiri hoy
    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Ei popup ba window (Stage) ta bondho korar kaj ta eikhane hocche
    private void closeStage() {
        if (tfProductName != null && tfProductName.getScene() != null) {
            // TextField er reference diye current window ta khuje ber kore seta close kore dicche
            ((Stage) tfProductName.getScene().getWindow()).close();
        }
    }
}