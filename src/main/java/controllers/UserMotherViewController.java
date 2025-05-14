package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

import java.io.IOException;

public class UserMotherViewController {

    @FXML
    private StackPane center;

    // Method to load the Vehicles User view
    @FXML
    private void openVehiclesUser() throws IOException {

    }

    // Method to load the Users Table view
    @FXML
    private void openUsersTableUser() throws IOException {
    }

    // Method to load the Notifications User view
    @FXML
    private void openNotificationsUser() throws IOException {
        loadView("/NotificationsUserView.fxml");  // Path to Notifications User view
    }

    // Method to load the Payments User view
    @FXML
    private void openPaymentsUser() throws IOException {
    }

    // Method to load the Reservations User view
    @FXML
    private void openReservationsUser() throws IOException {
        loadView("/BookingsUserView.fxml");  // Path to Reservations User view
    }

    // Helper method to load the FXML views into the center area
    private void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent newContent = loader.load();
        center.getChildren().setAll(newContent);
    }
}
