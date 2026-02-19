package com.example.foodprice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminLoginController {

    @FXML private TextField tfEmail;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfCode;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    // Hardcoded credentials (production এ অবশ্যই ডাটাবেস + hashing ব্যবহার করতে হবে)
    private static final String ADMIN_EMAIL    = "abc@gmail.com";
    private static final String ADMIN_PASSWORD = "1234";
    private static final String UNIQUE_CODE    = "raj";

    @FXML
    void handleLogin(ActionEvent event) {
        String email    = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();
        String code     = tfCode.getText().trim();

        // প্রথমে এরর লুকিয়ে রাখি
        lblError.setVisible(false);
        lblError.setText("");

        // সব ফিল্ড চেক
        if (email.isEmpty() || password.isEmpty() || code.isEmpty()) {
            showError("সব ফিল্ড পূরণ করুন");
            return;
        }

        // ইমেইল চেক
        if (!email.equals(ADMIN_EMAIL)) {
            showError("ভুল ইমেইল");
            tfEmail.requestFocus();
            return;
        }

        // পাসওয়ার্ড চেক
        if (!password.equals(ADMIN_PASSWORD)) {
            showError("ভুল পাসওয়ার্ড");
            pfPassword.requestFocus();
            pfPassword.clear();  // ভুল হলে পাসওয়ার্ড ক্লিয়ার করে দিচ্ছে
            return;
        }

        // ইউনিক কোড চেক
        if (!code.equals(UNIQUE_CODE)) {
            showError("ভুল ইউনিক কোড");
            tfCode.requestFocus();
            tfCode.clear();
            return;
        }

        // সব ঠিক থাকলে → Dashboard খোলা
        try {
            // তোমার ইতিমধ্যে তৈরি করা dashboard.fxml লোড করা হচ্ছে
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent root = loader.load();

            // নতুন সিন তৈরি + সাইজ সেট (তোমার ইচ্ছামতো চেঞ্জ করতে পারো)
            Scene scene = new Scene(root, 1400, 900);

            // CSS যোগ করা (যদি dashboard এর জন্য আলাদা CSS থাকে)
            // scene.getStylesheets().add(getClass().getResource("/dashboard.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard - FoodPrice");
            stage.centerOnScreen();
            stage.isResizable();


        } catch (IOException e) {
            e.printStackTrace();
            showError("Dashboard লোড করতে সমস্যা হয়েছে। ফাইল চেক করুন।");
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}