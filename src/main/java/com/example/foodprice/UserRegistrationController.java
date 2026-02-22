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

public class UserRegistrationController {

    @FXML private TextField tfName, tfEmail, tfPhone;
    @FXML private PasswordField pfPassword, pfConfirmPassword;
    @FXML private Label lblMessage;

    @FXML
    void handleRegistration(ActionEvent event) {
        String name = tfName.getText().trim(), email = tfEmail.getText().trim(), phone = tfPhone.getText().trim();
        String pass = pfPassword.getText(), confirmPass = pfConfirmPassword.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            showMessage("সবগুলো ফিল্ড পূরণ করা বাধ্যতামূলক!", false); return;
        }
        if (pass.length() < 8) { showMessage("পাসওয়ার্ড কমপক্ষে ৮ অক্ষরের হতে হবে!", false); return; }
        if (!pass.equals(confirmPass)) { showMessage("পাসওয়ার্ড দুটি মিলছে না!", false); return; }

        List<User> users = DataManager.loadUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                showMessage("এই ইমেইল দিয়ে আগেই একাউন্ট খোলা হয়েছে!", false); return;
            }
        }

        users.add(new User(name, email, phone, pass));
        DataManager.saveUsers(users);

        showMessage("রেজিস্ট্রেশন সফল! দয়া করে লগইন করুন।", true);
        clearFields();
    }

    private void showMessage(String msg, boolean success) {
        lblMessage.setText(msg); lblMessage.setVisible(true); lblMessage.setManaged(true);
        lblMessage.setStyle(success ? "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;" : "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;");
    }

    private void clearFields() {
        tfName.clear(); tfEmail.clear(); tfPhone.clear(); pfPassword.clear(); pfConfirmPassword.clear();
    }

    @FXML void goToUserLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/user_login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1000, 700));
            } else {
                currentScene.setRoot(root);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
