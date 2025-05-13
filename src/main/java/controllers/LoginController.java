package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.UserService;
import utils.Session;

import java.io.IOException;


public class LoginController {

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    private final UserService userService = new UserService();


    @FXML
    private void handleLogin() {
        String phone = phoneField.getText();
        String password = passwordField.getText();

        User user = userService.findUserByPhone(phone);

        if (user == null) {
            errorMessage.setText("User Not Found");
            errorMessage.setVisible(true);
        } else if (!user.getPassword().equals(password)) {
            errorMessage.setText("Wrong Password");
            errorMessage.setVisible(true);
        } else {
            Session.getInstance().setCurrentUser(user);
            errorMessage.setVisible(false);
            try {
                FXMLLoader loader;
                if ("admin".equals(user.getRole())) {
                    loader = new FXMLLoader(getClass().getResource("/admin-view.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/user-profile-view.fxml"));
                }

                Parent root = loader.load();

                // Optionally, pass the logged-in user to the controller
                if ("admin".equals(user.getRole())) {
                    AdminController adminController = loader.getController();

                } else {
                    UserProfileController userController = loader.getController();
                    // Optionally do something with the controller
                }

                Stage stage = (Stage) phoneField.getScene().getWindow();

                // Set scene with dimensions 1400x700

                stage.setScene(new Scene(root));
                stage.setResizable(false); // Optional: lock resizing
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register-view.fxml"));
            Parent registerRoot = loader.load();
            Stage stage = (Stage) phoneField.getScene().getWindow();

            stage.setScene(new Scene(registerRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
