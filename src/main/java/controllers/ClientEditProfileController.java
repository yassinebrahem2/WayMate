package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.UserService;
import utils.Session;

import java.io.IOException;

public class ClientEditProfileController {

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

    public void initialize() {
        this.currentUser = Session.getInstance().getCurrentUser();
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

        String newFirstName = firstNameField.getText().trim();
        String newLastName = lastNameField.getText().trim();
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic checks for non-empty fields (you might decide which are strictly required)
        if (newFirstName.isEmpty() || newLastName.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            messageLabel.setText("All fields (except password) are required!");
            messageLabel.setVisible(true);
            return;
        }

        // Password confirmation check (only if a new password is provided)
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setVisible(true);
            return;
        }

        // Password length check (only if a new password is provided)
        if (!newPassword.isEmpty() && newPassword.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters long.");
            messageLabel.setVisible(true);
            return;
        }

        // Email validation
        if (!isValidEmail(newEmail)) {
            messageLabel.setText("Invalid email format.");
            messageLabel.setVisible(true);
            return;
        }

//        // Phone number validation (Tunisian context - adjust regex if needed)
//        if (!isValidTunisianPhoneNumber(newPhone)) {
//            messageLabel.setText("Invalid Tunisian phone number format (e.g., 9xxxxxxxx).");
//            messageLabel.setVisible(true);
//            return;
//        }

        // Username validation
        if (!isValidUsername(newUsername)) {
            messageLabel.setText("Username must be alphanumeric and can include underscores or hyphens.");
            messageLabel.setVisible(true);
            return;
        }

        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setPhone(newPhone);

        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        boolean updated = userService.updateUser(currentUser);
        messageLabel.setText(updated ? "Profile updated successfully!" : "Failed to update profile.");
        messageLabel.setTextFill(updated ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
        messageLabel.setVisible(true);
    }

    // Helper methods (reusing and adapting from registration)
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean isValidTunisianPhoneNumber(String phone) {
        // Example for Tunisian phone numbers starting with 9, followed by 7 digits
        return phone.matches("^9\\d{7}$");
        // You might need to adjust this regex based on other possible formats
    }

    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_-]+$");
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-profile-view.fxml"));
            Parent profileRoot = loader.load();

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(profileRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleProfileButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-profile-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleVehiclesButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-vehicle-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleReviewsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-reviews-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
