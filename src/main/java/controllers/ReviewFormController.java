package controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import entities.Review;
import services.ReviewService;

import java.sql.SQLException;
import javafx.scene.control.Alert;

public class ReviewFormController {
    private final ReviewService reviewService = new ReviewService();

    @FXML
    private Button ajouterButton;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private TextField createdAtField;
    @FXML
    private TextField idField;

    @FXML
    private Button modifierButton;

    @FXML
    private TextField ratingField;

    @FXML
    private Button supprimerButton;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField vehiclePlateField;

    @FXML
    void handleAjouter(ActionEvent event) {
        try {
            String userId = userIdField.getText();
            String vehiclePlate = vehiclePlateField.getText();
            int rating = Integer.parseInt(ratingField.getText());
            String comment = commentTextArea.getText();
            String createdAt = createdAtField.getText();

            Review review = new Review(0, userId, vehiclePlate, rating, comment, createdAt);
            reviewService.ajouter(review);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Avis ajouté avec succès !");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le rating doit être un entier.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }

    }

    @FXML
    void handleModifier(ActionEvent event) {
        try {
            int id = Integer.parseInt(idField.getText());
            String userId = userIdField.getText();
            String vehiclePlate = vehiclePlateField.getText();
            int rating = Integer.parseInt(ratingField.getText());
            String comment = commentTextArea.getText();
            String createdAt = createdAtField.getText();

            Review review = new Review(id, userId, vehiclePlate, rating, comment, createdAt);
            reviewService.modifier(review);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Avis modifié avec succès !");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "ID et Rating doivent être des entiers.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }

    }

    @FXML
    void handleSupprimer(ActionEvent event) {
        try {
            int id = Integer.parseInt(idField.getText());
            reviewService.supprimer(id);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Avis supprimé avec succès !");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "L'ID doit être un entier.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }

    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        userIdField.clear();
        vehiclePlateField.clear();
        ratingField.clear();
        commentTextArea.clear();
        createdAtField.clear();
    }


}
