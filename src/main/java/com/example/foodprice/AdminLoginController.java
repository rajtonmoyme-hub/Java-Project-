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

public class AdminLoginController {

    @FXML private TextField tfEmail, tfCode;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblError;

    private static final String UNIQUE_CODE = "raj";

    @FXML
    void handleLogin(ActionEvent event) {
        String identity = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();
        String code = tfCode.getText().trim();

        lblError.setVisible(false);
        lblError.setText("");

        if (identity.isEmpty() || password.isEmpty() || code.isEmpty()) {
            showError("সব ফিল্ড পূরণ করুন"); return;
        }

        if (!authenticateAdmin(identity, password, code)) {
            showError("ইমেইল/ফোন, পাসওয়ার্ড অথবা ইউনিক কোড ভুল");
            pfPassword.clear(); return;
        }

        // --- SESSION SET FOR ADMIN ---
        UserSession.login(UserSession.Role.ADMIN, identity);

        navigate(event, "/dashboard.fxml");
    }

    private boolean authenticateAdmin(String identity, String password, String code) {
        if (!UNIQUE_CODE.equalsIgnoreCase(code)) return false;

        List<AdminUser> admins = DataManager.loadAdmins();
        for (AdminUser admin : admins) {
            boolean identityMatch = (admin.getEmail() != null && admin.getEmail().equalsIgnoreCase(identity))
                    || (admin.getPhone() != null && admin.getPhone().equals(identity));
            if (identityMatch && admin.getPassword() != null && admin.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void showError(String message) {
        lblError.setText(message); lblError.setVisible(true); lblError.setManaged(true);
    }

    @FXML void goToAdminRegistration(ActionEvent event) { navigate(event, "/admin_registration.fxml"); }
    @FXML void goToUserLogin(ActionEvent event) { navigate(event, "/user_login.fxml"); }

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