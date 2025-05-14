package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientPaymentController {
    @FXML private Label vehicleLabel;
    @FXML private Label periodLabel;
    @FXML private Label amountLabel;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private TextField cvvField;
    @FXML private Label cardNumberLabel;
    @FXML private Label expiryDateLabel;
    @FXML private Label cvvLabel;

    @FXML
    public void initialize() {
        // Setup payment methods
        paymentMethodCombo.getItems().addAll(
                "Credit Card",
                "PayPal",
                "Bank Transfer",
                "Cash"
        );

        // Show/hide card fields based on selection
        paymentMethodCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCard = "Credit Card".equals(newVal);
            cardNumberField.setVisible(isCard);
            expiryDateField.setVisible(isCard);
            cvvField.setVisible(isCard);
            cardNumberLabel.setVisible(isCard);
            expiryDateLabel.setVisible(isCard);
            cvvLabel.setVisible(isCard);
        });
    }

    // Call this method when loading the payment form
    public void setBookingDetails(String vehicleInfo, String period, String amount) {
        vehicleLabel.setText(vehicleInfo);
        periodLabel.setText(period);
        amountLabel.setText(amount);
    }

    @FXML
    private void handleConfirmPayment() {
        // Validate and process payment
        String method = paymentMethodCombo.getValue();

        if (method == null || method.isEmpty()) {
            showAlert("Error", "Please select a payment method");
            return;
        }

        if ("Credit Card".equals(method)) {
            if (!validateCardDetails()) {
                return;
            }
        }

        // Process payment...
        showAlert("Success", "Payment processed successfully");
    }

    private boolean validateCardDetails() {
        // Add proper validation logic
        return true;
    }

    @FXML
    private void handleCancel() {
        // Close the payment window
//        ((Stage) cancelButton.getScene().getWindow()).close();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}