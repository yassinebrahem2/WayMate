package controllers;

import entities.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import services.VehicleService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VehiculeMainController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;

    @FXML
    private TableView<Vehicle> vehiclesTable;
    @FXML
    private TableColumn<Vehicle, String> licensePlateColumn;
    @FXML
    private TableColumn<Vehicle, String> typeColumn;
    @FXML
    private TableColumn<Vehicle, String> brandColumn;
    @FXML
    private TableColumn<Vehicle, String> modelColumn;
    @FXML
    private TableColumn<Vehicle, Integer> yearColumn;
    @FXML
    private TableColumn<Vehicle, String> statusColumn;
    @FXML
    private TableColumn<Vehicle, Double> priceColumn;

    private final VehicleService vehicleService = new VehicleService();
    private final ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();
    @FXML
    private BorderPane mainPane;

    @FXML
    private VBox sidebar;

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
    void addVehicle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add-vehicle-admin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Véhicule");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            //stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startAutoRefresh() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(10), event -> refreshTable())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
    void modifyVehicle(ActionEvent event) {
        Vehicle selectedVehicle = vehiclesTable.getSelectionModel().getSelectedItem();
        System.out.println(selectedVehicle.getLicensePlate());
        if (selectedVehicle != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modify-vehicle-admin.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Modify un Véhicule");
                stage.setScene(new Scene(root));
                stage.setResizable(false);

                ModifyVehicleController controller = loader.getController();
                controller.setData(selectedVehicle);


                //stage.setAlwaysOnTop(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        startAutoRefresh();

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