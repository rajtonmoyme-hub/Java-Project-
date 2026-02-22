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

public class ImportDialogController {

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

    private boolean saveClicked = false;
    private ImportRecord createdRecord;

    @FXML
    public void initialize() {
        cbCategory.setItems(FXCollections.observableArrayList("চাল", "গম", "চিনি", "তেল", "ডাল", "সবজি", "অন্যান্য"));
        cbEntryPort.setItems(FXCollections.observableArrayList("চট্টগ্রাম বন্দর", "মোংলা বন্দর", "পায়রা বন্দর", "বেনাপোল স্থলবন্দর"));
        cbCategory.setValue("চাল");
        cbEntryPort.setValue("চট্টগ্রাম বন্দর");
        dpImportDate.setValue(LocalDate.now());
        tgCustomsCleared.setSelected(false);
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public ImportRecord getCreatedRecord() {
        return createdRecord;
    }

    @FXML
    private void handleSave() {
        if (!isValidInput()) {
            showWarning("অসম্পূর্ণ তথ্য", "পণ্যের নাম, উৎস দেশ, পরিমাণ, FOB ও ল্যান্ডিং কস্ট সঠিকভাবে দিন।");
            return;
        }

        String status = tgCustomsCleared.isSelected() ? "যাচাইকৃত" : "অমিমাংসিত";
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

        saveClicked = true;
        closeStage();
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    @FXML
    private void handleClose() {
        closeStage();
    }

    private boolean isValidInput() {
        return !tfProductName.getText().trim().isEmpty()
                && !tfSourceCountry.getText().trim().isEmpty()
                && isDouble(tfQuantityMt.getText())
                && isDouble(tfFobPrice.getText())
                && isDouble(tfLandingCost.getText())
                && dpImportDate.getValue() != null;
    }

    private boolean isDouble(String text) {
        try {
            Double.parseDouble(text.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return Double.parseDouble(text.trim());
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeStage() {
        if (tfProductName != null && tfProductName.getScene() != null) {
            ((Stage) tfProductName.getScene().getWindow()).close();
        }
    }
}
