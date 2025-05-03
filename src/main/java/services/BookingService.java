package services;

import entities.Booking;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class BookingService {
    private Connection connection;
    public BookingService() { connection = DatabaseConnection.getInstance().getConnection(); }
    public void addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (user_id, vehicle_license_plate, start_time, end_time, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, booking.getUserId());
        stmt.setString(2, booking.getVehicleLicensePlate());
        stmt.setString(3, booking.getStartTime());
        stmt.setString(4, booking.getEndTime());
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
            b.setStartTime(rs.getString("start_time"));
            b.setEndTime(rs.getString("end_time"));
            b.setTotalPrice(rs.getDouble("total_price"));
            b.setStatus(rs.getString("status"));
            b.setCreatedAt(rs.getString("created_at"));
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
        stmt.setString(3, booking.getStartTime());
        stmt.setString(4, booking.getEndTime());
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






}
