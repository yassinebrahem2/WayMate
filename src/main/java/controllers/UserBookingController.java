package controllers;

import entities.Booking;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.BookingService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class UserBookingController {
    @FXML
    private DatePicker StartdateField;

    @FXML
    private Button addbooking;

    @FXML
    private DatePicker endDateField;

    @FXML
    private TextField idUserField;

    @FXML
    private TextField licenseplateField;

    @FXML
    void handleAddBooking(ActionEvent event) {
        try {
            String userIdText = idUserField.getText().trim();
            String licenseplate =  licenseplateField.getText().trim();
            LocalDate dateDebut = StartdateField.getValue();
            LocalDate dateFin = endDateField.getValue();

            if (userIdText.isEmpty() || licenseplate.isEmpty() || dateDebut == null || dateFin == null) {
                showAlert(Alert.AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs requis.");
                return;
            }

            if (dateFin.isBefore(dateDebut)) {
                showAlert(Alert.AlertType.ERROR, "Dates invalides", "La date de fin doit être postérieure à la date de début.");
                return;
            }

            int userId = Integer.parseInt(userIdText);
            String status = "en_attente";

            // Création de l'objet réservation
            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setVehicleLicensePlate(licenseplate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDateStr = dateDebut.format(formatter);
            String endDateStr = dateFin.format(formatter);

            booking.setStartTime(startDateStr);
            booking.setEndTime(endDateStr);

            booking.setStatus(status);


            // Appel du service
            BookingService bookingService = new BookingService();
            bookingService.addBooking(booking);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès !");

            // Réinitialiser les champs
            idUserField.clear();
            licenseplateField.clear();
            StartdateField.setValue(LocalDate.now());
            endDateField.setValue(LocalDate.now().plus(1, ChronoUnit.DAYS));

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur ID", "L'ID utilisateur doit être un nombre.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite." + e.getMessage());
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
