package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import entities.User;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

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

    // Call this after loading the controller to inject the user
    public void setUser(User user) {
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


    public void setLoggedInUser(User user) {
    }
}
