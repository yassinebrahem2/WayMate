package controllers;


import entities.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import services.BookingService;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class AdminEditBookingsController {
    @FXML
    private TableView<Booking> bookingTable;

    @FXML
    private TableColumn<Booking, LocalDateTime> createdAtColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> endTimeColumn;

    @FXML
    private TableColumn<Booking, Integer> idColumn;

    @FXML
    private TableColumn<Booking, Double> priceColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> startTimeColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;
    @FXML private BorderPane mainPane;

    @FXML
    private TableColumn<Booking, Integer> userIdColumn;

    @FXML
    private TableColumn<Booking, String> vehicleColumn;

    @FXML
    private TableColumn<Booking, Void> actionColumn;

    @FXML
    private void handleSaveChanges() {
        ObservableList<Booking> bookings = bookingTable.getItems();
        BookingService bookingService = new BookingService();

        for (Booking booking : bookings) {
            try {
                bookingService.updateBooking(booking);
            } catch (SQLException e) {
                showError("Erreur lors de la mise à jour de la réservation\nID: " + booking.getId() + "\n" + e.getMessage());
                return;
            }
        }

        try {
            // Recharger uniquement les bookings avec status = 'en_attente'
            List<Booking> updatedBookings = bookingService.getAllBookings();
            List<Booking> filtered = updatedBookings.stream()
                    .filter(b -> "en_attente".equalsIgnoreCase(b.getStatus()))
                    .toList();
            bookingTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            System.out.println("Erreur lors du rechargement : " + e.getMessage());
        }

        showInfo("Les réservations ont été mises à jour avec succès.");
    }
    @FXML
    void initialize() {
        BookingService bookingService = new BookingService();
        try {
            // Fetching all bookings from the database
            List<Booking> bookings = bookingService.getAllBookings();
            ObservableList<Booking> list = FXCollections.observableArrayList(
                    bookings.stream()
                            .filter(b -> "en_attente".equalsIgnoreCase(b.getStatus()))
                            .toList()
            );

            bookingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Enable editing for the table view
            bookingTable.setEditable(true);

            // Define columns with PropertyValueFactory
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
            vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleLicensePlate"));
            startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

            // Set cell factories for editable columns
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

            // Date formatter for startTimeColumn and endTimeColumn
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Editable date field: startTimeColumn
            startTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
                @Override
                public String toString(LocalDateTime object) {
                    return object != null ? object.format(formatter) : "";
                }

                @Override
                public LocalDateTime fromString(String string) {
                    try {
                        return LocalDateTime.parse(string, formatter);
                    } catch (DateTimeParseException e) {
                        showError("Format invalide pour la date de début (attendu : yyyy-MM-dd HH:mm)");
                        return null;
                    }
                }
            }));
            startTimeColumn.setOnEditCommit(event -> {
                LocalDateTime newValue = event.getNewValue();
                if (newValue != null) {
                    event.getRowValue().setStartTime(newValue);
                }
            });

            // Editable date field: endTimeColumn
            endTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
                @Override
                public String toString(LocalDateTime object) {
                    return object != null ? object.format(formatter) : "";
                }

                @Override
                public LocalDateTime fromString(String string) {
                    try {
                        return LocalDateTime.parse(string, formatter);
                    } catch (DateTimeParseException e) {
                        showError("Format invalide pour la date de fin (attendu : yyyy-MM-dd HH:mm)");
                        return null;
                    }
                }
            }));
            endTimeColumn.setOnEditCommit(event -> {
                LocalDateTime newValue = event.getNewValue();
                if (newValue != null) {
                    event.getRowValue().setEndTime(newValue);
                }
            });

            // Editable price field
            priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            priceColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                booking.setTotalPrice(event.getNewValue());
            });

            // Editable status field
            statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            statusColumn.setOnEditCommit(event -> {
                Booking booking = event.getRowValue();
                String newStatus = event.getNewValue();
                booking.setStatus(newStatus);

                if ("confirmée".equalsIgnoreCase(newStatus)) {
                    try {
                        new BookingService().updateBooking(booking); // Update DB
                        bookingTable.getItems().remove(booking); // Remove from UI
                        showInfo("Booking confirmée");
                    } catch (SQLException e) {
                        showError("Erreur lors de la confirmation : " + e.getMessage());
                    }
                } else {
                    try {
                        new BookingService().updateBooking(booking); // Still update DB
                    } catch (SQLException e) {
                        showError("Erreur lors de la mise à jour du statut : " + e.getMessage());
                    }
                }
            });

            // Action column (Confirm/Cancel buttons)
            actionColumn.setCellFactory(col -> new TableCell<Booking, Void>() {
                private final Button confirmButton = new Button("✔");
                private final Button cancelButton = new Button("✘");

                {
                    // Style the buttons
                    confirmButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                    cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

                    // Action handlers
                    confirmButton.setOnAction(e -> handleConfirm(getTableRow().getItem()));
                    cancelButton.setOnAction(e -> handleCancel(getTableRow().getItem()));
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Booking booking = getTableRow().getItem();
                        if (booking != null) {
                            HBox box = new HBox(10); // Space between buttons
                            if ("en_attente".equals(booking.getStatus())) {
                                box.getChildren().setAll(confirmButton, cancelButton);
                            } else if ("confirmée".equals(booking.getStatus())) {
                                box.getChildren().setAll(cancelButton);
                            } else if ("annulée".equals(booking.getStatus())) {
                                box.getChildren().setAll(confirmButton);
                            }
                            setGraphic(box);
                        }
                    }
                }
            });

            // Set the table data
            bookingTable.setItems(list);

        } catch (SQLException e) {
            System.out.println("Erreur de chargement : " + e.getMessage());
        }
    }
    private void handleConfirm(Booking booking) {
        if (booking != null) {
            // Update the booking status to "confirmée"
            booking.setStatus("confirmée");

            // Update the booking in the database
            BookingService bookingService = new BookingService();
            try {
                bookingService.updateBooking(booking); // Update the database
                // Remove the booking from the table (so it won't appear in the 'en attente' state)
                bookingTable.getItems().remove(booking);
                showInfo("Réservation confirmée !");
            } catch (SQLException e) {
                showError("Erreur lors de la confirmation de la réservation : " + e.getMessage());
            }
        } else {
            showError("Réservation non trouvée.");
        }
    }

    private void handleCancel(Booking booking) {
        if (booking != null) {
            // Update the booking status to "annulée"
            booking.setStatus("annulée");

            // Update the booking in the database
            BookingService bookingService = new BookingService();
            try {
                bookingService.updateBooking(booking); // Update the database
                // Remove the booking from the table (so it won't appear in the 'en attente' state)
                bookingTable.getItems().remove(booking);
                showInfo("Réservation annulée !");
            } catch (SQLException e) {
                showError("Erreur lors de l'annulation de la réservation : " + e.getMessage());
            }
        } else {
            showError("Réservation non trouvée.");
        }
    }
    @FXML
    void openNotificationsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/review-form-admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Reservation");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openPaymentsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/payment-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Payments");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openReservationsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminHistoriqueBookings.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Reservation");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openUsersTableAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Users");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openVehiclesAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Véhicule");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }









    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setConnectedUserId(int id) {
        // Optionally implement if needed
    }

}






