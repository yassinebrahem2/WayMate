package controllers;

import entities.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import services.BookingService;
import javafx.util.Callback;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AdminEditPopUpController {

    @FXML
    private Label bookingIdLabel;

    @FXML
    private Label vehicleLicensePlateLabel;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> startHourCombo;

    @FXML
    private ComboBox<String> startMinuteCombo;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> endHourCombo;

    @FXML
    private ComboBox<String> endMinuteCombo;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Booking booking;
    private BookingService bookingService;
    private Callback<Booking, Void> onSaveCallback;

    @FXML
    public void initialize() {
        bookingService = new BookingService();

        // Initialiser les combobox d'heures et minutes
        for (int i = 0; i < 24; i++) {
            startHourCombo.getItems().add(String.format("%02d", i));
            endHourCombo.getItems().add(String.format("%02d", i));
        }

        for (int i = 0; i < 60; i += 15) {
            startMinuteCombo.getItems().add(String.format("%02d", i));
            endMinuteCombo.getItems().add(String.format("%02d", i));
        }

        // Initialiser la combobox de statut
        statusComboBox.getItems().addAll("confirmée", "en_cours", "terminée", "annulée");

        // Configurer les actions des boutons
        saveButton.setOnAction(event -> saveBooking());
        cancelButton.setOnAction(event -> closeWindow());
    }

    public void setBooking(Booking booking) {
        this.booking = booking;

        // Afficher les informations de la réservation
        bookingIdLabel.setText(String.valueOf(booking.getId()));
        vehicleLicensePlateLabel.setText(booking.getVehicleLicensePlate());

        // Configurer les dates et heures
        LocalDateTime startDateTime = booking.getStartTime();
        LocalDateTime endDateTime = booking.getEndTime();

        startDatePicker.setValue(startDateTime.toLocalDate());
        startHourCombo.setValue(String.format("%02d", startDateTime.getHour()));
        startMinuteCombo.setValue(String.format("%02d", startDateTime.getMinute() - startDateTime.getMinute() % 15));

        endDatePicker.setValue(endDateTime.toLocalDate());
        endHourCombo.setValue(String.format("%02d", endDateTime.getHour()));
        endMinuteCombo.setValue(String.format("%02d", endDateTime.getMinute() - endDateTime.getMinute() % 15));

        // Configurer le statut
        statusComboBox.setValue(booking.getStatus());
    }

    public void setOnSaveCallback(Callback<Booking, Void> callback) {
        this.onSaveCallback = callback;
    }

    private void saveBooking() {
        try {
            // Récupérer les valeurs des champs
            LocalDate startDate = startDatePicker.getValue();
            int startHour = Integer.parseInt(startHourCombo.getValue());
            int startMinute = Integer.parseInt(startMinuteCombo.getValue());

            LocalDate endDate = endDatePicker.getValue();
            int endHour = Integer.parseInt(endHourCombo.getValue());
            int endMinute = Integer.parseInt(endMinuteCombo.getValue());

            String status = statusComboBox.getValue();

            // Créer les LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(startHour, startMinute));
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.of(endHour, endMinute));

            // Vérifier que la date de fin est après la date de début
            if (endDateTime.isBefore(startDateTime) || endDateTime.isEqual(startDateTime)) {
                showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Erreur",
                        "La date de fin doit être après la date de début.");
                return;
            }

            // Mettre à jour l'objet booking
            booking.setStartTime(startDateTime);
            booking.setEndTime(endDateTime);
            booking.setStatus(status);

            // Recalculer le prix total
            double pricePerHour = bookingService.getPricePerHourByLicensePlate(booking.getVehicleLicensePlate());
            double durationHours = java.time.Duration.between(startDateTime, endDateTime).toMinutes() / 60.0;
            double totalPrice = durationHours * pricePerHour;
            booking.setTotalPrice(totalPrice);

            // Sauvegarder dans la base de données
            bookingService.updateBooking(booking);

            // Appeler le callback
            if (onSaveCallback != null) {
                onSaveCallback.call(booking);
            }

            // Fermer la fenêtre
            closeWindow();

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(javafx.scene.control.Alert.AlertType type, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}