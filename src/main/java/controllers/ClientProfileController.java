package controllers;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.UserService;
import utils.Session;

import java.io.IOException;

public class ClientProfileController {

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label createdAtLabel;

    private final UserService userService = new UserService();
    private User currentUser;

    // Call this after loading the controller to inject the user
    public void initialize() {
        this.currentUser = Session.getInstance().getCurrentUser();
        if (this.currentUser != null) {
            // Set the values to the labels
            firstNameLabel.setText(this.currentUser.getFirstName());
            lastNameLabel.setText(this.currentUser.getLastName());
            usernameLabel.setText(this.currentUser.getUsername());
            emailLabel.setText(this.currentUser.getEmail());
            phoneLabel.setText(this.currentUser.getPhone());
            roleLabel.setText(this.currentUser.getRole());
            createdAtLabel.setText(this.currentUser.getCreatedAt().toString());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login-view.fxml")); // or wherever you want to go back
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-edit-profile-view.fxml"));
            Parent editRoot = loader.load();


            Stage stage = (Stage) firstNameLabel.getScene().getWindow(); // or any element
            stage.setScene(new Scene(editRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete your account?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (userService.deleteUser(this.currentUser.getId())) {
                    Session.getInstance().clear();
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setContentText("Your account has been deleted.");
                    success.showAndWait();

                    // Redirect to login
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
                        Parent loginRoot = loader.load();
                        Stage stage = (Stage) usernameLabel.getScene().getWindow(); // or any other element
                        stage.setScene(new Scene(loginRoot));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Error deleting account. Please try again.");
                    error.show();
                }
            }
        });
    }

}