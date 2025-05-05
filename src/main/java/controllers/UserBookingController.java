package controllers;

import entities.Booking;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private Label priceLabel;

    private final double PRICE_PER_DAY = 30.0;
    private int connectedUserId = 8;

    @FXML
    void handleAddBooking(ActionEvent event) {
        try {
            String licenseplate = licenseplateField.getText().trim();
            LocalDate dateDebut = StartdateField.getValue();
            LocalDate dateFin = endDateField.getValue();


            double totalPrice = 0.0;


            String rawPrice = priceLabel.getText().replace("DT", "").trim().replace(",", ".");


            System.out.println("Prix brut nettoyé: " + rawPrice);  // Débogage


            totalPrice = Double.parseDouble(rawPrice);




            if (licenseplate.isEmpty() || dateDebut == null || dateFin == null) {
                showAlert(Alert.AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs requis.");
                return;
            }

            if (dateFin.isBefore(dateDebut)) {
                showAlert(Alert.AlertType.ERROR, "Dates invalides", "La date de fin doit être postérieure à la date de début.");
                return;
            }

            int userId = connectedUserId;

            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setVehicleLicensePlate(licenseplate);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            booking.setStartTime(dateDebut.format(formatter));
            booking.setEndTime(dateFin.format(formatter));
            booking.setStatus("en_attente");
            booking.setTotalPrice(totalPrice);

            BookingService bookingService = new BookingService();
            bookingService.addBooking(booking);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès !");
            licenseplateField.clear();
            StartdateField.setValue(LocalDate.now());
            endDateField.setValue(LocalDate.now().plus(1, ChronoUnit.DAYS));

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de conversion", "Erreur lors de la lecture du prix.");
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

    // Update the price based on start and end date
    private void updatePrice() {
        LocalDate start = StartdateField.getValue();
        LocalDate end = endDateField.getValue();

        if (start != null && end != null && !end.isBefore(start)) {
            long days = ChronoUnit.DAYS.between(start, end) + 1; // +1 to include the last day
            double price = days * PRICE_PER_DAY;
            priceLabel.setText(String.format("%.2f DT", price));
        } else {
            priceLabel.setText("0.00 DT");
        }
    }

    // Initialize listeners for date changes
    @FXML
    public void initialize() {
        ChangeListener<Object> dateChangeListener = (obs, oldVal, newVal) -> updatePrice();

        StartdateField.valueProperty().addListener(dateChangeListener);
        endDateField.valueProperty().addListener(dateChangeListener);
        idUserField.setText(String.valueOf(connectedUserId));
        idUserField.setDisable(true); // Optional: disable if you don't want users to change it

    }


    public void setConnectedUserId(int userId) {
        this.connectedUserId = userId;

        // Populate the text field (if it's already initialized)
        if (idUserField != null) {
            idUserField.setText(String.valueOf(userId));
        }
    }

}
