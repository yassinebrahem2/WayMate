package controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardMainController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private VBox sidebar;

    @FXML
    void openNotificationsAdmin(ActionEvent event) {

    }

    @FXML
    void openPaymentsAdmin(ActionEvent event) {

    }

    @FXML
    void openReservationsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/booking-admin-view.fxml"));
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

    @FXML
    void openUsersTableAdmin(ActionEvent event) {

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
            //stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
