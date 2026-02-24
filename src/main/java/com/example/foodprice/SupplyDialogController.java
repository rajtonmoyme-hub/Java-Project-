package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

// Supply Chain er notun entry add kora ba edit korar jonno je popup (dialog) ashe, tar controller
public class SupplyDialogController {

    // FXML theke UI element gulo link kora hocche
    // Dropdown (ComboBox) gulo
    @FXML private ComboBox<String> cbProduct, cbStage, cbRegion, cbTransport;
    // Text lekhar ghor (TextField) gulo
    @FXML private TextField tfLocation, tfAmount, tfPrice, tfEfficiency, tfWaste, tfDelay;
    // Popup er title r save button
    @FXML private Label dialogTitleLabel;
    @FXML private Button saveButton;

    // Save button a click kora hoise kina ta track korar jonno
    private boolean saveClicked = false;
    // Eta ki notun entry naki purano entry edit kora hocche ta bojhar jonno
    private boolean editMode = false;
    // Edit mode hole kon number (index) data ta edit hocche tar track
    private int editIndex = -1;

    // Popup open howar sathe sathe ei method ta call hobe
    @FXML
    public void initialize() {
        // Dropdown gulote ki ki option thakbe ta set kora hocche
        cbProduct.setItems(FXCollections.observableArrayList("চাল", "গম", "চিনি", "তেল", "পেঁয়াজ", "আলু", "সবজি", "ডাল"));
        cbStage.setItems(FXCollections.observableArrayList("উৎস", "পরিবহন", "গুদাম", "পাইকারি", "খুচরা"));
        cbRegion.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "খুলনা", "রাজশাহী", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ"));
        cbTransport.setItems(FXCollections.observableArrayList("ট্রাক", "পিকআপ", "নৌকা", "ট্রেন"));

        // Popup open hole by default kon option gulo select kora thakbe ta set kora
        cbStage.setValue("উৎস");
        cbRegion.setValue("ঢাকা");
        cbTransport.setValue("ট্রাক");
    }

    // Main page theke check korar jonno je user save korse naki cancel kore ber hoye gese
    public boolean isSaveClicked() { return saveClicked; }

    // Jodi entry Edit korar jonno popup open hoy, tokhon ei method ta call hobe r ager data gulo ghor e bosiye dibe
    public void setEditEntry(SupplyEntry entry, int index) {
        if (entry == null) return;

        // Edit mode on kora r index set kora
        this.editMode = true;
        this.editIndex = index;

        // Dialog er title r button er text change kore 'Edit' onujayi kora
        if (dialogTitleLabel != null) {
            dialogTitleLabel.setText("সাপ্লাই এন্ট্রি সম্পাদনা");
        }
        if (saveButton != null) {
            saveButton.setText("আপডেট সংরক্ষণ");
        }

        // Ager data gulo form er input ghor gulote set kora hocche
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

    // Save ba Update button e click korle ei method ta kaj korbe
    @FXML
    private void handleSave() {
        // Prothome check kora hocche form er input thik ase kina
        if (isValidInput()) {
            try {
                // Form er sob data niye ekta notun SupplyEntry object toiri kora hocche
                // Jodi number er ghore kisu na lekhe, tahole default hisabe "0" dhore nibe
                SupplyEntry newEntry = new SupplyEntry(
                        cbProduct.getValue(), cbStage.getValue(), tfLocation.getText(), cbRegion.getValue(),
                        tfAmount.getText(), tfPrice.getText(), cbTransport.getValue(),
                        Double.parseDouble(tfEfficiency.getText().isEmpty() ? "0" : tfEfficiency.getText()),
                        Double.parseDouble(tfWaste.getText().isEmpty() ? "0" : tfWaste.getText()),
                        Double.parseDouble(tfDelay.getText().isEmpty() ? "0" : tfDelay.getText()),
                        false, null
                );

                // Database (JSON file) theke current list load kora
                List<SupplyEntry> list = DataManager.loadSupplyEntries();

                // Jodi edit mode e thake, tahole oi nirdisto index er data ta update korbe
                if (editMode && editIndex >= 0 && editIndex < list.size()) {
                    list.set(editIndex, newEntry);
                } else {
                    // R jodi notun entry hoy, tahole list er ekdom shurute (index 0) add korbe
                    list.add(0, newEntry);
                }

                // Update kora list abar file e save kora
                DataManager.saveSupplyEntries(list);

                // Save hoye gese ta mark kore popup window ta close kora
                saveClicked = true;
                closeStage();
            } catch (NumberFormatException e) {
                // Jodi text field e number er bodole onno kono text (jemon abc) diye dey tahole ei error dekhabe
                showAlert("ভুল ইনপুট", "পরিমাণ, মূল্য এবং শতকরা হারে শুধুমাত্র সংখ্যা ব্যবহার করুন।");
            }
        } else {
            // Form er kisu ghor faka thakle ei error dekhabe
            showAlert("অসম্পূর্ণ তথ্য", "দয়া করে পণ্য নির্বাচন করুন এবং পরিমাণ ও মূল্য সঠিকভাবে লিখুন।");
        }
    }

    // User sob proyojonio data (Product, Amount, Price) diyese kina ta check korar method
    private boolean isValidInput() {
        return cbProduct.getValue() != null &&
                tfAmount.getText() != null && !tfAmount.getText().trim().isEmpty() &&
                tfPrice.getText() != null && !tfPrice.getText().trim().isEmpty();
    }

    // Error ba warning message dekhanor jonno ekta choto popup toiri korar method
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait(); // User OK na kora porjonto wait korbe
    }

    // Cancel button e click korle popup close hobe
    @FXML private void handleCancel() { closeStage(); }

    // Popup (Window/Stage) bondho korar asol method
    private void closeStage() { ((Stage) tfLocation.getScene().getWindow()).close(); }
}