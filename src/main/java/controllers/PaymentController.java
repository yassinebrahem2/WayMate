package controllers;

import entities.Payment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import services.PaymentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField bookingIdField;
    @FXML private TextField amountField;
    @FXML private TextField paymentDateField;
    @FXML private ComboBox<String> methodComboBox;
    @FXML private TextField statusField;
    @FXML private CheckBox termsCheckBox;
    @FXML private Label messageLabel;

    // TableView et colonnes
    @FXML private TableView<Payment> paymentTableView;
    @FXML private TableColumn<Payment, Integer> idColumn;
    @FXML private TableColumn<Payment, Integer> bookingIdColumn;
    @FXML private TableColumn<Payment, Double> amountColumn;
    @FXML private TableColumn<Payment, String> paymentDateColumn;
    @FXML private TableColumn<Payment, String> methodColumn;
    @FXML private TableColumn<Payment, String> statusColumn;

    // Champs pour affichage après bouton Pay
    @FXML private TextField displayAmountField;
    @FXML private TextField displayMethodField;
    @FXML private TextField cardNumberField;
    @FXML private PasswordField cardPasswordField;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML
    private Label confirmationMessageLabel;

    private final PaymentService paymentService = new PaymentService();

    private void resetConfirmationMessage() {
        confirmationMessageLabel.setText("");  // Réinitialiser le texte
    }

    private void updateMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        if (isSuccess) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        methodComboBox.setItems(FXCollections.observableArrayList("carte_bancaire","Paypal","en_espéces" ));

        try {
            paymentService.deleteOldPayments();
            List<Payment> allPayments = paymentService.getAllPayments();
            for (Payment p : allPayments) {
                paymentService.markAsLate(p);
                paymentService.updatePayment(p, p.getId());
            }
            loadPaymentsToTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPaymentsToTable() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            ObservableList<Payment> data = FXCollections.observableArrayList(payments);
            paymentTableView.setItems(data);
            idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
            bookingIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getBookingId()).asObject());
            amountColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getAmount()).asObject());
            paymentDateColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                    cell.getValue().getPaymentDate() != null ? cell.getValue().getPaymentDate().toString() : ""));
            methodColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMethod()));
            statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddPayment() {
        if (!termsCheckBox.isSelected()) {
            updateMessage("Vous devez accepter les conditions d'utilisation.",false);
            return;
        }
        try {
            int bookingId = Integer.parseInt(bookingIdField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String method = methodComboBox.getValue();

            Payment newPayment = new Payment(0, bookingId, amount, null, method, "en_attente");
            double calculatedAmount = paymentService.calculatePayment(List.of(amount));
            paymentService.addPayment(newPayment, calculatedAmount);

            updateMessage("Paiement ajouté avec succès.",true);
            loadPaymentsToTable();
        } catch (Exception e) {
            updateMessage("Erreur lors de l'ajout du paiement.",false);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleModifyPayment() {
        if (!termsCheckBox.isSelected()) {
            updateMessage("Vous devez accepter les conditions d'utilisation.",false);
            return;
        }
        try {
            int id = Integer.parseInt(idField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String dateStr = paymentDateField.getText();
            String method = methodComboBox.getValue();
            String status = statusField.getText();

            LocalDateTime paymentDate;
            try {
                paymentDate = LocalDateTime.parse(dateStr);
            } catch (DateTimeParseException e) {
                updateMessage("Format de date invalide. Utilisez yyyy-MM-ddTHH:mm:ss",false);
                return;
            }

            Payment originalPayment = paymentService.getPayment(id);
            if (originalPayment != null) {
                originalPayment.setAmount(amount);
                originalPayment.setPaymentDate(paymentDate);
                originalPayment.setMethod(method);
                originalPayment.setStatus(status);

                paymentService.updatePayment(originalPayment, id);
                updateMessage("Paiement modifié avec succès.",true);
                loadPaymentsToTable();
            } else {
                updateMessage("Paiement introuvable.",false);
            }
        } catch (Exception e) {
            updateMessage("Erreur lors de la modification du paiement.",false);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeletePayment() {
        if (!termsCheckBox.isSelected()) {
            updateMessage("Vous devez accepter les conditions d'utilisation.",false);
            return;
        }
        try {
            int id = Integer.parseInt(idField.getText());
            paymentService.deletePayment(id);
            updateMessage("Paiement supprimé avec succès.",true);
            loadPaymentsToTable();
        } catch (Exception e) {
            updateMessage("Erreur lors de la suppression du paiement.",false);
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePayPayment() {
        if (!termsCheckBox.isSelected()) {
            updateMessage("Vous devez accepter les conditions d'utilisation.",false);
            return;
        }
        try {
            int id = Integer.parseInt(idField.getText());
            Payment payment = paymentService.getPayment(id);
            if (payment != null) {
                List<Double> relatedAmounts = paymentService.getAmountsByPaymentId(id);
                double totalAmount = paymentService.calculatePayment(relatedAmounts);

                displayAmountField.setText(String.valueOf(totalAmount));
                displayMethodField.setText(methodComboBox.getValue());

                // Afficher les champs carte si nécessaire
                boolean isCard = "carte_bancaire".equals(methodComboBox.getValue());
                cardNumberField.setVisible(isCard);
                cardPasswordField.setVisible(isCard);
                confirmButton.setVisible(true);
                cancelButton.setVisible(true);

                updateMessage("Vérifiez les détails et confirmez le paiement.",false);
            } else {
                updateMessage("Paiement introuvable.",false);
            }
        } catch (Exception e) {
            updateMessage("Erreur lors de l'opération.",false);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleConfirmPayment() {
        try {
            int id = Integer.parseInt(idField.getText());
            Payment payment = paymentService.getPayment(id);
            if (payment != null) {
                payment.setStatus("payé");
                payment.setPaymentDate(LocalDateTime.now());
                paymentService.updatePayment(payment, id);
                confirmationMessageLabel.setText("Paiement confirmé.Paiement enregistré, merci à la prochaine");
                confirmationMessageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");

                loadPaymentsToTable();

                cardNumberField.clear();
                cardPasswordField.clear();
                cardNumberField.setVisible(false);
                cardPasswordField.setVisible(false);
                confirmButton.setVisible(false);
                cancelButton.setVisible(false);
            } else {
                updateMessage("Paiement introuvable.", false);
            }
        } catch (Exception e) {
            updateMessage("Erreur lors de la confirmation.", false);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancelPayment() {
        cardNumberField.clear();
        cardPasswordField.clear();
        cardNumberField.setVisible(false);
        cardPasswordField.setVisible(false);
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        confirmationMessageLabel.setText("Paiement annulé.");
        confirmationMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
    }
}