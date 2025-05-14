package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import entities.Booking;
import services.BookingService;
import entities.User;
import utils.Session;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientBookingsController {

    @FXML
    private VBox bookingsContainer;

    @FXML
    private Button profileButton;

    @FXML
    private Button vehiclesButton;

    @FXML
    private Button reviewsButton;

    @FXML
    private Button bookingsButton;

    private User currentUser;

    private final BookingService bookingService = new BookingService();
    // Initialize method - called after FXML fields are populated
    @FXML
    public void initialize() {
        currentUser = Session.getInstance().getCurrentUser();
        loadBookings();
    }

    private void loadBookings() {
        // Clear existing bookings
        bookingsContainer.getChildren().clear();

        // Example: Get bookings from a service (replace with your actual data source)
        List<Booking> bookings = bookingService.getBookingsByUserId(currentUser.getId());

        if (bookings.isEmpty()) {
            Label noBookingsLabel = new Label("You don't have any bookings yet.");
            noBookingsLabel.getStyleClass().add("no-bookings-label");
            bookingsContainer.getChildren().add(noBookingsLabel);
            return;
        }

        // Create a card for each booking
        for (Booking booking : bookings) {
            bookingsContainer.getChildren().add(createBookingCard(booking));
        }
    }

    private HBox createBookingCard(Booking booking) {
        HBox card = new HBox(20);
        card.getStyleClass().add("booking-card");

        // Left side - booking details
        VBox detailsBox = new VBox(8);
        detailsBox.getStyleClass().add("booking-details");

        Label bookingIdLabel = new Label("Booking #" + booking.getId());
        bookingIdLabel.getStyleClass().add("booking-id");

        Label vehicleLabel = new Label("Vehicle: " + booking.getVehicleLicensePlate());
        Label datesLabel = new Label(booking.getEndTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                " - " + booking.getEndTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        detailsBox.getChildren().addAll(bookingIdLabel, vehicleLabel, datesLabel);

        // Right side - status and price
        VBox statusBox = new VBox(8);
        statusBox.getStyleClass().add("booking-status");

        Label statusLabel = new Label(booking.getStatus());
        statusLabel.getStyleClass().add("status-" + booking.getStatus().toLowerCase());

        Label priceLabel = new Label("$" + String.format("%.2f", booking.getTotalPrice()));
        priceLabel.getStyleClass().add("booking-price");

        statusBox.getChildren().addAll(statusLabel, priceLabel);

        card.getChildren().addAll(detailsBox, statusBox);
        return card;
    }

    @FXML
    private void handleProfileButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-profile-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleVehiclesButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-vehicle-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleReviewsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-reviews-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleNotificationsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-notifications-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookingsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-bookings-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}