package services;

import entities.Booking;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private Connection connection;

    public BookingService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public void addBooking(Booking booking) throws SQLException {
        // Étape 1: Récupérer le tarif horaire du véhicule
        String getPriceSql = "SELECT price_per_hour FROM vehicles WHERE license_plate = ?";
        try (PreparedStatement priceStmt = connection.prepareStatement(getPriceSql)) {
            priceStmt.setString(1, booking.getVehicleLicensePlate());

            try (ResultSet priceRs = priceStmt.executeQuery()) {
                if (priceRs.next()) {
                    double pricePerHour = priceRs.getDouble("price_per_hour");

                    // Étape 2: Calculer la durée
                    Duration duration = Duration.between(booking.getStartTime(), booking.getEndTime());
                    double durationHours = duration.toMinutes() / 60.0;

                    // Étape 3: Calculer le prix total
                    double totalPrice = durationHours * pricePerHour;
                    booking.setTotalPrice(totalPrice);
                } else {
                    throw new SQLException("Véhicule non trouvé: " + booking.getVehicleLicensePlate());
                }
            }
        }

        // Étape 4: Insérer la réservation
        String sql = "INSERT INTO bookings (user_id, vehicle_license_plate, start_time, end_time, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setString(2, booking.getVehicleLicensePlate());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getStatus());

            stmt.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setVehicleLicensePlate(rs.getString("vehicle_license_plate"));
                b.setTotalPrice(rs.getDouble("total_price"));
                b.setStatus(rs.getString("status"));

                Timestamp startTimestamp = rs.getTimestamp("start_time");
                Timestamp endTimestamp = rs.getTimestamp("end_time");
                Timestamp createdAtTimestamp = rs.getTimestamp("created_at");

                b.setStartTime(startTimestamp != null ? startTimestamp.toLocalDateTime() : null);
                b.setEndTime(endTimestamp != null ? endTimestamp.toLocalDateTime() : null);
                b.setCreatedAt(createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : null);

                bookings.add(b);
            }
        }

        return bookings;
    }

    public void updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET user_id = ?, vehicle_license_plate = ?, start_time = ?, end_time = ?, total_price = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setString(2, booking.getVehicleLicensePlate());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getStatus());
            stmt.setInt(7, booking.getId());

            stmt.executeUpdate();
        }
    }

    public void deleteBooking(int id) throws SQLException {
        // Supprimer d'abord les paiements (table enfant)
        String deletePaymentsSQL = "DELETE FROM payments WHERE booking_id = ?";
        try (PreparedStatement paymentStmt = connection.prepareStatement(deletePaymentsSQL)) {
            paymentStmt.setInt(1, id);
            paymentStmt.executeUpdate();
        }

        // Puis supprimer la réservation (table parent)
        String deleteBookingSQL = "DELETE FROM bookings WHERE id = ?";
        try (PreparedStatement bookingStmt = connection.prepareStatement(deleteBookingSQL)) {
            bookingStmt.setInt(1, id);
            bookingStmt.executeUpdate();
        }
    }

    public boolean deleteBookingById(int id) {
        try {
            deleteBooking(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setUserId(rs.getInt("user_id"));
                    booking.setVehicleLicensePlate(rs.getString("vehicle_license_plate"));

                    Timestamp startTimestamp = rs.getTimestamp("start_time");
                    Timestamp endTimestamp = rs.getTimestamp("end_time");

                    booking.setStartTime(startTimestamp != null ? startTimestamp.toLocalDateTime() : null);
                    booking.setEndTime(endTimestamp != null ? endTimestamp.toLocalDateTime() : null);
                    booking.setStatus(rs.getString("status"));
                    booking.setTotalPrice(rs.getDouble("total_price"));

                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public double getPricePerHourByLicensePlate(String licensePlate) {
        double pricePerHour = 0.0;
        String query = "SELECT price_per_hour FROM vehicles WHERE license_plate = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, licensePlate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    pricePerHour = resultSet.getDouble("price_per_hour");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pricePerHour;
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    // Création de l'objet booking à partir des données
                    Booking booking = new Booking();
                    booking.setId(resultSet.getInt("id"));
                    booking.setUserId(resultSet.getInt("user_id"));
                    booking.setVehicleLicensePlate(resultSet.getString("vehicle_license_plate"));

                    Timestamp startTimestamp = resultSet.getTimestamp("start_time");
                    Timestamp endTimestamp = resultSet.getTimestamp("end_time");

                    booking.setStartTime(startTimestamp.toLocalDateTime());
                    booking.setEndTime(endTimestamp.toLocalDateTime());
                    booking.setTotalPrice(resultSet.getDouble("total_price"));
                    booking.setStatus(resultSet.getString("status"));

                    return booking;
                }
            }
        }

        return null; // Si aucune réservation trouvée
    }

    public void updateBookingDates(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET start_time = ?, end_time = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(2, Timestamp.valueOf(booking.getEndTime()));
            stmt.setInt(3, booking.getId());

            stmt.executeUpdate();
            System.out.println("Réservation ID " + booking.getId() + " mise à jour avec succès.");
        }
    }


    public void updateBookingStatuses() throws SQLException {
        String sql = "UPDATE bookings SET status = CASE " +
                "WHEN start_time > NOW() THEN 'confirmée' " +
                "WHEN end_time > NOW() AND start_time <= NOW() THEN 'en_cours' " +
                "WHEN end_time <= NOW() AND status != 'annulée' THEN 'terminée' " +
                "ELSE status END " +
                "WHERE status IN ('confirmée', 'en_cours') OR " +
                "(status = 'terminée' AND end_time > NOW()) OR " +
                "(status = 'confirmée' AND start_time <= NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int updatedRows = stmt.executeUpdate();
            System.out.println(updatedRows + " réservations ont été mises à jour.");
        }
    }
}