package controllers;

import entities.Booking;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.BookingService;
import services.VehicleService;  // Assuming you have a service for vehicles

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private Label priceLabel;

    @FXML
    private ComboBox<String> endHourCombo;

    @FXML
    private ComboBox<String> endMinuteCombo;

    @FXML
    private ComboBox<String> startHourCombo;

    @FXML
    private ComboBox<String> startMinuteCombo;

    private final double DEFAULT_PRICE_PER_HOUR = 30.0; // Default price if vehicle doesn't exist

    private int connectedUserId = 8;

    @FXML
    void handleAddBooking(ActionEvent event) {
        try {
            String licenseplate = licenseplateField.getText().trim();
            LocalDate dateDebut = StartdateField.getValue();
            LocalDate dateFin = endDateField.getValue();

            if (licenseplate.isEmpty() || dateDebut == null || dateFin == null) {
                showAlert(Alert.AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs requis.");
                return;
            }

            if (dateFin.isBefore(dateDebut)) {
                showAlert(Alert.AlertType.ERROR, "Dates invalides", "La date de fin doit être postérieure à la date de début.");
                return;
            }

            // Get start and end times
            String startDate = StartdateField.getValue().toString();
            String startHour = startHourCombo.getValue();
            String startMinute = startMinuteCombo.getValue();
            String fullStartTime = startDate + " " + startHour + ":" + startMinute;

            String endDate = endDateField.getValue().toString();
            String endHour = endHourCombo.getValue();
            String endMinute = endMinuteCombo.getValue();
            String fullEndTime = endDate + " " + endHour + ":" + endMinute;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime startDateTime = LocalDateTime.parse(fullStartTime, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(fullEndTime, formatter);

            // Calculate the total price based on the price_per_hour and booking duration
            long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
            if (hours < 0) {
                showAlert(Alert.AlertType.ERROR, "Durée invalide", "La durée de la réservation ne peut pas être négative.");
                return;
            }

            double pricePerHour = new BookingService().getPricePerHourByLicensePlate(licenseplate);

            double totalPrice = hours * pricePerHour;
            priceLabel.setText(String.format("%.2f DT", totalPrice));

            // Prepare booking object
            Booking booking = new Booking();
            booking.setUserId(connectedUserId);
            booking.setVehicleLicensePlate(licenseplate);
            booking.setStartTime(startDateTime);
            booking.setEndTime(endDateTime);
            booking.setStatus("en_attente");
            booking.setTotalPrice(totalPrice);

            BookingService bookingService = new BookingService();
            bookingService.addBooking(booking);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès !");
            licenseplateField.clear();
            StartdateField.setValue(LocalDate.now());
            endDateField.setValue(LocalDate.now().plus(1, ChronoUnit.DAYS));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    // Helper method to show alert dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Initialize listeners for date changes
    @FXML
    public void initialize() {
        licenseplateField.textProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());

        StartdateField.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        endDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());

        startHourCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        startMinuteCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        endHourCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        endMinuteCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());


        idUserField.setText(String.valueOf(connectedUserId));
        idUserField.setDisable(true);

        ObservableList<String> hours = FXCollections.observableArrayList();
        ObservableList<String> minutes = FXCollections.observableArrayList();

        for (int i = 0; i < 24; i++) hours.add(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5) minutes.add(String.format("%02d", i));

        startHourCombo.setItems(hours);
        startMinuteCombo.setItems(minutes);
        endHourCombo.setItems(hours);
        endMinuteCombo.setItems(minutes);

        startHourCombo.setValue("08");
        startMinuteCombo.setValue("00");
        endHourCombo.setValue("09");
        endMinuteCombo.setValue("00");
    }

    public void setConnectedUserId(int userId) {
        this.connectedUserId = userId;

        // Populate the text field (if it's already initialized)
        if (idUserField != null) {
            idUserField.setText(String.valueOf(userId));
        }
    }

    private void updateTotalPrice() {
        try {
            String licensePlate = licenseplateField.getText().trim();
            if (licensePlate.isEmpty()) return;

            if (StartdateField.getValue() == null || endDateField.getValue() == null
                    || startHourCombo.getValue() == null || startMinuteCombo.getValue() == null
                    || endHourCombo.getValue() == null || endMinuteCombo.getValue() == null) {
                return;
            }

            LocalDateTime start = LocalDateTime.parse(
                    StartdateField.getValue() + " " + startHourCombo.getValue() + ":" + startMinuteCombo.getValue(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );
            LocalDateTime end = LocalDateTime.parse(
                    endDateField.getValue() + " " + endHourCombo.getValue() + ":" + endMinuteCombo.getValue(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );

            if (end.isBefore(start)) {
                priceLabel.setText("0.00 DT");
                return;
            }

            double durationHours = java.time.Duration.between(start, end).toMinutes() / 60.0;

            double pricePerHour = new BookingService().getPricePerHourByLicensePlate(licensePlate);
            if (pricePerHour == 0.0) pricePerHour = DEFAULT_PRICE_PER_HOUR;

            double total = durationHours * pricePerHour;
            priceLabel.setText(String.format("%.2f DT", total));
        } catch (Exception e) {
            priceLabel.setText("Erreur de calcul");
        }
    }


}
