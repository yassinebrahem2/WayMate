package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private ImageView lockIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Les images sont déjà définies dans le FXML avec les URL directes
        // Cependant, si vous préférez les charger par code :
        /*
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/bird_logo.png"));
            Image lock = new Image(getClass().getResourceAsStream("/images/lock_icon.png"));
            
            logoImage.setImage(logo);
            lockIcon.setImage(lock);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
        }
        */
    }

    @FXML
    private void handleLogin() {
        String username = phoneField.getText();
        String password = passwordField.getText();

        // Validation simple (à adapter selon vos besoins)
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Veuillez remplir tous les champs");
            errorMessage.setVisible(true);
            return;
        }

        // Logique de connexion ici
        System.out.println("Tentative de connexion avec: " + username);

        // Simulation d'une connexion réussie
        errorMessage.setVisible(false);
    }

    @FXML
    private void goToRegister() {
        // Logique pour aller à l'écran d'inscription
        System.out.println("Navigation vers l'écran d'inscription");
    }
}