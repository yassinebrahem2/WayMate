package controllers;

import entities.Booking;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.BookingService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdminHistoriqueBookingsController {

    @FXML
    private TextField vehicleSearchField;

    @FXML
    private CheckBox enCoursCheckBox;

    @FXML
    private CheckBox termineeCheckBox;

    @FXML
    private CheckBox annuleeCheckBox;

    @FXML
    private TableView<Booking> bookingTableHistorique;

    @FXML
    private TableColumn<Booking, Integer> idColumn;

    @FXML
    private TableColumn<Booking, Integer> userIdColumn;

    @FXML
    private TableColumn<Booking, String> vehicleColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> startTimeColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> endTimeColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> createdAtColumn;

    @FXML
    private TableColumn<Booking, Double> priceColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private TableColumn<Booking, String> actionColumn;

    @FXML
    private CheckBox confirmeeCheckBox;

    @FXML
    private BorderPane mainPane;
    private ObservableList<Booking> allBookings;
    private ObservableList<Booking> filteredList;
    private BookingService bookingService;

    private Timeline statusCheckTimer;
    @FXML
    void openNotificationsAdmin(ActionEvent event) {
        try {
            // Close the original window


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
            // Close the original window


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
            // Close the original window


            // Add your users table admin view loading code here
            // Example template:
            FXMLLoader loader = new FXMLLoader(getClass().getResource(""));
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
            // Close the original window


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

    @FXML
    public void initialize() throws SQLException {
        bookingService = new BookingService();
        allBookings = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        setupTableColumns();
        setupActionColumn();
        loadAllBookings();

        // Configuration du timer pour vérifier les statuts
        if (statusCheckTimer == null) {
            statusCheckTimer = new Timeline(
                    new KeyFrame(Duration.seconds(30), e -> {
                        try {
                            checkAndUpdateBookingStatuses();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    })
            );
            statusCheckTimer.setCycleCount(Animation.INDEFINITE);
            statusCheckTimer.play();
        }
    }

    private void checkAndUpdateBookingStatuses() throws SQLException {
        // Mettre à jour les statuts en base de données
        bookingService.updateBookingStatuses();

        // Recharger les réservations avec les statuts à jour
        loadAllBookings();
    }

    private void loadAllBookings() throws SQLException {
        List<Booking> bookings = bookingService.getAllBookings();
        allBookings.setAll(bookings);
        applyFilters(); // Applique les filtres actuels
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleLicensePlate"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("TotalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Formattage pour les cellules
        vehicleColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });

        priceColumn.setCellFactory(column -> new TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("%.2f DT", item));
            }
        });

        statusColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);

                if (item != null) {
                    switch (item) {
                        case "confirmée":
                            setStyle("-fx-text-fill: blue;");
                            break;
                        case "en_cours":
                            setStyle("-fx-text-fill: green;");
                            break;
                        case "terminée":
                            setStyle("-fx-text-fill: black;");
                            break;
                        case "annulée":
                            setStyle("-fx-text-fill: red;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                } else {
                    setStyle("");
                }
            }
        });

        bookingTableHistorique.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox hbox = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().add("action-button");
                deleteButton.getStyleClass().add("action-button-delete");
                hbox.setAlignment(javafx.geometry.Pos.CENTER);

                editButton.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    try {
                        // Use the correct FXML path (adjust based on your project structure)
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminEditPopUp.fxml"));
                        Parent root = loader.load();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));

                        stage.show();

                        AdminEditPopUpController controller = loader.getController();
                        controller.setBooking(booking);
                        controller.setOnSaveCallback(updatedBooking -> {
                            try {
                                bookingService.updateBooking(updatedBooking); // Persist changes to the database
                                loadAllBookings(); // Reload bookings to refresh the UI
                            } catch (SQLException e) {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update booking: " + e.getMessage());
                            }
                            return null;
                        });

                        Stage popupStage = new Stage();
                        popupStage.setTitle("Edit Booking");
                        popupStage.setScene(new Scene(root));
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open edit popup: " + e.getMessage());
                    }
                });

                deleteButton.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirmation");
                    confirmAlert.setHeaderText("Delete Booking");
                    confirmAlert.setContentText("Are you sure you want to delete this booking?");

                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                bookingService.deleteBooking(booking.getId());
                                allBookings.remove(booking);
                                filteredList.remove(booking);
                                updateTable();
                                showAlert(Alert.AlertType.INFORMATION, "Success", "Booking deleted successfully!");
                            } catch (SQLException e) {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete booking: " + e.getMessage());
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Booking booking = getTableView().getItems().get(getIndex());
                editButton.setDisable("terminée".equalsIgnoreCase(booking.getStatus()) ||
                        "annulée".equalsIgnoreCase(booking.getStatus()));
                setGraphic(hbox);
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleFilter() {
        applyFilters();
    }

    private void applyFilters() {
        String searchText = vehicleSearchField.getText().trim().toLowerCase();
        boolean filterEnCours = enCoursCheckBox.isSelected();
        boolean filterTerminee = termineeCheckBox.isSelected();
        boolean filterAnnulee = annuleeCheckBox.isSelected();
        boolean filterConfirmee = confirmeeCheckBox.isSelected(); // Nouveau filtre

        // Si aucun filtre n'est sélectionné, on affiche tout
        if (!filterEnCours && !filterTerminee && !filterAnnulee && !filterConfirmee) {
            filterEnCours = true;
            filterTerminee = true;
            filterAnnulee = true;
            filterConfirmee = true;
        }

        boolean finalFilterEnCours = filterEnCours;
        boolean finalFilterTerminee = filterTerminee;
        boolean finalFilterAnnulee = filterAnnulee;
        boolean finalFilterConfirmee = filterConfirmee;

        filteredList.setAll(allBookings.stream()
                .filter(booking -> {
                    // Filtre par recherche texte
                    if (!searchText.isEmpty() &&
                            !booking.getVehicleLicensePlate().toLowerCase().contains(searchText)) {
                        return false;
                    }

                    // Filtre par statut
                    String status = booking.getStatus();
                    return (finalFilterEnCours && "en_cours".equals(status)) ||
                            (finalFilterTerminee && "terminée".equals(status)) ||
                            (finalFilterAnnulee && "annulée".equals(status)) ||
                            (finalFilterConfirmee && "confirmée".equals(status));
                })
                .collect(Collectors.toList()));

        updateTable();
    }


    private void updateTable() {
        bookingTableHistorique.setItems(filteredList);
        bookingTableHistorique.refresh();
    }

    public void setConnectedUserId(int userId) {
        // Implémentation si nécessaire pour votre logique
    }
    @FXML
    void BringCalendar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/booking-admin-calendar.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Calendar");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void editBookings(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-edit-bookings-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Edit Bookings");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}