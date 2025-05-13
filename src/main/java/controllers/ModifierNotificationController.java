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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierNotificationController {

    @FXML
    private TextArea textMessage;

    @FXML
    private ComboBox<String> comboUser;

    @FXML
    private Button btnSave;

    @FXML
    private Hyperlink hyperlinkAfficher;

    private Notification notification;

    private final NotificationService notificationService = new NotificationService();
    private final Map<String, Integer> userMap = new HashMap<>();
    private final UserService userService = new UserService();

    // Callback pour recharger la liste après modification
    private Runnable onModificationComplete;

    public void setOnModificationComplete(Runnable onModificationComplete) {
        this.onModificationComplete = onModificationComplete;
    }

    public void initialiserAvecNotification(Notification notif) {
        this.notification = notif;
        textMessage.setText(notif.getMessage());

        // Charger les utilisateurs dans la ComboBox
        chargerUtilisateursDansComboBox();


        User currentUser = userService.getUserById(notif.getUser_id());
        String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
        comboUser.setValue(fullName);


        hyperlinkAfficher.setOnAction(this::handleBackButton);
        btnSave.setOnAction(e -> modifierNotification());
    }
    // Gestion du clic sur le bouton "Retour"
    private void handleBackButton(ActionEvent event) {
        // Fermer la fenêtre actuelle (ou revenir à la vue précédente)
        Stage stage = (Stage) hyperlinkAfficher.getScene().getWindow();
        stage.close(); // Ferme la fenêtre actuelle
    }
    private void chargerUtilisateursDansComboBox() {
        List<User> users = userService.getAllUsers();
        users.sort(Comparator.comparing(u -> u.getFirstName() + " " + u.getLastName()));
        ObservableList<String> userNames = FXCollections.observableArrayList();
        for (User user : users) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            userMap.put(fullName, user.getId());
            userNames.add(fullName);
        }
        comboUser.setItems(userNames);
    }

    private void modifierNotification() {
        if (notification == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune notification sélectionnée.");
            return;
        }

        String message = textMessage.getText().trim();
        String selectedName = comboUser.getValue();

        if (message.isEmpty() || selectedName == null) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", "Veuillez remplir tous les champs.");
            return;
        }

        Integer userId = userMap.get(selectedName);
        if (userId == null) {
            showAlert(Alert.AlertType.ERROR, "Utilisateur invalide", "Utilisateur sélectionné introuvable.");
            return;
        }

        try {
            notification.setMessage(message);
            notification.setUser_id(userId);
            notificationService.modifier(notification);

            if (onModificationComplete != null) {
                onModificationComplete.run(); // Recharger la liste
            }

            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la modification.");
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
