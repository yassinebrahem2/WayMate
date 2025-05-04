package controllers;

import entities.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import services.BookingService;

import java.awt.print.Book;
import java.io.IOException;
import java.util.List;

public class UserBookingListController {
    @FXML
    private TableView<Booking> bookingTable;

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


    @FXML
    public void initialize() {
        // Setup column bindings
        colLicensePlate.setCellValueFactory(new PropertyValueFactory<>("vehicleLicensePlate"));
        colStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Load bookings for current user
        loadUserBookings();
    }

    private int connectedUserId;
    public void setConnectedUserId(int connectedUserId) {
        this.connectedUserId = connectedUserId;
        loadUserBookings(); // Optionally trigger data loading here
    }


    private void loadUserBookings() {
        if (connectedUserId == 0) return; // prevent loading if user not set

        BookingService bookingService = new BookingService();
        List<Booking> userBookings = bookingService.getBookingsByUserId(connectedUserId);
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(userBookings);
        bookingTable.setItems(bookingList);
    }
    @FXML
    private void handleBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user-profile-view.fxml")); // ✅ Correct path
            Parent root = loader.load();

            // OPTIONAL: pass user ID to user profile controller
            // UserProfileController controller = loader.getController();
            // controller.setConnectedUserId(connectedUserId);

            bookingTable.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void handleDeleteBooking() {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();

        if (selectedBooking != null) {
            BookingService bookingService = new BookingService();
            boolean success = bookingService.deleteBookingById(selectedBooking.getId());

            if (success) {
                bookingTable.getItems().remove(selectedBooking); // update UI
                System.out.println("Booking deleted successfully.");
            } else {
                System.out.println("Failed to delete booking.");
            }
        } else {
            System.out.println("Please select a booking to delete.");
        }

    }
}
