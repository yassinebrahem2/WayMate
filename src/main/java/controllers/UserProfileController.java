package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import entities.User;
import services.UserService;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

public class UserProfileController {

    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label roleLabel;
    @FXML private Label createdAtLabel;

    private User loggedInUser;
    private final UserService userService = new UserService();

    // Call this after loading the controller to inject the user
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        // Set the values to the labels
        firstNameLabel.setText(user.getFirstName());
        lastNameLabel.setText(user.getLastName());
        usernameLabel.setText(user.getUsername());
        emailLabel.setText(user.getEmail());
        phoneLabel.setText(user.getPhone());
        roleLabel.setText(user.getRole());
        createdAtLabel.setText(user.getCreatedAt().toString());
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
    private void handleModify() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-edit-view.fxml"));
            Parent editRoot = loader.load();

            // Pass the current user to the edit controller
            UserEditController editController = loader.getController();
            editController.setUser(loggedInUser); // assumes you stored `user` in this controller

            Stage stage = (Stage) firstNameLabel.getScene().getWindow(); // or any element
            stage.setScene(new Scene(editRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete your account?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (userService.deleteUser(this.loggedInUser.getId())) {
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
