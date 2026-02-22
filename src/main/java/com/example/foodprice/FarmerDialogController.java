package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class FarmerDialogController {

    @FXML private TextField tfName, tfPhone, tfNid, tfDistrict, tfUpazila, tfVillage, tfLand, tfAccount, tfCrops;
    @FXML private ComboBox<String> cbDivision, cbBanking;

    private Farmer newFarmer;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        if (cbDivision != null) {
            cbDivision.setItems(FXCollections.observableArrayList(
                    "ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"
            ));
            cbDivision.getSelectionModel().selectFirst();
        }
        if (cbBanking != null) {
            cbBanking.setItems(FXCollections.observableArrayList(
                    "বিকাশ", "নগদ", "রকেট", "ব্যাংক অ্যাকাউন্ট"
            ));
            cbBanking.getSelectionModel().selectFirst();
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        if (isInputValid()) {
            newFarmer = new Farmer(
                    tfName.getText().trim(),
                    "📞 " + tfPhone.getText().trim(),
                    tfNid.getText().trim(),
                    cbDivision.getValue(),
                    tfDistrict.getText().trim(),
                    tfUpazila.getText().trim(),
                    tfVillage.getText().trim(),
                    tfLand.getText().trim() + " একর",
                    cbBanking.getValue(),
                    tfAccount.getText().trim(),
                    tfCrops.getText().trim().isEmpty() ? "ধান" : tfCrops.getText().trim(),
                    "৳০",
                    50,
                    false
            );

            List<Farmer> farmers = DataManager.loadFarmers();
            farmers.add(newFarmer);
            DataManager.saveFarmers(farmers);

            saveClicked = true;
            closeStage();
        } else {
            new Alert(Alert.AlertType.ERROR, "দয়া করে নাম ও ফোন নম্বর পূরণ করুন।").show();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public Farmer getNewFarmer() {
        return newFarmer;
    }

    private boolean isInputValid() {
        return !tfName.getText().trim().isEmpty() && !tfPhone.getText().trim().isEmpty();
    }
}
