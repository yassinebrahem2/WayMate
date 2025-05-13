package test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import entities.Booking;
import services.BookingService;
import entities.User;
import entities.Payment;
import services.PaymentService;





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


        // Print the user object
        System.out.println(user);
        BookingService bookingService = new BookingService();

        //Booking booking = new Booking(4, "KK987LL", "2026-05-20 12:00:08", "2026-05-20 12:00:08" ,  20.00, "confirmée" );
        Booking updatedBooking = new Booking();
        updatedBooking.setId(1);
        updatedBooking.setUserId(2);
        updatedBooking.setVehicleLicensePlate("CC456DD");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        updatedBooking.setStartTime(LocalDateTime.parse("2025-05-10 10:00:00", formatter));
        updatedBooking.setEndTime(LocalDateTime.parse("2025-05-10 12:00:00", formatter));

        updatedBooking.setTotalPrice(50.0);
        updatedBooking.setStatus("confirmée");
        try {
            //bookingService.addBooking(booking);
            //bookingService.deleteBooking(10);
            bookingService.updateBooking(updatedBooking);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


//       <<<<<<< feature/payments
//         PaymentService paymentService = new PaymentService();

//         // Création d’un nouveau paiement
//         Payment payment = new Payment(
//                 0,                    // ID (auto-incrémenté)
//                 2,                    // bookingId
//                 150.0,                // montant
//                 null,                // date de paiement (sera définie automatiquement)
//                 "carte_bancaire",    // méthode de paiement
//                 "en_attente"         // statut initial
//         );

//         try {
//             // Ajout du paiement
//             paymentService.addPayment(payment);

//             // Récupération de l'ID généré
//             int paymentId = paymentService.getLastInsertedPaymentId();
//             payment.setId(paymentId);

//             // Simuler un paiement effectué (status = "payé")
//             payment.setStatus("payé");

//             // La date sera mise à jour automatiquement dans updatePayment() si status == "payé"
//             paymentService.updatePayment(payment, paymentId);
//             System.out.println("Paiement mis à jour avec succès !");

//             // Affichage du statut final
//             System.out.println("Statut du paiement : " + paymentService.getStatus(payment));

//         } catch (SQLException e) {
//             System.out.println("Erreur lors de l'opération de paiement : " + e.getMessage());
//         }
      
// >>>>>>> feature/bookings
//         // Print the user object
//         System.out.println(user);
//         BookingService bookingService = new BookingService();

//         //Booking booking = new Booking(4, "KK987LL", "2026-05-20 12:00:08", "2026-05-20 12:00:08" ,  20.00, "confirmée" );
//         Booking updatedBooking = new Booking();
//         updatedBooking.setId(1);
//         updatedBooking.setUserId(2);
//         updatedBooking.setVehicleLicensePlate("CC456DD");
//         updatedBooking.setStartTime("2025-05-10 10:00:00");
//         updatedBooking.setEndTime("2025-05-10 12:00:00");
//         updatedBooking.setTotalPrice(50.0);
//         updatedBooking.setStatus("confirmée");
//         try {
//             //bookingService.addBooking(booking);
//             //bookingService.deleteBooking(10);
//             bookingService.updateBooking(updatedBooking);
//         } catch (SQLException e) {
//             System.out.println(e.getMessage());
//         }


    }

}