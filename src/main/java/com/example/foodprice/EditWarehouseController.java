package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

// Purano ba existing Gudam (Warehouse) er data edit/update korar popup controller class
public class EditWarehouseController {

    // FXML theke UI element gulo (Text lekhar ghor, dropdown, date etc) link kora hocche
    @FXML private TextField tfWarehouseName, tfOwnerName, tfAddress, tfCapacity, tfStock, tfLicense, tfContact;
    @FXML private ComboBox<String> cbType, cbRegion;
    @FXML private DatePicker dpExpiry;
    @FXML private ToggleButton btnColdStorage;

    // Kon gudam ta edit hocche, main list e tar number (index) koto ta mone rakhar jonno
    private int warehouseIndex;
    // Update button e click kora hoise kina ta track korar jonno
    private boolean updateClicked = false;

    // Popup open howar sathe sathe ei method ta call hobe
    @FXML
    public void initialize() {
        // Dropdown e ki ki option thakbe ta set kora hocche
        cbType.setItems(FXCollections.observableArrayList("সরকারি", "বেসরকারি", "ব্যবসায়ী", "সমবায়"));
        cbRegion.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "খুলনা", "রাজশাহী", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ"));
    }

    // Main page theke jei gudam a click kora hoise, tar data ei form e niye ashar method
    public void setWarehouseData(Warehouse w, int index) {
        this.warehouseIndex = index; // Index ta save kore rakha hocche pore update korar jonno

        // Form er ghor gulote ager data gulo bosiye (fill kore) dewa hocche
        tfWarehouseName.setText(w.getName());
        tfOwnerName.setText(w.getOwner());
        cbType.setValue(w.getType());
        cbRegion.setValue(w.getRegion());
        tfAddress.setText(w.getAddress());
        tfCapacity.setText(String.valueOf(w.getCapacity()));
        tfStock.setText(String.valueOf(w.getCurrentStock()));
        tfLicense.setText(w.getLicenseNo());
        tfContact.setText(w.getContact());
        btnColdStorage.setSelected(w.isHasColdStorage()); // Toggle button on/off kora

        // Date jodi faka na thake, tahole String theke LocalDate te convert kore DatePicker e set kora
        if (w.getExpiryDate() != null && !w.getExpiryDate().isEmpty()) {
            dpExpiry.setValue(LocalDate.parse(w.getExpiryDate()));
        }
    }

    // Update button e click korle ei method ta kaj korbe
    @FXML
    private void handleUpdate() {
        // Database (JSON) theke gudamer list load kora
        List<Warehouse> list = DataManager.loadWarehouses();

        // Form er ekhonkar (notun/edit kora) data gulo diye notun ekta Warehouse object banano hocche
        Warehouse updated = new Warehouse(
                tfWarehouseName.getText(), tfOwnerName.getText(), cbType.getValue(), cbRegion.getValue(),
                tfAddress.getText(), Double.parseDouble(tfCapacity.getText()), Double.parseDouble(tfStock.getText()),
                tfLicense.getText(), (dpExpiry.getValue() != null ? dpExpiry.getValue().toString() : ""),
                tfContact.getText(), btnColdStorage.isSelected()
        );

        // List er oi fixed index a old data tar bodole new update kora data ta set (replace) kora hocche
        list.set(warehouseIndex, updated);

        // Update kora list ta abar database/file e save kora
        DataManager.saveWarehouses(list);

        // Update complete hoise ta mark kore popup close kora
        updateClicked = true;
        closeStage();
    }

    // Main page theke check korar jonno je data asolei update hoise naki cancel kora hoise
    public boolean isUpdateClicked() { return updateClicked; }

    // Cancel button e click korle popup bondho hobe
    @FXML private void handleCancel() { closeStage(); }

    // Popup (Window/Stage) bondho korar main method
    private void closeStage() { ((Stage) tfWarehouseName.getScene().getWindow()).close(); }
}