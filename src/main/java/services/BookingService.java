package services;

import entities.Booking;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class BookingService {
    private Connection connection;

    public BookingService() {
        connection = DatabaseConnection.getInstance().getConnection();
    }


    public void addBooking(Booking booking) throws SQLException {
        // Step 1: Retrieve price_per_hour from vehicles table
        String getPriceSql = "SELECT price_per_hour FROM vehicles WHERE license_plate = ?";
        PreparedStatement priceStmt = connection.prepareStatement(getPriceSql);
        priceStmt.setString(1, booking.getVehicleLicensePlate());
        ResultSet priceRs = priceStmt.executeQuery();

        double pricePerHour = 0.0;
        if (priceRs.next()) {
            pricePerHour = priceRs.getDouble("price_per_hour");
        } else {
            throw new SQLException("Vehicle not found: " + booking.getVehicleLicensePlate());
        }

        priceRs.close();
        priceStmt.close();

        // Step 2: Calculate duration
        Duration duration = Duration.between(booking.getStartTime(), booking.getEndTime());
        double durationHours = duration.toMinutes() / 60.0;

        // Step 3: Calculate total price
        double totalPrice = durationHours * pricePerHour;
        booking.setTotalPrice(totalPrice);

        String sql = "INSERT INTO bookings (user_id, vehicle_license_plate, start_time, end_time, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, booking.getUserId());
        stmt.setString(2, booking.getVehicleLicensePlate());
        stmt.setObject(3, booking.getStartTime());
        stmt.setObject(4, booking.getEndTime());

        stmt.setDouble(5, booking.getTotalPrice());
        stmt.setString(6, booking.getStatus());
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Booking b = new Booking();
            b.setId(rs.getInt("id"));
            b.setUserId(rs.getInt("user_id"));
            b.setVehicleLicensePlate(rs.getString("vehicle_license_plate"));
            b.setTotalPrice(rs.getDouble("total_price"));
            b.setStatus(rs.getString("status"));
            b.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            b.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
            b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            bookings.add(b);
        }

        rs.close();
        stmt.close();
        return bookings;
    }

    public void updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET user_id = ?, vehicle_license_plate = ?, start_time = ?, end_time = ?, total_price = ?, status = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, booking.getUserId());
        stmt.setString(2, booking.getVehicleLicensePlate());
        stmt.setObject(3, booking.getStartTime());
        stmt.setObject(4, booking.getEndTime());

        stmt.setDouble(5, booking.getTotalPrice());
        stmt.setString(6, booking.getStatus());
        stmt.setInt(7, booking.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void deleteBooking(int id) throws SQLException {
        // Delete payments first (child table)
        String deletePaymentsSQL = "DELETE FROM payments WHERE booking_id = ?";
        PreparedStatement paymentStmt = connection.prepareStatement(deletePaymentsSQL);
        paymentStmt.setInt(1, id);
        paymentStmt.executeUpdate();
        paymentStmt.close();

        // Then delete booking (parent table)
        String deleteBookingSQL = "DELETE FROM bookings WHERE id = ?";
        PreparedStatement bookingStmt = connection.prepareStatement(deleteBookingSQL);
        bookingStmt.setInt(1, id);
        bookingStmt.executeUpdate();
        bookingStmt.close();
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

        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setVehicleLicensePlate(rs.getString("vehicle_license_plate"));
                booking.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                booking.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());

                booking.setStatus(rs.getString("status"));
                booking.setTotalPrice(rs.getDouble("total_price"));
                bookings.add(booking);
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
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                pricePerHour = resultSet.getDouble("price_per_hour");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pricePerHour;
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, bookingId);

        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            // Assuming your Booking class has a constructor that takes these values
            Booking booking = new Booking(
                    resultSet.getInt("id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("vehicle_license_plate"),
                    resultSet.getTimestamp("start_time"),
                    resultSet.getTimestamp("end_time"),
                    resultSet.getDouble("total_price"),
                    resultSet.getString("status")
            );
            stmt.close();
            return booking;
        } else {
            stmt.close();
            return null; // If no booking found
        }
    }








}
