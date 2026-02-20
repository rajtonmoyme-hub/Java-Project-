package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class WarehouseDialogController {
    @FXML private TextField tfWarehouseName, tfOwnerName, tfAddress, tfCapacity, tfStock, tfLicense, tfContact;
    @FXML private ComboBox<String> cbType, cbRegion;
    @FXML private DatePicker dpExpiry;
    @FXML private ToggleButton btnColdStorage;

    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        cbType.setItems(FXCollections.observableArrayList("সরকারি", "বেসরকারি", "ব্যবসায়ী", "সমবায়"));
        cbRegion.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "খুলনা", "রাজশাহী", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ"));

        cbType.setValue("বেসরকারি");
        cbRegion.setValue("ঢাকা");
    }

    public boolean isSaveClicked() { return saveClicked; }

    @FXML
    private void handleSave() {
        // ১. ভ্যালিডেশন চেক (ঘর খালি আছে কিনা বা সংখ্যা কিনা)
        if (isValidInput()) {
            try {
                // ২. ডাটা পার্স করা (এখন নিরাপদ কারণ আমরা চেক করেছি)
                double capacity = Double.parseDouble(tfCapacity.getText());
                double stock = Double.parseDouble(tfStock.getText());

                Warehouse newWarehouse = new Warehouse(
                        tfWarehouseName.getText(),
                        tfOwnerName.getText(),
                        cbType.getValue(),
                        cbRegion.getValue(),
                        tfAddress.getText(),
                        capacity,
                        stock,
                        tfLicense.getText(),
                        (dpExpiry.getValue() != null ? dpExpiry.getValue().toString() : ""),
                        tfContact.getText(),
                        btnColdStorage.isSelected()
                );

                // ৩. ডাটা সেভ করা
                List<Warehouse> list = DataManager.loadWarehouses();
                list.add(0, newWarehouse);
                DataManager.saveWarehouses(list);

                saveClicked = true;
                closeStage();
            } catch (NumberFormatException e) {
                // যদি নম্বর না দিয়ে টেক্সট লেখে
                showAlert("ভুল ইনপুট", "ধারণক্ষমতা এবং মজুদের ঘরে শুধুমাত্র নম্বর লিখুন।");
            }
        } else {
            // যদি কোনো ঘর খালি থাকে
            showAlert("অসম্পূর্ণ তথ্য", "গুদামের নাম, ধারণক্ষমতা এবং মজুদ অবশ্যই পূরণ করতে হবে।");
        }
    }

    // ইনপুট সঠিক কিনা যাচাই করার মেথড
    private boolean isValidInput() {
        if (tfWarehouseName.getText() == null || tfWarehouseName.getText().trim().isEmpty()) return false;
        if (tfCapacity.getText() == null || tfCapacity.getText().trim().isEmpty()) return false;
        if (tfStock.getText() == null || tfStock.getText().trim().isEmpty()) return false;
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private void handleCancel() {
        saveClicked = false;
        closeStage();
    }

    private void closeStage() {
        ((Stage) tfWarehouseName.getScene().getWindow()).close();
    }
}