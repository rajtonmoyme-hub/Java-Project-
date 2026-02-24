package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

// Notun gudam (Warehouse) add korar jonno je popup (dialog) ashe, tar controller class
public class WarehouseDialogController {

    // FXML theke UI element gulo link kora hocche
    // Text lekhar ghor gulo (TextField)
    @FXML private TextField tfWarehouseName, tfOwnerName, tfAddress, tfCapacity, tfStock, tfLicense, tfContact;
    // Dropdown gulo (ComboBox)
    @FXML private ComboBox<String> cbType, cbRegion;
    // Tarikh (Date) select korar ghor
    @FXML private DatePicker dpExpiry;
    // Cold storage kina ta select korar toggle button
    @FXML private ToggleButton btnColdStorage;

    // Save button a click kora hoise kina ta track korar jonno
    private boolean saveClicked = false;

    // Popup open howar sathe sathe ei method ta call hobe
    @FXML
    public void initialize() {
        // ড্রপডাউন অপশন সেট করা (Dropdown e ki ki option thakbe ta set kora hocche)
        cbType.setItems(FXCollections.observableArrayList("সরকারি", "বেসরকারি", "ব্যবসায়ী", "সমবায়"));
        cbRegion.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "খুলনা", "রাজশাহী", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ"));

        // ডিফল্ট ভ্যালু (Popup open hole by default kon option gulo select thakbe)
        cbType.setValue("বেসরকারি");
        cbRegion.setValue("ঢাকা");
    }

    // Main page theke check korar jonno je user save korse naki cancel kore ber hoye gese
    public boolean isSaveClicked() {
        return saveClicked;
    }

    // Save button e click korle ei method ta kaj korbe
    @FXML
    private void handleSave() {
        // (User name, capacity ba stock faka rakhse kina)
        if (tfWarehouseName.getText().trim().isEmpty() ||
                tfCapacity.getText().trim().isEmpty() ||
                tfStock.getText().trim().isEmpty()) {

            // Faka thakle warning dekhabe
            showAlert("অসম্পূর্ণ তথ্য", "দয়া করে গুদামের নাম, ধারণক্ষমতা এবং মজুদ অবশ্যই পূরণ করুন।");
            return;
        }

        try {
            // String (Text) theke Double (Number) e convert kora hocche
            double capacity = Double.parseDouble(tfCapacity.getText().trim());
            double stock = Double.parseDouble(tfStock.getText().trim());

            // Form er sob data niye ekta notun Warehouse object toiri kora hocche
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
                    btnColdStorage.isSelected() // Toggle button on thakle true, off thakle false asbe
            );

            //jason file e new list add
            List<Warehouse> list = DataManager.loadWarehouses();
            list.add(0, newWarehouse); // jate list er opore dekhay added card ta
            DataManager.saveWarehouses(list); // Update kora list ta abar file e save kora hocche

            // Save hoye gese ta mark kore popup window ta close kora
            saveClicked = true;
            closeStage();

        } catch (NumberFormatException e) {
            // ইউজার যদি ধারণক্ষমতা বা মজুদের ঘরে ভুল করে টেক্সট (যেমন: A, B, C) লিখে ফেলে, tokhon ei error dhorbe
            showAlert("ভুল ইনপুট", "ধারণক্ষমতা এবং মজুদের ঘরে শুধুমাত্র সংখ্যা (Number) লিখুন।");
        }
    }

    // অ্যালার্ট দেখানোর মেথড (Error ba warning message dekhanor choto popup)
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait(); // User OK na kora porjonto wait korbe
    }

    // Cancel button e click korle popup close hobe (save hobe na)
    @FXML
    private void handleCancel() {
        saveClicked = false;
        closeStage();
    }

    // Popup (Window/Stage) bondho korar asol method
    private void closeStage() {
        Stage stage = (Stage) tfWarehouseName.getScene().getWindow();
        stage.close();
    }
}