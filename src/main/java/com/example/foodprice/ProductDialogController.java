package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class ProductDialogController {
    @FXML private TextField tfNameEn, tfNameBn, tfCurrentPrice, tfPrevPrice, tfStock, tfImport, tfHoarderStock, tfHoarderPrice;
    @FXML private ComboBox<String> cbCategory, cbRegion, cbUnit, cbSource;
    @FXML private ToggleButton btnEssential;

    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        cbCategory.setItems(FXCollections.observableArrayList("চাল", "ডাল", "তেল", "চিনি", "সবজি", "মাংস"));
        cbRegion.setItems(FXCollections.observableArrayList("জাতীয়", "ঢাকা", "চট্টগ্রাম", "রাজশাহী"));
        cbUnit.setItems(FXCollections.observableArrayList("কেজি", "লিটার", "বস্তা", "টন"));
        cbSource.setItems(FXCollections.observableArrayList("স্থানীয়", "আমদানি"));

        cbCategory.setValue("চাল");
        cbRegion.setValue("জাতীয়");
        cbUnit.setValue("কেজি");
        cbSource.setValue("স্থানীয়");
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            Product newProduct = new Product(
                    tfNameEn.getText(), tfNameBn.getText(), cbCategory.getValue(), cbRegion.getValue(),
                    Double.parseDouble(tfCurrentPrice.getText()), parseSafe(tfPrevPrice.getText()),
                    Double.parseDouble(tfStock.getText()), cbUnit.getValue(), cbSource.getValue(),
                    parseSafe(tfImport.getText()), parseSafe(tfHoarderStock.getText()),
                    parseSafe(tfHoarderPrice.getText()), btnEssential != null && btnEssential.isSelected()
            );

            List<Product> products = DataManager.loadProducts();
            products.add( newProduct);
            DataManager.saveProducts(products);

            saveClicked = true;
            closeStage();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "দয়া করে নাম, দাম এবং মজুদ পূরণ করুন!");
            alert.show();
        }
    }

    private boolean isInputValid() {
        return !tfNameEn.getText().isEmpty() && !tfNameBn.getText().isEmpty() &&
                !tfCurrentPrice.getText().isEmpty() && !tfStock.getText().isEmpty();
    }

    private double parseSafe(String text) {
        return (text == null || text.isEmpty()) ? 0.0 : Double.parseDouble(text);
    }

    @FXML private void handleCancel() { closeStage(); }
    private void closeStage() { ((Stage) tfNameEn.getScene().getWindow()).close(); }
}