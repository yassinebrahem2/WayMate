package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;


public class RegisterController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    private final UserService userService = new UserService();


    @FXML
    private void handleSignUp() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required!");
            messageLabel.setVisible(true);
            return;
        }


        if (password.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters long.");
            messageLabel.setVisible(true);
            return;
        }

        if (!isValidEmail(email)) {
            messageLabel.setText("Invalid email format.");
            messageLabel.setVisible(true);
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            messageLabel.setText("Invalid phone number format.");
            messageLabel.setVisible(true);
            return;
        }

        // You might want to add more robust username validation
        if (!isValidUsername(username)) {
            messageLabel.setText("Username must be alphanumeric and can include underscores or hyphens.");
            messageLabel.setVisible(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            messageLabel.setVisible(true);
            return;
        }

        User newUser = new User(firstName, lastName, username, email, phone, password, "client");
        userService.addUser(newUser);

        messageLabel.setText("Account created successfully!");
        messageLabel.setVisible(true);
        System.out.println(newUser);
    }

    // Helper methods for validation (you can refine these)
    private boolean isValidEmail(String email) {
        // A basic email validation using regex
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean isValidPhoneNumber(String phone) {
        // A basic phone number validation (you might need a more specific pattern)
        return phone.matches("^\\d{8,}$"); // Assuming at least 8 digits
    }

    private boolean isValidUsername(String username) {
        // Username should be alphanumeric and can include underscores and hyphens
        return username.matches("^[a-zA-Z0-9_-]+$");
    }

    @FXML
    private void handleReturnToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
