package controllers;

import entities.Booking;
import entities.Payment;
import entities.User;
import entities.Vehicle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.PaymentService;
import utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
    private Label messageLabel;

    private final PaymentService paymentService = new PaymentService();
    private Payment payment;
    private Booking currentBooking;

    @FXML
    public void initialize() {
        // Setup payment methods
        paymentMethodCombo.getItems().addAll(
                "carte_bancaire",
                "Paypal",
                "en_espéces"
        );

        // Show/hide card fields based on selection
        paymentMethodCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCard = "carte_bancaire".equals(newVal);
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


    private void updateMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        if (isSuccess) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        }
    }

    public void setBookingData(Booking booking) {
        currentBooking = booking;
    }


    @FXML
    private void handleConfirmPayment() throws SQLException {
        Payment payment = new Payment();
        String method = paymentMethodCombo.getValue();

        if (method == null || method.isEmpty()) {

            System.out.println("NULLLLLLLL");
            updateMessage("Please select a payment method", false);

            return;
        }

        boolean isCard = "carte_bancaire".equals(payment.getMethod());
        if (isCard && (cardNumberField.getText().isEmpty() || cvvField.getText().isEmpty())) {
            updateMessage("Veuillez remplir les informations de carte.", false);
            return;
        }
            /*if (isCard){
                if (cardNumberField.getText().length() != 16 || !cardNumberField.getText().matches("\\d{16}")) {
                    updateMessage("Le numéro de carte doit contenir exactement 16 chiffres.", false);
                    return;
                }

                // Vérification du mot de passe (4 chiffres)
                if (cardPasswordField.getText().length() != 4 || !cardPasswordField.getText().matches("\\d{4}")) {
                    updateMessage("Le mot de passe doit contenir exactement 4 chiffres.", false);
                    return;
                }
            }*/

        if (payment != null) {
            payment.setStatus("payé");
            payment.setPaymentDate(LocalDateTime.now());


            payment.setBookingId(currentBooking.getId());
            payment.setAmount(currentBooking.getTotalPrice());
            payment.setMethod(method);
            payment.setStatus("en_attente");

            paymentService.addPayment(payment);
            messageLabel.setText("Paiement confirmé. Paiement enregistré, merci à la prochaine.");
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");

        } else {
            updateMessage("Paiement échoué.", false);
        }
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