package controllers;

import entities.Notification;
import entities.User;
import services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.NotificationService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class AjouterNotificationController {

    @FXML
    private ComboBox<String> comboUser;

    @FXML
    private TextArea textMessage;

    @FXML
    private Button btnEnvoyer;

    @FXML
    private Hyperlink hyperlinkAfficher;

    private final NotificationService notificationService = new NotificationService();
    private final Map<String, Integer> userMap = new HashMap<>();
    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        chargerUtilisateursDansComboBox();

        hyperlinkAfficher.setOnAction(this::handleBackButton);
        btnEnvoyer.setOnAction(event -> ajouterNotification());
    }
    // Gestion du clic sur le bouton "Retour"
    private void handleBackButton(ActionEvent event) {
        // Fermer la fenêtre actuelle (ou revenir à la vue précédente)
        Stage stage = (Stage) hyperlinkAfficher.getScene().getWindow();
        stage.close(); // Ferme la fenêtre actuelle
    }
    private void chargerUtilisateursDansComboBox() {
            List<User> users = userService.getAllUsers();
            users.sort(Comparator.comparing(u -> u.getFirstName() + " " + u.getLastName())); // ✅ tri alphabétique

            ObservableList<String> fullNames = FXCollections.observableArrayList();

            for (User user : users) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                userMap.put(fullName, user.getId());
                fullNames.add(fullName);
            }
            comboUser.setItems(fullNames);
    }

    private void ajouterNotification() {
        String selectedName = comboUser.getValue();
        String message = textMessage.getText().trim();

        if (selectedName == null || message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs !");
            return;
        }

        Integer userId = userMap.get(selectedName);
        if (userId == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur sélectionné introuvable.");
            return;
        }

        Notification notif = new Notification(
                userId,
                message,
                false,
                new Timestamp(new Date().getTime())
        );

        try {
            notificationService.ajouter(notif);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Notification ajoutée avec succès !");
            comboUser.setValue(null);
            textMessage.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la notification.");
        }
    }

    private void afficherNotification() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherNotification.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) hyperlinkAfficher.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Liste des Notifications");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'affichage.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
