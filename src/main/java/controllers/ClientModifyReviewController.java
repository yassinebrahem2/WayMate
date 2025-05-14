package controllers;

import entities.Review;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import services.ReviewService;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ClientModifyReviewController implements Initializable {
    @FXML private Label vehicleLabel;
    @FXML private RadioButton rating1, rating2, rating3, rating4, rating5;
    @FXML private TextArea commentArea;

    private Review currentReview;
    private Consumer<Void> refreshCallback;
    private final ReviewService reviewService = new ReviewService();
    private ToggleGroup ratingGroup = new ToggleGroup();

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup rating group
        rating1.setToggleGroup(ratingGroup);
        rating2.setToggleGroup(ratingGroup);
        rating3.setToggleGroup(ratingGroup);
        rating4.setToggleGroup(ratingGroup);
        rating5.setToggleGroup(ratingGroup);
    }


    public void setReview(Review review, Consumer<Void> refreshCallback) {
        this.currentReview = review;
        this.refreshCallback = refreshCallback;

        vehicleLabel.setText(review.getVehicle_license_plate());
        commentArea.setText(review.getComment());

        // Select current rating
        switch (review.getRating()) {
            case 1: rating1.setSelected(true); break;
            case 2: rating2.setSelected(true); break;
            case 3: rating3.setSelected(true); break;
            case 4: rating4.setSelected(true); break;
            case 5: rating5.setSelected(true); break;
        }
    }

    @FXML
    private void handleSaveChanges() {
        try {
            // Get selected rating
            RadioButton selected = (RadioButton) ratingGroup.getSelectedToggle();
            if (selected == null) {
                showAlert("Error", "Please select a rating");
                return;
            }
            int newRating = Integer.parseInt(selected.getUserData().toString());

            // Get comment
            String newComment = commentArea.getText().trim();
            if (newComment.isEmpty()) {
                showAlert("Error", "Please enter a comment");
                return;
            }

            // Update review
            currentReview.setRating(newRating);
            currentReview.setComment(newComment);
            reviewService.updateReview(currentReview);

            // Close dialog and refresh
            closeWindow();
            refreshCallback.accept(null);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update review: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) commentArea.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}