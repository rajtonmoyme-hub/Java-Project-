package com.example.foodprice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminRegistrationController {

    @FXML private TextField tfName, tfEmail, tfPhone; // name, email, phone input box gula
    @FXML private PasswordField pfPassword, pfConfirmPassword; // password + confirm password
    @FXML private Label lblMessage; // success/error message show korbe

    @FXML
    void handleRegistration(ActionEvent event) {

        // user ja likhse sob niye nilam (extra space remove)
        String name = tfName.getText().trim(), email = tfEmail.getText().trim(), phone = tfPhone.getText().trim();
        String pass = pfPassword.getText(), confirmPass = pfConfirmPassword.getText();

        // kono field faka hoile agei block
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            showMessage("সবগুলো ফিল্ড পূরণ করা বাধ্যতামূলক!", false); return;
        }

        // password minimum 8 char lagbe
        if (pass.length() < 8) { showMessage("পাসওয়ার্ড কমপক্ষে ৮ অক্ষরের হতে হবে!", false); return; }

        // password 2 ta same kina check
        if (!pass.equals(confirmPass)) { showMessage("পাসওয়ার্ড দুটি মিলছে না!", false); return; }

        // database/file theke sob admin load
        List<AdminUser> users = DataManager.loadAdmins();

        // already ei email diye account ache kina check
        for (AdminUser user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                showMessage("এই ইমেইল দিয়ে আগেই একটি একাউন্ট খোলা হয়েছে!", false); return;
            }
        }

        // new admin add korlam list e
        users.add(new AdminUser(name, email, phone, pass));

        // abar file e save kore dilam
        DataManager.saveAdmins(users);

        // success message + form clear
        showMessage("রেজিস্ট্রেশন সফল হয়েছে! এখন লগইন করুন।", true);
        clearFields();
    }

    private void showMessage(String message, boolean isSuccess) {

        // message label e text set
        lblMessage.setText(message);
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);

        // success hole green, error hole red background
        lblMessage.setStyle(isSuccess ? "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;" : "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;");
    }

    private void clearFields() {

        // sob input box clear kore dilam
        tfName.clear(); tfEmail.clear(); tfPhone.clear(); pfPassword.clear(); pfConfirmPassword.clear();
    }

    @FXML
    void goToAdminLogin(ActionEvent event) {
        try {
            // login page load
            Parent root = FXMLLoader.load(getClass().getResource("/admin_login.fxml"));

            // current window ta nilam
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            // scene na thakle new banabo, naile root replace
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1000, 700));
            } else {
                currentScene.setRoot(root);
            }

        } catch (IOException e) { e.printStackTrace(); } // error hole console e print
    }
}