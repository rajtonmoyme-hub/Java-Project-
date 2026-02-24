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

    @FXML private TextField tfName, tfEmail, tfPhone; // name, email, phone input
    @FXML private PasswordField pfPassword, pfConfirmPassword; // password + confirm
    @FXML private Label lblMessage; // success/error message

    @FXML
    void handleRegistration(ActionEvent event) {

        // user ja input dise nilam + extra space remove
        String name = tfName.getText().trim(), email = tfEmail.getText().trim(), phone = tfPhone.getText().trim();
        String pass = pfPassword.getText(), confirmPass = pfConfirmPassword.getText();

        // kono field faka hoile stop
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            showMessage("সবগুলো ফিল্ড পূরণ করা বাধ্যতামূলক!", false); return;
        }

        // password minimum 8 char
        if (pass.length() < 8) { showMessage("পাসওয়ার্ড কমপক্ষে ৮ অক্ষরের হতে হবে!", false); return; }

        // password match check
        if (!pass.equals(confirmPass)) { showMessage("পাসওয়ার্ড দুটি মিলছে না!", false); return; }

        // sob user database/file theke load
        List<User> users = DataManager.loadUsers();

        // same email diye already account ache kina
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                showMessage("এই ইমেইল দিয়ে আগেই একাউন্ট খোলা হয়েছে!", false); return;
            }
        }

        // new user add list e
        users.add(new User(name, email, phone, pass));

        // file e save kore dilam
        DataManager.saveUsers(users);

        // success message + form reset
        showMessage("রেজিস্ট্রেশন সফল! দয়া করে লগইন করুন।", true);
        clearFields();
    }

    private void showMessage(String msg, boolean success) {

        // message show + color change
        lblMessage.setText(msg);
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);

        // success green, error red
        lblMessage.setStyle(success ? "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;" : "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;");
    }

    private void clearFields() {

        // sob input box clean
        tfName.clear(); tfEmail.clear(); tfPhone.clear(); pfPassword.clear(); pfConfirmPassword.clear();
    }

    @FXML void goToUserLogin(ActionEvent event) {
        try {
            // login page load
            Parent root = FXMLLoader.load(getClass().getResource("/user_login.fxml"));

            // current window nilam
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            // scene na thakle new, naile replace
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1000, 700));
            } else {
                currentScene.setRoot(root);
            }

        } catch (IOException e) { e.printStackTrace(); }
    }
}