package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.UserService;
import utils.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    @FXML
    private ImageView logoImage;


    @FXML
    private void handleLogin() {
        String phone = phoneField.getText();
        String password = passwordField.getText();


    @FXML
    private void handleLogin() {
        String username = phoneField.getText();
        String password = passwordField.getText();

        // Validation simple (à adapter selon vos besoins)
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Veuillez remplir tous les champs");
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