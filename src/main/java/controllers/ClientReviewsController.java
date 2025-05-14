package controllers;

import entities.Review;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Pos;


import services.ReviewService;
import utils.Session;

import java.io.IOException;
import java.util.List;

public class ClientReviewsController {
    @FXML private HBox reviewsHBox;
    @FXML private VBox reviewsContainer;

    private final ReviewService reviewService = new ReviewService();

    @FXML
    public void initialize() {
        loadUserReviews();
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox();
        card.getStyleClass().add("review-card");
        card.setSpacing(10);
        card.setPrefWidth(800); // Fixed width for consistency

        // Vehicle info
        Label vehicleLabel = new Label("Vehicle: " + review.getVehicle_license_plate());
        vehicleLabel.getStyleClass().add("review-vehicle");

        // Rating
        Label ratingLabel = new Label("Rating: " + "★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
        ratingLabel.getStyleClass().add("review-rating");

        // Comment
        TextArea commentArea = new TextArea(review.getComment());
        commentArea.getStyleClass().add("review-comment");
        commentArea.setEditable(false);
        commentArea.setWrapText(true);

        // Date
        Label dateLabel = new Label("Posted on: " + review.getCreated_at());
        dateLabel.getStyleClass().add("review-date");

        // Modify button
        Button modifyButton = new Button("Modify");
        modifyButton.getStyleClass().add("modify-button");
        modifyButton.setOnAction(e -> openModifyReviewDialog(review));

        // Create a container for the button to right-align it
        HBox buttonContainer = new HBox(modifyButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(vehicleLabel, ratingLabel, commentArea, dateLabel, buttonContainer);
        return card;
    }

    private void loadUserReviews() {
        reviewsContainer.getChildren().clear();

        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) return;

        try {
            List<Review> reviews = reviewService.getReviewsByUserId(currentUser.getId());

            for (Review review : reviews) {
                reviewsContainer.getChildren().add(createReviewCard(review));
            }

            if (reviews.isEmpty()) {
                Label noReviews = new Label("You haven't submitted any reviews yet");
                noReviews.getStyleClass().add("no-reviews-label");
                reviewsContainer.getChildren().add(noReviews);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load reviews: " + e.getMessage());
        }
    }

    private void openModifyReviewDialog(Review review) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-modify-review-view.fxml"));
            Parent dialogRoot = loader.load();


            ClientModifyReviewController controller = loader.getController();
            controller.setReview(review, unused -> loadUserReviews());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modify Your Review");
            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open modification dialog");
        }
    }

    @FXML
    private void handleProfileButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-profile-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleVehiclesButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-vehicle-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleReviewsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-reviews-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleNotificationsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-notifications-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookingsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-bookings-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}