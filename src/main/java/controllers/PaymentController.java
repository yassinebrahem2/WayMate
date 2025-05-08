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
    @FXML private ComboBox<String> statusComboBox;
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
        /*loadPaymentsToTable();*/
        statusComboBox.setItems(FXCollections.observableArrayList("en_attente", "payé", "échoué","en_retard"));
        methodComboBox.setItems(FXCollections.observableArrayList("carte_bancaire","Paypal","en_espéces" ));

        methodComboBox.setOnAction(e -> {
            String selected = methodComboBox.getValue();
            if (selected != null) {
                displayMethodField.setText(selected);
            }
        });
        statusComboBox.setOnAction(e -> {
            String selectedStatus = statusComboBox.getValue();
        });

        toggleCardFields(false);
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

        paymentTableView.setOnMouseClicked(event -> {
            Payment selected = paymentTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                idField.setText(String.valueOf(selected.getId()));
                bookingIdField.setText(String.valueOf(selected.getBookingId()));
                amountField.setText(String.valueOf(selected.getAmount()));

                // Affichage dans le format ISO_LOCAL_DATE_TIME attendu dans handleModifyPayment
                paymentDateField.setText(selected.getPaymentDate() != null ? selected.getPaymentDate().toString() : "");

                statusComboBox.setValue(selected.getStatus());
                methodComboBox.setValue(selected.getMethod());
            }
        });



    }

    private void loadPaymentsToTable() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            ObservableList<Payment> data = FXCollections.observableArrayList(payments);
            paymentTableView.setItems(data);

            // Définition des cellules pour chaque colonne
            idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
            bookingIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getBookingId()).asObject());
            amountColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getAmount()).asObject());

            // Formater la date avec toString() sans formatter explicite
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
        resetConfirmationMessage();
        toggleCardFields(false);


        if (!termsCheckBox.isSelected()) {
            updateMessage("Vous devez accepter les conditions d'utilisation.", false);
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String method = methodComboBox.getValue();

            if (method == null || method.isEmpty()) {
                updateMessage("Veuillez sélectionner une méthode de paiement.", false);
                return;
            }
            if (amount <= 0) {
                updateMessage("Le montant doit être supérieur à 0.", false);
                return;
            }

            String status = statusComboBox.getValue();
            if (status == null || status.isEmpty()) {
                status = "en_attente"; // par défaut
            }

            /*LocalDateTime paymentDate = LocalDateTime.now();*/
            Payment newPayment = new Payment(0, bookingId, amount, null, method,status);
            paymentService.addPayment(newPayment);
            if ("payé".equals(newPayment.getStatus())) {
                newPayment.setPaymentDate(LocalDateTime.now());
            }

            updateMessage("Paiement ajouté avec succès.", true);
            loadPaymentsToTable();
            clearFields();


        } catch (Exception e) {
            updateMessage("Erreur lors de l'ajout du paiement.", false);
            e.printStackTrace();
        }
        clearFields();
    }

    public void handleModifyPayment() {
        toggleCardFields(false);
        resetConfirmationMessage();

        if (!termsCheckBox.isSelected()) {
            updateMessage("Vous devez accepter les conditions d'utilisation.", false);
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String dateStr = paymentDateField.getText();
            String method = methodComboBox.getValue();
            String status = statusComboBox.getValue();

            LocalDateTime newPaymentDate;
            try {
                newPaymentDate = LocalDateTime.parse(dateStr);
            } catch (DateTimeParseException e) {
                updateMessage("Format de date invalide. Utilisez yyyy-MM-ddTHH:mm:ss", false);
                return;
            }

            Payment originalPayment = paymentService.getPayment(id);
            if (originalPayment != null) {
                if (amount <= 0) {
                    updateMessage("Le montant doit être supérieur à 0.", false);
                    return;
                }

                // Vérifie si la date a été modifiée par l’utilisateur
                boolean dateChanged = !newPaymentDate.equals(originalPayment.getPaymentDate());

                // Applique la logique de mise à jour
                originalPayment.setAmount(amount);
                originalPayment.setMethod(method);
                originalPayment.setStatus(status);

                if ("payé".equals(status)) {
                    if (dateChanged) {
                        // L'utilisateur a modifié la date → on garde sa date
                        originalPayment.setPaymentDate(newPaymentDate);
                    } else {
                        // L'utilisateur n'a pas modifié la date → on met la date actuelle
                        originalPayment.setPaymentDate(LocalDateTime.now());
                    }
                } else {
                    // Statut non payé → on garde la date entrée manuellement
                    originalPayment.setPaymentDate(newPaymentDate);
                }

                // Appeler markAsLate ici
                paymentService.markAsLate(originalPayment);

                // Après avoir marqué comme en retard, on met à jour le paiement
                paymentService.updatePayment(originalPayment, id);

                // Appelle les autres fonctions si souhaité
                paymentService.deleteOldPayments();

                updateMessage("Paiement modifié avec succès.", true);
                loadPaymentsToTable();
            } else {
                updateMessage("Paiement introuvable.", false);
            }
        } catch (Exception e) {
            updateMessage("Erreur lors de la modification du paiement.", false);
            e.printStackTrace();
        }

        clearFields();
    }



    private void clearFields() {
        idField.clear();
        bookingIdField.clear();
        amountField.clear();
        paymentDateField.clear();
        statusComboBox.setValue(null);
        methodComboBox.setValue(null);
        termsCheckBox.setSelected(false);
    }
    private void toggleCardFields(boolean visible) {
        cardNumberField.setVisible(visible);
        cardPasswordField.setVisible(visible);
        confirmButton.setVisible(visible);
        cancelButton.setVisible(visible);
    }



    @FXML
    public void handleDeletePayment() {
        resetConfirmationMessage();
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
        clearFields();
    }

    @FXML
    public void handlePayPayment() {
        if (!termsCheckBox.isSelected()) {
            updateMessage("Vous devez accepter les conditions d'utilisation.", false);
            return;
        }

        try {
            // Utilisation de l'ID du paiement et non de l'ID de la réservation
            int paymentId = Integer.parseInt(idField.getText());
            Payment payment = paymentService.getPayment(paymentId);

            if (payment != null) {
                // Calcul du montant à payer basé sur l'ID du paiement
                double totalAmount = paymentService.calculatePayment(paymentId);

                displayAmountField.setText(String.valueOf(totalAmount));
                displayMethodField.setText(payment.getMethod());
                boolean isCard = "carte_bancaire".equals(payment.getMethod());

                cardNumberField.setVisible(isCard);
                cardPasswordField.setVisible(isCard);
                confirmButton.setVisible(true);
                cancelButton.setVisible(true);

                updateMessage("Vérifiez les détails et confirmez le paiement.", false);
            } else {
                updateMessage("Paiement introuvable.", false);
            }
        } catch (Exception e) {
            updateMessage("Erreur lors de l'opération.", false);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleConfirmPayment() {
        try {
            int paymentId = Integer.parseInt(idField.getText());
            Payment payment = paymentService.getPayment(paymentId);
            if ("payé".equals(payment.getStatus())) {
                updateMessage("Le paiement est déjà effectué.", false);
                return;
            }
            boolean isCard = "carte_bancaire".equals(payment.getMethod());
            if (isCard && (cardNumberField.getText().isEmpty() || cardPasswordField.getText().isEmpty())) {
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
                paymentService.updatePayment(payment, paymentId);
                confirmationMessageLabel.setText("Paiement confirmé. Paiement enregistré, merci à la prochaine.");
                confirmationMessageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");

                loadPaymentsToTable();

                cardNumberField.clear();
                cardPasswordField.clear();
                displayMethodField.clear();
                displayAmountField.clear();
                toggleCardFields(false);
                clearFields();
            } else {
                updateMessage("Paiement introuvable.", false);
            }
        } catch (Exception e) {
            updateMessage("Erreur lors de la confirmation.", false);
            e.printStackTrace();
        }
        clearFields();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String paymentId = idField.getText().trim();  // idField au lieu de paymentIdField
        String bookingId = bookingIdField.getText().trim();
        String date = paymentDateField.getText().trim(); // paymentDateField au lieu de dateField
        String amount = amountField.getText().trim();
        String status = statusComboBox.getValue();
        String method = methodComboBox.getValue(); // ComboBox

        try {
            List<Payment> results = paymentService.searchPayments(paymentId, bookingId, date, amount, status, method);
            paymentTableView.setItems(FXCollections.observableArrayList(results));// paymentTableView au lieu de paymentTable
            if (results.isEmpty()) {
                updateMessage("Aucun paiement trouvé.", false);
            }

        } catch (Exception e) {
            updateMessage("Erreur lors de la recherche.", false);
            e.printStackTrace();
        }
    }


    @FXML
    public void handleCancelPayment() {
        try {
            int paymentId = Integer.parseInt(idField.getText());
            Payment payment = paymentService.getPayment(paymentId);
            if ("payé".equals(payment.getStatus())) {
                updateMessage("Le paiement est déjà effectué.", false);
                return;
            }

            if (payment != null) {
                payment.setStatus("échoué");
                paymentService.updatePayment(payment, paymentId);

                cardNumberField.clear();
                cardPasswordField.clear();
                displayAmountField.clear();
                displayMethodField.clear();
                toggleCardFields(false);
                clearFields();
                confirmationMessageLabel.setText("Paiement annulé. Statut mis à 'échoué'.");
                confirmationMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");

                loadPaymentsToTable();
            } else {
                updateMessage("Paiement introuvable pour annulation.", false);
            }
        } catch (Exception e) {
            updateMessage("Erreur lors de l'annulation du paiement.", false);
            e.printStackTrace();
        }
        clearFields();
    }

}