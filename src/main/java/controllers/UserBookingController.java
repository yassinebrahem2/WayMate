package controllers;

import entities.Booking;
import entities.User;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.BookingService;
import services.UserService;
import services.VehicleService;  // Assuming you have a service for vehicles
import utils.Session;

import java.sql.SQLIntegrityConstraintViolationException;
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
    private Label usernameLabel;


    @FXML
    private DatePicker endDateField;

    @FXML
    private TextField idUsernamefield;

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

            long hours = ChronoUnit.HOURS.between(startDateTime, endDateTime);
            if (hours < 0) {
                showAlert(Alert.AlertType.ERROR, "Durée invalide", "La durée de la réservation ne peut pas être négative.");
                return;
            }

            double pricePerHour = new BookingService().getPricePerHourByLicensePlate(licenseplate);
            double totalPrice = hours * pricePerHour;
            priceLabel.setText(String.format("%.2f DT", totalPrice));

            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Utilisateur non trouvé", "Aucun utilisateur connecté.");
                return;
            }
            if (currentUser.getId() <= 0) {
                showAlert(Alert.AlertType.ERROR, "Utilisateur invalide", "L'ID de l'utilisateur est invalide: " + currentUser.getId());
                return;
            }

            Booking booking = new Booking();
            booking.setUserId(currentUser.getId());
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

        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de contrainte", "L'utilisateur ou le véhicule spécifié n'existe pas dans la base de données.");
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
    @FXML
    public void initialize() {
        int userId = 7;

        // Create an instance of UserService (assuming the constructor doesn't require arguments)
        UserService userService = new UserService();

        // Fetch the user from the database using the user_id
        User user = userService.getUserById(userId);

        // Add listeners to update total price when values change
        licenseplateField.textProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        StartdateField.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        endDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        startHourCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        startMinuteCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        endHourCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        endMinuteCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());

        // Set up hour and minute values for the combo boxes
        ObservableList<String> hours = FXCollections.observableArrayList();
        ObservableList<String> minutes = FXCollections.observableArrayList();

        for (int i = 0; i < 24; i++) hours.add(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5) minutes.add(String.format("%02d", i));

        startHourCombo.setItems(hours);
        startMinuteCombo.setItems(minutes);
        endHourCombo.setItems(hours);
        endMinuteCombo.setItems(minutes);

        // Si un utilisateur est trouvé, on le stocke dans la session
        if (user != null) {
            Session.getInstance().setCurrentUser(user);

            // Maintenant on peut accéder à l'utilisateur courant de la session
            User currentUser = Session.getInstance().getCurrentUser();

            // Afficher le username dans le champ
            if (idUsernamefield != null) {
                idUsernamefield.setText(currentUser.getUsername());
                idUsernamefield.setEditable(true);  // Make sure the field is editable

            } else {
                System.out.println("Erreur : idUsernamefield est null !");
            }
        } else {
            System.out.println("Erreur : L'utilisateur avec l'ID " + userId + " n'a pas été trouvé.");
        }

        // Initialize time values
        startHourCombo.setValue("08");
        startMinuteCombo.setValue("00");
        endHourCombo.setValue("09");
        endMinuteCombo.setValue("00");
        // Execute your insert statement

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
