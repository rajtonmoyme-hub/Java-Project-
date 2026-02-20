package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class EditWarehouseController {
    @FXML private TextField tfWarehouseName, tfOwnerName, tfAddress, tfCapacity, tfStock, tfLicense, tfContact;
    @FXML private ComboBox<String> cbType, cbRegion;
    @FXML private DatePicker dpExpiry;
    @FXML private ToggleButton btnColdStorage;

    private int warehouseIndex;
    private boolean updateClicked = false;

    @FXML
    public void initialize() {
        cbType.setItems(FXCollections.observableArrayList("সরকারি", "বেসরকারি", "ব্যবসায়ী", "সমবায়"));
        cbRegion.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "খুলনা", "রাজশাহী", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ"));
    }

    public void setWarehouseData(Warehouse w, int index) {
        this.warehouseIndex = index;
        tfWarehouseName.setText(w.getName());
        tfOwnerName.setText(w.getOwner());
        cbType.setValue(w.getType());
        cbRegion.setValue(w.getRegion());
        tfAddress.setText(w.getAddress());
        tfCapacity.setText(String.valueOf(w.getCapacity()));
        tfStock.setText(String.valueOf(w.getCurrentStock()));
        tfLicense.setText(w.getLicenseNo());
        tfContact.setText(w.getContact());
        btnColdStorage.setSelected(w.isHasColdStorage());
        if (w.getExpiryDate() != null && !w.getExpiryDate().isEmpty()) {
            dpExpiry.setValue(LocalDate.parse(w.getExpiryDate()));
        }
    }

    @FXML
    private void handleUpdate() {
        List<Warehouse> list = DataManager.loadWarehouses();
        Warehouse updated = new Warehouse(
                tfWarehouseName.getText(), tfOwnerName.getText(), cbType.getValue(), cbRegion.getValue(),
                tfAddress.getText(), Double.parseDouble(tfCapacity.getText()), Double.parseDouble(tfStock.getText()),
                tfLicense.getText(), (dpExpiry.getValue() != null ? dpExpiry.getValue().toString() : ""),
                tfContact.getText(), btnColdStorage.isSelected()
        );

        list.set(warehouseIndex, updated);
        DataManager.saveWarehouses(list);
        updateClicked = true;
        closeStage();
    }

    public boolean isUpdateClicked() { return updateClicked; }
    @FXML private void handleCancel() { closeStage(); }
    private void closeStage() { ((Stage) tfWarehouseName.getScene().getWindow()).close(); }
}