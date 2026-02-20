package com.example.foodprice;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FarmerDialogController {

    @FXML private TextField tfName, tfPhone, tfNid, tfDistrict, tfUpazila, tfVillage, tfLand, tfAccount, tfCrops;
    @FXML private ComboBox<String> cbDivision, cbBanking;

    private Farmer newFarmer;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        // ড্রপডাউন সেটআপ
        if (cbDivision != null) {
            cbDivision.setItems(FXCollections.observableArrayList("ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ"));
        }
        if (cbBanking != null) {
            cbBanking.setItems(FXCollections.observableArrayList("বিকাশ", "নগদ", "রকেট", "ব্যাংক অ্যাকাউন্ট"));
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {
        if (isInputValid()) {
            // Farmer ক্লাসের নতুন কনস্ট্রাক্টর অনুযায়ী ঠিক ১৪টি ডাটা পাঠাতে হবে:
            newFarmer = new Farmer(
                    tfName.getText(),                    // 1. Name
                    "📞 " + tfPhone.getText(),           // 2. Phone
                    tfNid.getText(),                     // 3. NID
                    cbDivision.getValue(),               // 4. Division
                    tfDistrict.getText(),                // 5. District
                    tfUpazila.getText(),                 // 6. Upazila
                    tfVillage.getText(),                 // 7. Village
                    tfLand.getText() + " একর",           // 8. Land Amount
                    cbBanking.getValue(),                // 9. Banking Type
                    tfAccount.getText(),                 // 10. Account Number
                    tfCrops.getText().isEmpty() ? "ধান" : tfCrops.getText(), // 11. Crops
                    "৳০",                                // 12. Total Sales (Default)
                    50,                                  // 13. Score (Default)
                    false                                // 14. Verified (Default)
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
        // ভ্যালিডেশন: নাম এবং ফোন নম্বর অবশ্যই থাকতে হবে
        return !tfName.getText().isEmpty() && !tfPhone.getText().isEmpty();
    }
}