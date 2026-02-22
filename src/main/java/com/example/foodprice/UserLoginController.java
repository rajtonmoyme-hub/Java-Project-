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

public class UserLoginController {

    @FXML private TextField tfEmail;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblError;

    @FXML
    void handleLogin(ActionEvent event) {
        String email = tfEmail.getText().trim();
        String password = pfPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("ইমেইল এবং পাসওয়ার্ড দিন!");
            return;
        }

        List<User> users = DataManager.loadUsers();
        boolean validLogin = false;

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                validLogin = true;
                break;
            }
        }

        if (validLogin) {
            navigate(event, "/dashboard.fxml");
        } else {
            showError("ভুল ইমেইল অথবা পাসওয়ার্ড!");
            pfPassword.clear();
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
        lblError.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;");
    }

    // Navigation Methods
    @FXML void goToRegistration(ActionEvent event) { navigate(event, "/user_registration.fxml"); }
    @FXML void goToAdminLogin(ActionEvent event) { navigate(event, "/admin_login.fxml"); }

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
        } catch (IOException e) { e.printStackTrace(); }
    }
}
