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

    // input field gula (email/phone, password, unique code)
    @FXML private TextField tfEmail, tfCode;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblError; // error message dekhano r jonno label

    // hardcoded admin secret code
    private static final String UNIQUE_CODE = "raj";

    @FXML
    void handleLogin(ActionEvent event) {

        // user ja likhse segula niye trim kore nilam (space thakle remove)
        String identity = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();
        String code = tfCode.getText().trim();

        // purono error hide kore reset
        lblError.setVisible(false);
        lblError.setText("");

        // kono field empty hoile login try i korbo na
        if (identity.isEmpty() || password.isEmpty() || code.isEmpty()) {
            showError("সব ফিল্ড পূরণ করুন"); return;
        }

        // main authentication check
        if (!authenticateAdmin(identity, password, code)) {
            showError("ইমেইল/ফোন, পাসওয়ার্ড অথবা ইউনিক কোড ভুল");
            pfPassword.clear(); // vul hoile password clear korbe
            return;
        }

        // login success -> session e admin role set
        UserSession.login(UserSession.Role.ADMIN, identity);

        // dashboard page e niye jabe
        navigate(event, "/dashboard.fxml");
    }

    private boolean authenticateAdmin(String identity, String password, String code) {

        // unique code age check (eta fail hoile database check e jabo na)
        if (!UNIQUE_CODE.equalsIgnoreCase(code)) return false;

        // file/json theke sob admin load
        List<AdminUser> admins = DataManager.loadAdmins();

        // ekta ekta kore admin match khujtesi
        for (AdminUser admin : admins) {

            // email OR phone match hoile identity ok dhora
            boolean identityMatch = (admin.getEmail() != null && admin.getEmail().equalsIgnoreCase(identity))
                    || (admin.getPhone() != null && admin.getPhone().equals(identity));

            // identity + password match = real admin
            if (identityMatch && admin.getPassword() != null && admin.getPassword().equals(password)) {
                return true;
            }
        }
        return false; // keu match kore nai
    }

    private void showError(String message) {
        // error label visible kore message show
        lblError.setText(message);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    // admin registration page e jawa button
    @FXML void goToAdminRegistration(ActionEvent event) { navigate(event, "/admin_registration.fxml"); }

    // normal user login page e jawa button
    @FXML void goToUserLogin(ActionEvent event) { navigate(event, "/user_login.fxml"); }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            // fxml file locate kori
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) { showError("পেজ পাওয়া যায়নি: " + fxmlPath); return; }

            Parent root = FXMLLoader.load(fxmlLocation);

            // current window (stage) ber kori
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            // dashboard e gele sidebar er active button set korte hobe
            if ("/dashboard.fxml".equals(fxmlPath)) {
                SidebarController.activeButtonId = "btnDashboard";

                // scene na thakle new banabo, thakle root replace
                if (currentScene == null) stage.setScene(new Scene(root, 1000, 700));
                else currentScene.setRoot(root);
                return;
            }

            // onno sob page er jonno same logic
            if (currentScene == null) stage.setScene(new Scene(root, 1000, 700));
            else currentScene.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace(); // debug er jonno console e error print
        }
    }
}
