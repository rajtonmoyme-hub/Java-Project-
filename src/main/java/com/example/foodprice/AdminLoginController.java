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

public class AdminLoginController {

    @FXML private TextField tfEmail;
    @FXML private TextField tfCode;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblError;

    // Only unique code is hardcoded as requested.
    private static final String UNIQUE_CODE = "raj";

    @FXML
    void handleLogin(ActionEvent event) {
        String email = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();
        String code = tfCode.getText().trim();

        lblError.setVisible(false);
        lblError.setText("");

        if (email.isEmpty() || password.isEmpty() || code.isEmpty()) {
            showError("সব ফিল্ড পূরণ করুন");
            return;
        }

        if (!authenticateAdmin(email, password, code)) {
            showError("ইমেইল, পাসওয়ার্ড অথবা ইউনিক কোড ভুল");
            pfPassword.clear();
            return;
        }

        navigate(event, "/dashboard.fxml");
    }

    private boolean authenticateAdmin(String email, String password, String code) {
        // Unique code must match the hardcoded value.
        if (!UNIQUE_CODE.equalsIgnoreCase(code)) {
            return false;
        }

        // Email and password must come from registration data (admins.json).
        List<AdminUser> admins = DataManager.loadAdmins();
        for (AdminUser admin : admins) {
            if (admin.getEmail() != null
                    && admin.getPassword() != null
                    && admin.getEmail().equalsIgnoreCase(email)
                    && admin.getPassword().equals(password)) {
                return true;
            }
        }

        return false;
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    @FXML
    void goToAdminRegistration(ActionEvent event) {
        navigate(event, "/admin_registration.fxml");
    }

    @FXML
    void goToUserLogin(ActionEvent event) {
        navigate(event, "/user_login.fxml");
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            if (currentScene == null) {
                stage.setScene(new Scene(root, 1000, 700));
            } else {
                currentScene.setRoot(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}