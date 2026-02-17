package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
        cbDivision.setItems(FXCollections.observableArrayList(
                "ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা",
                "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"
        ));
        cbBanking.setItems(FXCollections.observableArrayList("বিকাশ", "নগদ", "রকেট", "ব্যাংক"));
    }

    @FXML
    void handleRegister(ActionEvent event) {
        if (isInputValid()) {
            String location = buildLocation();
            String landStr = "জমি: " + tfLand.getText().trim() + " একর";

            newFarmer = new Farmer(
                    tfName.getText().trim(),
                    "📞 " + tfPhone.getText().trim(),
                    location.trim(),
                    landStr,
                    tfCrops.getText().trim().isEmpty() ? "ধান" : tfCrops.getText().trim(),
                    "৳0",           // default sales
                    50,             // default score
                    false           // not verified
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

    /**
     * Validates all input fields and shows an error alert if something is wrong.
     * @return true if all inputs are valid, false otherwise
     */
    private boolean isInputValid() {
        StringBuilder errorMsg = new StringBuilder();

        // 1. Name - required, at least 2 characters, allow Bangla + English + space
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            errorMsg.append("• নাম দিন (Name is required)\n");
        } else if (name.length() < 2) {
            errorMsg.append("• নাম খুব ছোট (Name too short)\n");
        }

        // 2. Phone - required, Bangladesh mobile format (01xxxxxxxxx - 11 digits)
        String phone = tfPhone.getText().trim();
        if (phone.isEmpty()) {
            errorMsg.append("• ফোন নম্বর দিন (Phone number is required)\n");
        } else if (!phone.matches("^01[3-9]\\d{8}$")) {
            errorMsg.append("• সঠিক ফোন নম্বর দিন (যেমন: 01712345678)\n");
        }

        // 3. Land area - required, positive number (integer or decimal)
        String landText = tfLand.getText().trim();
        if (landText.isEmpty()) {
            errorMsg.append("• জমির পরিমাণ দিন (Land area is required)\n");
        } else {
            try {
                double land = Double.parseDouble(landText);
                if (land <= 0) {
                    errorMsg.append("• জমির পরিমাণ ০-এর বেশি হতে হবে (Land area must be positive)\n");
                }
            } catch (NumberFormatException e) {
                errorMsg.append("• জমির পরিমাণ সংখ্যা হতে হবে (Land area must be a number)\n");
            }
        }

        // 4. Optional fields - just trim and warn if suspicious (but allow save)
        String district = tfDistrict.getText().trim();
        String upazila = tfUpazila.getText().trim();
        String village = tfVillage.getText().trim();

        if (district.isEmpty() && upazila.isEmpty() && village.isEmpty()) {
            errorMsg.append("• অন্তত একটি লোকেশন ফিল্ড পূরণ করুন (At least one location field should be filled)\n");
        }

        // Crops - optional, but if filled, should not be just spaces
        String crops = tfCrops.getText().trim();
        if (!crops.isEmpty() && crops.length() < 2) {
            errorMsg.append("• ফসলের নাম সঠিকভাবে লিখুন (Crop name too short or invalid)\n");
        }

        // Show alert if there are errors
        if (errorMsg.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ইনপুট ত্রুটি");
            alert.setHeaderText("কিছু তথ্য সঠিক নয়");
            alert.setContentText(errorMsg.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    /**
     * Builds location string from village, upazila, district.
     * Handles missing parts gracefully.
     */
    private String buildLocation() {
        StringBuilder sb = new StringBuilder();

        String village = tfVillage.getText().trim();
        String upazila = tfUpazila.getText().trim();
        String district = tfDistrict.getText().trim();

        if (!village.isEmpty()) sb.append(village);
        if (!upazila.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(upazila);
        }
        if (!district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }

        return sb.toString().trim();
    }
}