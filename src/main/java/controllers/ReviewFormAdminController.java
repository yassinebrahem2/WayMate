package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import entities.Review;
import services.ReviewService;

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

        List<Review> reviews = reviewService.afficher();
        reviewTable.getItems().setAll(reviews);
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

