package test;

import entities.User;
import entities.Payment;
import services.PaymentService;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Création d'un utilisateur fictif (non utilisé ici, mais utile dans un scénario complet)
        User user = new User(
                "Alice",
                "Smith",
                "alice_smith",
                "alice@example.com",
                "+1234567890",
                "securePassword123",
                "client"
        );

        System.out.println("Utilisateur : " + user);

        PaymentService paymentService = new PaymentService();

        // Création d’un nouveau paiement
        Payment payment = new Payment(
                0,                    // ID (auto-incrémenté)
                2,                    // bookingId
                150.0,                // montant
                null,                // date de paiement (sera définie automatiquement)
                "carte_bancaire",    // méthode de paiement
                "en_attente"         // statut initial
        );

        try {
            // Ajout du paiement
            paymentService.addPayment(payment);

            // Récupération de l'ID généré
            int paymentId = paymentService.getLastInsertedPaymentId();
            payment.setId(paymentId);

            // Simuler un paiement effectué (status = "payé")
            payment.setStatus("payé");

            // La date sera mise à jour automatiquement dans updatePayment() si status == "payé"
            paymentService.updatePayment(payment, paymentId);
            System.out.println("Paiement mis à jour avec succès !");

            // Affichage du statut final
            System.out.println("Statut du paiement : " + paymentService.getStatus(payment));

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'opération de paiement : " + e.getMessage());
        }
    }
}
