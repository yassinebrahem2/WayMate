package controllers;

import entities.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import services.BookingService;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.sql.SQLException;
import java.util.List;

public class AdminEditBookingController {
    @FXML
    private TableView<Booking> bookingTable;

    @FXML
    private TableColumn<Booking, String> createdAtColumn;

    @FXML
    private TableColumn<Booking, String> endTimeColumn;

    @FXML
    private TableColumn<Booking, Integer> idColumn;

    @FXML
    private TableColumn<Booking, Double> priceColumn;

    @FXML
    private TableColumn<Booking, String> startTimeColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private TableColumn<Booking, Integer> userIdColumn;

    @FXML
    private TableColumn<Booking, String> vehicleColumn;

    @FXML
    private void handleSaveChanges() {
        ObservableList<Booking> bookings = bookingTable.getItems();
        BookingService bookingService = new BookingService();

        for (Booking booking : bookings) {
            try {
                // This now calls the full updateBooking method
                bookingService.updateBooking(booking);
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de mise à jour");
                alert.setHeaderText("Erreur lors de la mise à jour de la réservation");
                alert.setContentText("ID: " + booking.getId() + "\n" + e.getMessage());
                alert.show();
                return;
            }
        }
        try {
            List<Booking> updatedBookings = bookingService.getAllBookings();
            bookingTable.setItems(FXCollections.observableArrayList(updatedBookings));
        } catch (SQLException e) {
            System.out.println("Erreur lors du rechargement : " + e.getMessage());
        }

        // Message de succès
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Les réservations ont été mises à jour avec succès.");
        alert.show();

    }
    @FXML
    void initialize() {
        BookingService bookingService = new BookingService();
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            ObservableList<Booking> list = FXCollections.observableArrayList(bookings);

            bookingTable.setEditable(true); // Enable table editing

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
            vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleLicensePlate"));
            startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

            // Set cell factories for editable fields
            userIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            userIdColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                booking.setUserId(event.getNewValue());
            });

            vehicleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            vehicleColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                booking.setVehicleLicensePlate(event.getNewValue());
            });

            startTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            startTimeColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                booking.setStartTime(event.getNewValue());
            });

            endTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            endTimeColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                booking.setEndTime(event.getNewValue());
            });

            priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            priceColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                booking.setTotalPrice(event.getNewValue());
            });

            statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            statusColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                booking.setStatus(event.getNewValue());
            });

            bookingTable.setItems(list); // Set data to table

        } catch (SQLException e) {
            System.out.println("Erreur de chargement : " + e.getMessage());
        }
    }





}
