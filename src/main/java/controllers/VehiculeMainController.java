package controllers;

import entities.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.VehicleService;

import java.net.URL;
import java.util.ResourceBundle;

public class VehiculeMainController {

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    @FXML private TableView<Vehicle> vehiclesTable;
    @FXML private TableColumn<Vehicle, String> licensePlateColumn;
    @FXML private TableColumn<Vehicle, String> typeColumn;
    @FXML private TableColumn<Vehicle, String> brandColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, Integer> yearColumn;
    @FXML private TableColumn<Vehicle, String> statusColumn;
    @FXML private TableColumn<Vehicle, Double> priceColumn;

    private final VehicleService vehicleService = new VehicleService();
    private final ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    @FXML
    void addVehicle(ActionEvent event) {
        // Create a dialog or new window for adding a vehicle
        System.out.println("Add vehicle button clicked");
        // You can implement this using a new FXML form
    }

    @FXML
    void delVehicle(ActionEvent event) {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            boolean deleted = vehicleService.deleteVehicle(selectedVehicle.getLicensePlate());
            if (deleted) {
                vehicleData.remove(selectedVehicle);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle deleted successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete vehicle");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a vehicle to delete");
        }
    }

    @FXML
    void initialize() {
        // Initialize table columns
        licensePlateColumn.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerHour"));

        // Add context menu for row actions
        setupContextMenu();

        // Load vehicle data
        refreshTable();
    }

    private void setupContextMenu() {
        // Create context menu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(event -> {
            Vehicle selected = vehiclesTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                editVehicle(selected);
            }
        });

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> delVehicle(null));

        contextMenu.getItems().addAll(editItem, deleteItem);

        // Set context menu on table
        vehiclesTable.setContextMenu(contextMenu);
    }

    private void editVehicle(Vehicle vehicle) {
        // Implement your edit dialog here
        System.out.println("Editing vehicle: " + vehicle.getLicensePlate());
    }

    public void refreshTable() {
        vehicleData.setAll(vehicleService.getAllVehicles());
        vehiclesTable.setItems(vehicleData);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}