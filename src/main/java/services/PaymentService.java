package services;


import entities.Payment;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentService {
    private Connection conn;

    public PaymentService() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    private static final List<String> validStatuses = Arrays.asList("payé", "en_attente", "échoué", "en_retard");
    private final List<String> validMethods = Arrays.asList("carte_bancaire", "en_espéces", "Paypal");

    public void addPayment(Payment pay) throws SQLException {
        if (!validMethods.contains(pay.getMethod())) {
            System.out.println("Méthode de paiement invalide");
            return;
        }
        if (!validStatuses.contains(pay.getStatus())) {
            System.out.println("Statut de paiement invalide");
            return;
        }
        if (pay.getStatus() == null || pay.getStatus().isEmpty()) {
            pay.setStatus("en_attente"); // Définir un statut par défaut si aucun statut n'est choisi
        }

        pay.setPaymentDate(LocalDateTime.now());

        String sql = "INSERT INTO payments (booking_id, amount, payment_date, method, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            ps.setInt(1, pay.getBookingId());
            ps.setDouble(2, pay.getAmount());
            ps.setTimestamp(3, Timestamp.valueOf(pay.getPaymentDate()));
            ps.setString(4, pay.getMethod());
            ps.setString(5, pay.getStatus());

            ps.executeUpdate();
            conn.commit();
            System.out.println("Paiement enregistré");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Erreur. Paiement annulé.");
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void updatePayment(Payment pay, int id) throws SQLException {
        if (!validStatuses.contains(pay.getStatus())) {
            System.out.println("Statut de paiement invalide");
            return;
        }

        System.out.println("Mise à jour du paiement - ID: " + id);
        System.out.println("Nouveau montant: " + pay.getAmount());
        System.out.println("Nouvelle méthode: " + pay.getMethod());
        System.out.println("Nouveau statut: " + pay.getStatus());
        System.out.println("Nouvelle date de paiement: " + pay.getPaymentDate());

        String sql = "UPDATE payments SET amount = ?, payment_date = ?, method = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, pay.getAmount());
            ps.setTimestamp(2, Timestamp.valueOf(pay.getPaymentDate()));  // Assurez-vous que la date est correcte ici
            ps.setString(3, pay.getMethod());
            ps.setString(4, pay.getStatus());
            ps.setInt(5, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Paiement mis à jour.");
            } else {
                System.out.println("Paiement n'existe pas !");
            }
        }
    }


    public Payment getPayment(int id) throws SQLException {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if (!validStatuses.contains(status)) {
                    System.out.println("Statut de paiement invalide dans la base de données");
                    status = "en_attente";  // Valeur par défaut
                }
                return new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("payment_date").toLocalDateTime(),
                        rs.getString("method"),
                        status
                );
            }
        }
        return null;
    }

    public void deletePayment(int id) throws SQLException {
        String sql = "DELETE FROM payments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Paiement supprimé.");
            } else {
                System.out.println("Paiement n'existe pas !");
            }
        }
    }

    public void deleteOldPayments() throws SQLException {
        LocalDateTime dateLimite = LocalDateTime.now().minusYears(10);
        String sql = "DELETE FROM payments WHERE status = 'payé' AND payment_date < ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(dateLimite));
            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Anciens paiements supprimés : " + rowsDeleted);
            } else {
                System.out.println("Pas d'ancien paiement !");
            }
        }
    }


    public void markAsLate(Payment pay) {
        LocalDate paymentDate = pay.getPaymentDate().toLocalDate();
        long daysPassed = ChronoUnit.DAYS.between(paymentDate, LocalDate.now());

        System.out.println("Days passed: " + daysPassed);
        if (daysPassed > 30 && !"payé".equals(pay.getStatus())) {
            pay.setStatus("en_retard");
            System.out.println("Paiement marqué en retard (ID: " + pay.getId() + ").");

        }
    }

    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                if (!validStatuses.contains(status)) {
                    status = "en_attente";  // Valeur par défaut
                }
                list.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("payment_date")!= null ? rs.getTimestamp("payment_date").toLocalDateTime() : null,
                        rs.getString("method"),
                        status
                ));
            }
        }
        return list;
    }

    public String getStatus(Payment pay) {
        if (!validStatuses.contains(pay.getStatus())) {
            return "en_attente";  // Valeur par défaut
        }
        return pay.getStatus();
    }


    public double calculatePayment(int paymentId) throws SQLException {
        double amount = 0.0;

        String sql = "SELECT amount FROM payments WHERE id = ? AND status != 'payé'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);  // On passe l'id du paiement
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                amount = rs.getDouble("amount");
            }
        }

        System.out.println("Montant à payer pour le paiement ID " + paymentId + ": " + amount);
        return amount;
    }


    public int getLastInsertedPaymentId() throws SQLException {
        String sql = "SELECT MAX(id) AS last_id FROM payments";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        }
        return -1;
    }

    public List<Payment> searchPayments(String paymentId, String bookingId, String date, String amount, String status, String method) {
        List<Payment> results = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM payments WHERE 1=1");

        // Ajouter des conditions de recherche
        if (!paymentId.isEmpty()) query.append(" AND id = ?");
        if (!bookingId.isEmpty()) query.append(" AND booking_id = ?");
        if (!date.isEmpty()) query.append(" AND payment_date >= ? AND payment_date < ?");
        if (!amount.isEmpty()) query.append(" AND amount = ?");
        if (status != null && !status.isEmpty()) query.append(" AND status = ?");
        if (method != null && !method.isEmpty()) query.append(" AND method = ?");

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            int index = 1;

            // Affecter les paramètres à la requête préparée
            if (!paymentId.isEmpty()) stmt.setInt(index++, Integer.parseInt(paymentId));
            if (!bookingId.isEmpty()) stmt.setInt(index++, Integer.parseInt(bookingId));
            if (!date.isEmpty()) {
                LocalDate localDate = LocalDate.parse(date);
                LocalDateTime dateTimeStart = localDate.atStartOfDay();
                LocalDateTime dateTimeEnd = localDate.plusDays(1).atStartOfDay();
                stmt.setTimestamp(index++, Timestamp.valueOf(dateTimeStart));
                stmt.setTimestamp(index++, Timestamp.valueOf(dateTimeEnd));
            }
            if (!amount.isEmpty()) stmt.setDouble(index++, Double.parseDouble(amount));
            if (status != null && !status.isEmpty()) stmt.setString(index++, status);
            if (method != null && !method.isEmpty()) stmt.setString(index++, method);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToPayment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));  // Corrigé, auparavant c'était "payment_id"
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getDouble("amount"));
        Timestamp ts = rs.getTimestamp("payment_date");
        if (ts != null) {
            payment.setPaymentDate(ts.toLocalDateTime());
        }
        payment.setStatus(rs.getString("status"));
        payment.setMethod(rs.getString("method"));  // Assurez-vous que le nom de la colonne est "method" dans la BDD
        return payment;
    }

}
