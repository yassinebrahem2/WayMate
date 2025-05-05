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

public class UserEditController {

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
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
        populateFields();
    }

    private void populateFields() {
        if (currentUser != null) {
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
        }
    }

    @FXML
    private void handleSave() {
        messageLabel.setText(""); // Clear previous message

        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        currentUser.setFirstName(firstNameField.getText());
        currentUser.setLastName(lastNameField.getText());
        currentUser.setUsername(usernameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setPhone(phoneField.getText());

        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        boolean updated = userService.updateUser(currentUser);
        messageLabel.setText(updated ? "Profile updated successfully!" : "Failed to update profile.");
        messageLabel.setTextFill(updated ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-profile-view.fxml"));
            Parent profileRoot = loader.load();

            // Optional: Pass back the current user
            UserProfileController controller = loader.getController();
            controller.setLoggedInUser(currentUser);

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(profileRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
