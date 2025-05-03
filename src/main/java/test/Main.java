package test;

import entities.User;
import entities.Payment;
import services.PaymentService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create a user object using the constructor
        User user = new User(
                "Alice",
                "Smith",
                "alice_smith",
                "alice@example.com",
                "+1234567890",
                "securePassword123",
                "client"
        );

        // Print the user object
        System.out.println(user);

        // Create a PaymentService object to interact with the database
        PaymentService paymentService = new PaymentService();

        // Example test data for a Payment object
        Payment payment = new Payment(
                0,
                2,                            // bookingId
                150.0,                        // amount
                LocalDateTime.now(),          // paymentDate (LocalDateTime object)
                "carte_bancaire",             // payment method
                "en_attente"                  // initial status
        );

        // Updating the payment data
        payment.setAmount(150);  // New amount
        payment.setStatus("payé");  // New status
        payment.setPaymentDate(LocalDateTime.now());  // Set current payment date (LocalDateTime)

        try {
            // Try updating the payment
            paymentService.updatePayment(payment, payment.getId());
            System.out.println("Paiement mis à jour avec succès!");

            // Calculate total amount (if needed)
            double calculatedAmount = paymentService.calculatePayment(List.of(150.0));  // Use List of Double

            // Add payment to the database
            paymentService.addPayment(payment, calculatedAmount);

            // Check payment status
            System.out.println("Statut du paiement : " + paymentService.getStatus(payment));
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'opération de paiement : " + e.getMessage());
        }
    }
}
