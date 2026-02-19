package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class ProductDialogController {
    @FXML private TextField tfNameEn, tfNameBn, tfCurrentPrice, tfPrevPrice, tfStock, tfImport, tfHoarderStock, tfHoarderPrice;
    @FXML private ComboBox<String> cbCategory, cbRegion, cbUnit, cbSource;
    @FXML private CheckBox checkEssential;

    @FXML
    public void initialize() {
        cbCategory.setItems(FXCollections.observableArrayList("চাল", "ডাল", "তেল", "চিনি", "সবজি"));
        cbRegion.setItems(FXCollections.observableArrayList("জাতীয়", "ঢাকা", "চট্টগ্রাম", "রাজশাহী"));
        cbUnit.setItems(FXCollections.observableArrayList("কেজি", "লিটার", "বস্তা", "টন"));
        cbSource.setItems(FXCollections.observableArrayList("স্থানীয়", "আমদানি"));
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            Product newProduct = new Product(
                    tfNameEn.getText(), tfNameBn.getText(), cbCategory.getValue(), cbRegion.getValue(),
                    Double.parseDouble(tfCurrentPrice.getText()), Double.parseDouble(tfPrevPrice.getText()),
                    Double.parseDouble(tfStock.getText()), cbUnit.getValue(), cbSource.getValue(),
                    Double.parseDouble(tfImport.getText().isEmpty() ? "0" : tfImport.getText()),
                    Double.parseDouble(tfHoarderStock.getText().isEmpty() ? "0" : tfHoarderStock.getText()),
                    Double.parseDouble(tfHoarderPrice.getText().isEmpty() ? "0" : tfHoarderPrice.getText()),
                    checkEssential.isSelected()
            );

            List<Product> products = DataManager.loadProducts();
            products.add(newProduct);
            DataManager.saveProducts(products);

            closeStage();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "দয়া করে প্রয়োজনীয় সব তথ্য পূরণ করুন!");
            alert.show();
        }
    }

    private boolean validateInput() {
        return !tfNameEn.getText().isEmpty() && !tfNameBn.getText().isEmpty() &&
                cbCategory.getValue() != null && tfCurrentPrice.getText().matches("\\d*(\\.\\d+)?");
    }

    @FXML private void handleCancel() { closeStage(); }
    private void closeStage() { ((Stage) tfNameEn.getScene().getWindow()).close(); }
}