package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class SupplyDialogController {
    @FXML private ComboBox<String> cbProduct, cbStage, cbRegion, cbTransport;
    @FXML private TextField tfLocation, tfAmount, tfPrice, tfEfficiency, tfWaste, tfDelay;
    @FXML private Label dialogTitleLabel;
    @FXML private Button saveButton;

    private boolean saveClicked = false;
    private boolean editMode = false;
    private int editIndex = -1;

    @FXML
    public void initialize() {
        cbProduct.setItems(FXCollections.observableArrayList("চাল", "গম", "চিনি", "তেল", "পেঁয়াজ", "আলু", "সবজি", "ডাল"));
        cbStage.setItems(FXCollections.observableArrayList("উৎস", "পরিবহন", "গুদাম", "পাইকারি", "খুচরা"));
        cbRegion.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "খুলনা", "রাজশাহী", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ"));
        cbTransport.setItems(FXCollections.observableArrayList("ট্রাক", "পিকআপ", "নৌকা", "ট্রেন"));

        cbStage.setValue("উৎস");
        cbRegion.setValue("ঢাকা");
        cbTransport.setValue("ট্রাক");
    }

    public boolean isSaveClicked() { return saveClicked; }

    public void setEditEntry(SupplyEntry entry, int index) {
        if (entry == null) return;

        this.editMode = true;
        this.editIndex = index;

        if (dialogTitleLabel != null) {
            dialogTitleLabel.setText("সাপ্লাই এন্ট্রি সম্পাদনা");
        }
        if (saveButton != null) {
            saveButton.setText("আপডেট সংরক্ষণ");
        }

        cbProduct.setValue(entry.getProduct());
        cbStage.setValue(entry.getStage());
        cbRegion.setValue(entry.getRegion());
        cbTransport.setValue(entry.getTransportMode());
        tfLocation.setText(entry.getLocation());
        tfAmount.setText(entry.getAmount());
        tfPrice.setText(entry.getPrice());
        tfEfficiency.setText(String.valueOf(entry.getEfficiency()));
        tfWaste.setText(String.valueOf(entry.getWastePercent()));
        tfDelay.setText(String.valueOf(entry.getDelayHours()));
    }

    @FXML
    private void handleSave() {
        if (isValidInput()) {
            try {
                SupplyEntry newEntry = new SupplyEntry(
                        cbProduct.getValue(), cbStage.getValue(), tfLocation.getText(), cbRegion.getValue(),
                        tfAmount.getText(), tfPrice.getText(), cbTransport.getValue(),
                        Double.parseDouble(tfEfficiency.getText().isEmpty() ? "0" : tfEfficiency.getText()),
                        Double.parseDouble(tfWaste.getText().isEmpty() ? "0" : tfWaste.getText()),
                        Double.parseDouble(tfDelay.getText().isEmpty() ? "0" : tfDelay.getText()),
                        false, null
                );

                List<SupplyEntry> list = DataManager.loadSupplyEntries();
                if (editMode && editIndex >= 0 && editIndex < list.size()) {
                    list.set(editIndex, newEntry);
                } else {
                    list.add(0, newEntry);
                }
                DataManager.saveSupplyEntries(list);

                saveClicked = true;
                closeStage();
            } catch (NumberFormatException e) {
                showAlert("ভুল ইনপুট", "পরিমাণ, মূল্য এবং শতকরা হারে শুধুমাত্র সংখ্যা ব্যবহার করুন।");
            }
        } else {
            showAlert("অসম্পূর্ণ তথ্য", "দয়া করে পণ্য নির্বাচন করুন এবং পরিমাণ ও মূল্য সঠিকভাবে লিখুন।");
        }
    }

    private boolean isValidInput() {
        return cbProduct.getValue() != null &&
                tfAmount.getText() != null && !tfAmount.getText().trim().isEmpty() &&
                tfPrice.getText() != null && !tfPrice.getText().trim().isEmpty();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private void handleCancel() { closeStage(); }
    private void closeStage() { ((Stage) tfLocation.getScene().getWindow()).close(); }
}
