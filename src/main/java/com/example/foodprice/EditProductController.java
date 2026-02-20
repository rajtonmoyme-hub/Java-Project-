package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class EditProductController {
    @FXML private TextField tfNameEn, tfNameBn, tfCurrentPrice, tfPrevPrice, tfStock, tfImport, tfHoarderStock, tfHoarderPrice;
    @FXML private ComboBox<String> cbCategory, cbRegion, cbUnit, cbSource;
    @FXML private ToggleButton btnEssential;

    private int productIndex;
    private boolean updateClicked = false;

    @FXML
    public void initialize() {
        cbCategory.setItems(FXCollections.observableArrayList("চাল", "ডাল", "তেল", "চিনি", "সবজি", "আলু"));
        cbRegion.setItems(FXCollections.observableArrayList("জাতীয়", "ঢাকা", "চট্টগ্রাম", "রাজশাহী"));
        cbUnit.setItems(FXCollections.observableArrayList("কেজি", "লিটার", "বস্তা", "টন"));
        cbSource.setItems(FXCollections.observableArrayList("স্থানীয়", "আমদানি"));
    }

    // আগের ডাটা লোড করার মেথড
    public void setProductData(Product p, int index) {
        this.productIndex = index;
        tfNameEn.setText(p.getNameEn());
        tfNameBn.setText(p.getNameBn());
        cbCategory.setValue(p.getCategory());
        cbRegion.setValue(p.getRegion());
        tfCurrentPrice.setText(String.valueOf(p.getCurrentPrice()));
        tfPrevPrice.setText(String.valueOf(p.getPrevPrice()));
        tfStock.setText(String.valueOf(p.getStock()));
        cbUnit.setValue(p.getUnit());
        cbSource.setValue(p.getSource());
        tfImport.setText(String.valueOf(p.getImportAmount()));
        tfHoarderStock.setText(String.valueOf(p.getHoarderStock()));
        tfHoarderPrice.setText(String.valueOf(p.getHoarderPrice()));
        btnEssential.setSelected(p.isEssential());
    }

    @FXML
    private void handleUpdate() {
        List<Product> products = DataManager.loadProducts();

        // বর্তমান ইনডেক্সের অবজেক্ট আপডেট করা
        Product updatedProduct = new Product(
                tfNameEn.getText(), tfNameBn.getText(), cbCategory.getValue(), cbRegion.getValue(),
                Double.parseDouble(tfCurrentPrice.getText()), Double.parseDouble(tfPrevPrice.getText()),
                Double.parseDouble(tfStock.getText()), cbUnit.getValue(), cbSource.getValue(),
                Double.parseDouble(tfImport.getText()), Double.parseDouble(tfHoarderStock.getText()),
                Double.parseDouble(tfHoarderPrice.getText()), btnEssential.isSelected()
        );

        products.set(productIndex, updatedProduct); // লিস্ট আপডেট
        DataManager.saveProducts(products); // JSON ফাইলে সেভ

        updateClicked = true;
        closeStage();
    }

    public boolean isUpdateClicked() { return updateClicked; }
    @FXML private void handleCancel() { closeStage(); }
    private void closeStage() { ((Stage) tfNameEn.getScene().getWindow()).close(); }
}