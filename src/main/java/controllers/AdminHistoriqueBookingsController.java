package controllers;

import entities.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import services.BookingService;

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

    private ObservableList<Booking> allBookings;
    private ObservableList<Booking> filteredList;
    private BookingService bookingService;

    @FXML
    public void initialize() throws SQLException {
        bookingService = new BookingService();
        allBookings = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
        loadAllBookings();
        setupTableColumns();
        setupActionColumn();
    }

    private void loadAllBookings() throws SQLException {
        List<Booking> bookings = bookingService.getAllBookings(); // Fetch from service
        allBookings.setAll(bookings);
        filteredList.setAll(allBookings);
        updateTable();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicle")); // Ensure this matches the getter
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price")); // Ensure this matches the getter
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Optional: Add cell factories for better formatting
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
                setText(empty ? null : String.format("%.2f", item));
            }
        });

        bookingTableHistorique.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupActionColumn() {
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            final Button editButton = new Button("Edit");
            final Button deleteButton = new Button("Delete");
            final HBox hbox = new HBox(5, editButton, deleteButton);

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    editButton.getStyleClass().add("action-button");
                    deleteButton.getStyleClass().add("action-button-delete");
                    hbox.setAlignment(javafx.geometry.Pos.CENTER);
                    editButton.setOnAction(event -> {
                        Booking booking = getTableView().getItems().get(getIndex());
                        System.out.println("Edit booking: " + booking.getId());
                    });
                    deleteButton.setOnAction(event -> {
                        Booking booking = getTableView().getItems().get(getIndex());
                        try {
                            bookingService.deleteBooking(booking.getId());
                            allBookings.remove(booking);
                            filteredList.remove(booking);
                            updateTable();
                        } catch (SQLException e) {
                            System.err.println("Failed to delete booking: " + e.getMessage());
                        }
                    });
                    setGraphic(hbox);
                }
            }
        });
        updateTable();
    }

    @FXML
    private void handleSearch() {
        String searchText = vehicleSearchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            filteredList.setAll(allBookings);
        } else {
            filteredList.setAll(allBookings.stream()
                    .filter(booking -> booking.getVehicleLicensePlate().toLowerCase().contains(searchText))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
        updateTable();
    }

    @FXML
    private void handleFilter() {
        boolean enCours = enCoursCheckBox.isSelected();
        boolean terminee = termineeCheckBox.isSelected();
        boolean annulee = annuleeCheckBox.isSelected();

        filteredList.setAll(allBookings.stream()
                .filter(booking -> {
                    String status = booking.getStatus().toLowerCase();
                    return (!enCours || status.contains("en cours")) &&
                            (!terminee || status.contains("terminée")) &&
                            (!annulee || status.contains("annulée"));
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        updateTable();
    }

    private void updateTable() {
        bookingTableHistorique.setItems(filteredList);
    }

    public void setConnectedUserId(int userId) {
        // Implement if needed for your logic (e.g., filter by userId)
    }
}