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

    @FXML private TextField tfEmail; // email/phone input
    @FXML private PasswordField pfPassword; // password input
    @FXML private Label lblError; // error message show

    @FXML
    void handleLogin(ActionEvent event) {

        // user input nilam + extra space remove
        String identity = tfEmail.getText().trim();
        String password = pfPassword.getText();

        // faka hole login try korbo na
        if (identity.isEmpty() || password.isEmpty()) {
            showError("ইমেইল/ফোন এবং পাসওয়ার্ড দিন"); return;
        }

        // sob registered user load
        List<User> users = DataManager.loadUsers();

        boolean validLogin = false; // dhore nilam login vul

        // ekta ekta user er sathe match kortesi
        for (User u : users) {

            // email OR phone match hole identity ok
            boolean identityMatch = (u.getEmail() != null && u.getEmail().equalsIgnoreCase(identity))
                    || (u.getPhone() != null && u.getPhone().equals(identity));

            // identity + password match hole login success
            if (identityMatch && u.getPassword() != null && u.getPassword().equals(password)) {
                validLogin = true;
                break; // peye gechi, ar check lagbe na
            }
        }

        if (validLogin) {

            // session e USER role set (mane ekhon app janbe user login)
            UserSession.login(UserSession.Role.USER, identity);

            // dashboard page e niye jai
            navigate(event, "/dashboard.fxml");

        } else {

            // vul hole error + password clear
            showError("ভুল তথ্য। ইমেইল/ফোন বা পাসওয়ার্ড সঠিক নয়");
            pfPassword.clear();
        }
    }

    private void showError(String msg) {

        // error label visible + red background
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
        lblError.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;");
    }

    // registration page e jawa
    @FXML void goToRegistration(ActionEvent event) { navigate(event, "/user_registration.fxml"); }

    // admin login e jawa
    @FXML void goToAdminLogin(ActionEvent event) { navigate(event, "/admin_login.fxml"); }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            // fxml file locate
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) { showError("পেজ পাওয়া যায়নি: " + fxmlPath); return; }

            Parent root = FXMLLoader.load(fxmlLocation);

            // current window nilam
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            // dashboard hole sidebar button highlight
            if ("/dashboard.fxml".equals(fxmlPath)) {
                SidebarController.activeButtonId = "btnDashboard";
                if (currentScene == null) stage.setScene(new Scene(root, 1000, 700));
                else currentScene.setRoot(root);
                return;
            }

            // normal navigation
            if (currentScene == null) stage.setScene(new Scene(root, 1000, 700));
            else currentScene.setRoot(root);

        } catch (Exception e) { e.printStackTrace(); }
    }
}