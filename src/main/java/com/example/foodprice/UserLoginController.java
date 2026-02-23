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
import java.net.URL;
import java.util.List;

public class UserLoginController {

    @FXML private TextField tfEmail;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblError;

    @FXML
    void handleLogin(ActionEvent event) {
        String identity = tfEmail.getText().trim();
        String password = pfPassword.getText();

        if (identity.isEmpty() || password.isEmpty()) {
            showError("ইমেইল/ফোন এবং পাসওয়ার্ড দিন"); return;
        }

        List<User> users = DataManager.loadUsers();
        boolean validLogin = false;

        for (User u : users) {
            boolean identityMatch = (u.getEmail() != null && u.getEmail().equalsIgnoreCase(identity))
                    || (u.getPhone() != null && u.getPhone().equals(identity));
            if (identityMatch && u.getPassword() != null && u.getPassword().equals(password)) {
                validLogin = true;
                break;
            }
        }

        if (validLogin) {
            // --- SESSION SET FOR NORMAL USER ---
            UserSession.login(UserSession.Role.USER, identity);
            navigate(event, "/dashboard.fxml");
        } else {
            showError("ভুল তথ্য। ইমেইল/ফোন বা পাসওয়ার্ড সঠিক নয়");
            pfPassword.clear();
        }
    }

    private void showError(String msg) {
        lblError.setText(msg); lblError.setVisible(true); lblError.setManaged(true);
        lblError.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;");
    }

    @FXML void goToRegistration(ActionEvent event) { navigate(event, "/user_registration.fxml"); }
    @FXML void goToAdminLogin(ActionEvent event) { navigate(event, "/admin_login.fxml"); }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) { showError("পেজ পাওয়া যায়নি: " + fxmlPath); return; }
            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            if ("/dashboard.fxml".equals(fxmlPath)) {
                SidebarController.activeButtonId = "btnDashboard";
                if (currentScene == null) stage.setScene(new Scene(root, 1000, 700));
                else currentScene.setRoot(root);
                return;
            }

            if (currentScene == null) stage.setScene(new Scene(root, 1000, 700));
            else currentScene.setRoot(root);
        } catch (Exception e) { e.printStackTrace(); }
    }
}