package controllers;

import entities.Booking;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.BookingService;
import services.UserService;
import utils.Session;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserBookingListController {

    @FXML
    private TableView<Booking> bookingTableUser;

    @FXML
    private TableColumn<Booking, String> colEndTime;

    @FXML
    private TableColumn<Booking, String> colLicensePlate;

    @FXML
    private TableColumn<Booking, String> colStartTime;

    @FXML
    private TableColumn<Booking, String> colStatus;

    @FXML
    private TableColumn<Booking, Double> colTotalPrice;

    private int connectedUserId;

    @FXML
    public void initialize() {
        // Set initial user from Session or hardcoded for testing
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId() > 0) {
            connectedUserId = currentUser.getId();
            System.out.println("Initialized with user ID from Session: " + connectedUserId);
        } else {
            connectedUserId = 7; // Fallback for testing
            UserService userService = new UserService();
            User user = userService.getUserById(connectedUserId);
            if (user != null) {
                Session.getInstance().setCurrentUser(user);
                System.out.println("Initialized with fallback user ID: " + connectedUserId);
            } else {
                System.out.println("User with ID " + connectedUserId + " not found.");
            }
        }

        // Setup table and columns
        bookingTableUser.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Bind columns with custom formatting for LocalDateTime
        colLicensePlate.setCellValueFactory(new PropertyValueFactory<>("vehicleLicensePlate"));
        colStartTime.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        colEndTime.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Load bookings
        loadUserBookings();
    }

    public void setConnectedUserId(int connectedUserId) {
        this.connectedUserId = connectedUserId;
        loadUserBookings(); // Trigger data loading
        System.out.println("User ID set to: " + connectedUserId);
    }

    private void loadUserBookings() {
        if (connectedUserId <= 0) {
            System.out.println("No valid user ID set for loading bookings.");
            return;
        }

        BookingService bookingService = new BookingService();
        List<Booking> userBookings = bookingService.getBookingsByUserId(connectedUserId);
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(userBookings);
        bookingTableUser.setItems(bookingList);
        System.out.println("Loaded " + userBookings.size() + " bookings for user ID: " + connectedUserId);
    }

    @FXML
    private void handleBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-profile-view.fxml"));
            Parent root = loader.load();
            bookingTableUser.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation", "Impossible de charger la vue du profil.");
        }
    }

    @FXML
    private void handleDeleteBooking() {
        Booking selectedBooking = bookingTableUser.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réservation à supprimer.");
            return;
        }

        BookingService bookingService = new BookingService();
        boolean success = bookingService.deleteBookingById(selectedBooking.getId());

        if (success) {
            bookingTableUser.getItems().remove(selectedBooking);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée avec succès !");
            System.out.println("Booking deleted successfully. ID: " + selectedBooking.getId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Échec", "Échec de la suppression de la réservation.");
            System.out.println("Failed to delete booking. ID: " + selectedBooking.getId());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void openVehiclesUser(ActionEvent actionEvent) {
    }

    public void openUsersTableUser(ActionEvent actionEvent) {
    }

    public void openNotificationsUser(ActionEvent actionEvent) {
    }

    public void openPaymentsUser(ActionEvent actionEvent) {
    }

    public void openReservationsUser(ActionEvent actionEvent) {
    }
}