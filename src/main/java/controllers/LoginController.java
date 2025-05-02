package controllers;

import entities.User;
import services.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.*;


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

        } else if (!user.getPassword().equals(password)){
            errorMessage.setText("Wrong Password");
            errorMessage.setVisible(true);

        } else {
            errorMessage.setText("Login Successful");
            errorMessage.setVisible(true);
            // Load the next scene or dashboard here
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
