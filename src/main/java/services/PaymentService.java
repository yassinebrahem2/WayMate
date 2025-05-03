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

    private final List<String> validMethods = Arrays.asList("carte_bancaire", "en_espéces", "Paypal");

    public void addPayment(Payment pay, double calculatedAmount) throws SQLException {
        if (!validMethods.contains(pay.getMethod())) {
            System.out.println("Méthode de paiement invalide");
            return;
        }

        if (pay.getAmount() != calculatedAmount) {
            pay.setStatus("échoué");
            System.out.println("Montant incorrect");
        } else {
            pay.setStatus("payé");
            pay.setPaymentDate(LocalDateTime.now());
            System.out.println("Montant accepté");
        }

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
        if ("payé".equals(pay.getStatus())) {
            pay.setPaymentDate(LocalDateTime.now());
        }

        String sql = "UPDATE payments SET amount = ?, payment_date = ?, method = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, pay.getAmount());
            ps.setTimestamp(2, Timestamp.valueOf(pay.getPaymentDate()));
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
                return new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("payment_date").toLocalDateTime(),
                        rs.getString("method"),
                        rs.getString("status")
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
                list.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("payment_date").toLocalDateTime(),
                        rs.getString("method"),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    public String getStatus(Payment pay) {
        return pay.getStatus();
    }

    public double calculatePayment(List<Double> bookingAmounts) {
        if (bookingAmounts == null || bookingAmounts.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (double amount: bookingAmounts) total += amount;

        System.out.println("Montant total à payer: " + total);
        return (total);
    }

    public List<Double> getAmountsByPaymentId(int id) throws SQLException {
        List<Double> amounts = new ArrayList<>();
        String sql = "SELECT amount FROM payments WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                amounts.add(rs.getDouble("amount"));
            }
        }
        return amounts;
    }
}
