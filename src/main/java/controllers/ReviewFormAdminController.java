package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import entities.Review;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ReviewService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.List;

public class ReviewFormAdminController implements Initializable {

    private final ReviewService reviewService = new ReviewService();

    @FXML
    private TableView<Review> reviewTable;

    @FXML
    private TableColumn<Review, Integer> idColumn;

    @FXML
    private TableColumn<Review, String> userIdColumn;

    @FXML
    private TableColumn<Review, String> vehicleColumn;

    @FXML
    private TableColumn<Review, Integer> ratingColumn;

    @FXML
    private TableColumn<Review, String> commentColumn;

    @FXML
    private TableColumn<Review, String> createdAtColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicle_license_plate"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));

        try {
            List<Review> reviews = reviewService.getAllReviews();
            reviewTable.getItems().setAll(reviews);
        }
         catch (Exception e) {
            e.printStackTrace();
        }

    }
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
            // Close the original window


            // Add your payments admin view loading code here
            // Example template:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/payments-admin.fxml"));
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
    private void handleSupprimer() {
        Review selectedReview = reviewTable.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            try {
                reviewService.supprimer(selectedReview.getId());
                reviewTable.getItems().remove(selectedReview);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun avis sélectionné pour la suppression.");
        }
    }
}

