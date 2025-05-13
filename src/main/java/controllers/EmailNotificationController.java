package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import services.EmailService;

import javax.mail.MessagingException;
import java.sql.SQLException;
import java.util.List;

public class EmailNotificationController {
    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea messageField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button btnRetour;


    private EmailService emailService = new EmailService();
    private final ContextMenu suggestionsMenu = new ContextMenu();

    @FXML
    public void initialize() {
        recipientField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() < 2) {
                suggestionsMenu.hide();
                return;
            }

            Platform.runLater(() -> {
                try {
                    List<String> results = emailService.rechercherEmailsParMotCle(newText);
                    if (results.isEmpty()) {
                        suggestionsMenu.hide();
                        return;
                    }

                    suggestionsMenu.getItems().clear();
                    for (String email : results) {
                        MenuItem item = new MenuItem(email);
                        item.setOnAction(e -> {
                            recipientField.setText(email);
                            suggestionsMenu.hide();
                        });
                        suggestionsMenu.getItems().add(item);
                    }

                    if (!suggestionsMenu.isShowing()) {
                        suggestionsMenu.show(recipientField,
                                recipientField.localToScreen(0, recipientField.getHeight()).getX(),
                                recipientField.localToScreen(0, recipientField.getHeight()).getY());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });

        recipientField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                suggestionsMenu.hide();
            }
        });

        // Gestion du retour
        btnRetour.setOnAction(this::handleBackButton);
    }
    // Gestion du clic sur le bouton "Retour"
    private void handleBackButton(ActionEvent event) {
        // Fermer la fenêtre actuelle (ou revenir à la vue précédente)
        Stage stage = (Stage) btnRetour.getScene().getWindow();
        stage.close(); // Ferme la fenêtre actuelle
    }
    @FXML
    private void envoyerEmail() {
        String to = recipientField.getText();
        String subject = subjectField.getText();
        String message = messageField.getText();

        // Vérification des champs vides
        if (to.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        try {
            emailService.sendEmail(to, subject, message);

            // Affichage de la confirmation via une alerte
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Email envoyé avec succès !");
            alert.showAndWait();

            // Mise à jour du Label de statut
            statusLabel.setText("Email envoyé !");
            statusLabel.setTextFill(Color.GREEN);

            // Optionnel : Réinitialisation des champs après envoi
            recipientField.clear();
            subjectField.clear();
            messageField.clear();

        } catch (MessagingException e) {
            // Gestion des erreurs d'envoi
            e.printStackTrace();

            // Affichage d'une alerte d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'envoi");
            alert.setHeaderText("Impossible d'envoyer l'email");
            alert.setContentText("Détail : " + e.getMessage());
            alert.showAndWait();

            // Mise à jour du Label de statut en cas d'erreur
            statusLabel.setText("Échec de l'envoi.");
            statusLabel.setTextFill(Color.RED);
        }
    }
}
