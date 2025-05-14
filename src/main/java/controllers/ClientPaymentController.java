package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}