package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FarmerDialogController {

    @FXML private TextField tfName;
    @FXML private TextField tfPhone;
    @FXML private ComboBox<String> cbDivision;
    @FXML private TextField tfDistrict;
    @FXML private TextField tfUpazila;
    @FXML private TextField tfVillage;
    @FXML private TextField tfLand;
    @FXML private ComboBox<String> cbBanking;
    @FXML private TextField tfCrops;

    private Farmer newFarmer;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        cbDivision.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"));
        cbBanking.setItems(FXCollections.observableArrayList("বিকাশ", "নগদ", "রকেট", "ব্যাংক"));
    }

    @FXML
    void handleRegister(ActionEvent event) {
        if (isInputValid()) {
            String location = tfVillage.getText() + ", " + tfUpazila.getText() + ", " + tfDistrict.getText();

            // Create new Farmer object
            newFarmer = new Farmer(
                    tfName.getText(),
                    "📞 " + tfPhone.getText(),
                    location,
                    "জমি: " + tfLand.getText() + " একর",
                    tfCrops.getText().isEmpty() ? "ধান" : tfCrops.getText(),
                    "৳0", // Default sales for new farmer
                    50,   // Default score
                    false // Not verified yet
            );

            saveClicked = true;
            closeStage();
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
        // Simple validation
        return !tfName.getText().isEmpty() && !tfPhone.getText().isEmpty();
    }
}