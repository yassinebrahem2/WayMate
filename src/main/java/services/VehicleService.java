package services;

import entities.Vehicle;
import utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleService {
    private final Connection connection;

    public VehicleService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Create a new vehicle in the database
    public boolean addVehicle(Vehicle vehicle) {
        String query = "INSERT INTO vehicles (license_plate, type, brand, model, year, status, price_per_hour, location_lat, location_lng, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, vehicle.getLicensePlate());
            pstmt.setString(2, vehicle.getType());
            pstmt.setString(3, vehicle.getBrand());
            pstmt.setString(4, vehicle.getModel());
            pstmt.setInt(5, vehicle.getYear());
            pstmt.setString(6, vehicle.getStatus());
            pstmt.setBigDecimal(7, BigDecimal.valueOf(vehicle.getPricePerHour()));
            pstmt.setBigDecimal(8, BigDecimal.valueOf(vehicle.getLocationLat()));
            pstmt.setBigDecimal(9, BigDecimal.valueOf(vehicle.getLocationLng()));
            pstmt.setString(10, vehicle.getImageUrl());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            return false;
        }
    }

    // Retrieve a vehicle by license plate
    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        String query = "SELECT * FROM vehicles WHERE license_plate = ?";

        try (
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, licensePlate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicle: " + e.getMessage());
        }

        return null;
    }

    // Retrieve all vehicles
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles";

        try (
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicles: " + e.getMessage());
        }

        return vehicles;
    }

    // Update an existing vehicle
    // In VehicleService.java - Fix the parameter indices in updateVehicle
    public boolean updateVehicle(Vehicle vehicle) {
        String query = "UPDATE vehicles SET type = ?, brand = ?, model = ?, year = ?, status = ?, " +
                "price_per_hour = ?, location_lat = ?, location_lng = ?, image = ? " +
                "WHERE license_plate = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, vehicle.getType());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setString(5, vehicle.getStatus());
            pstmt.setBigDecimal(6, BigDecimal.valueOf(vehicle.getPricePerHour()));  // Fixed index
            pstmt.setBigDecimal(7, BigDecimal.valueOf(vehicle.getLocationLat()));   // Fixed index
            pstmt.setBigDecimal(8, BigDecimal.valueOf(vehicle.getLocationLng()));   // Fixed index
            pstmt.setString(9, vehicle.getImageUrl());                             // Fixed index
            pstmt.setString(10, vehicle.getLicensePlate());                        // Fixed index

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            return false;
        }
    }

    // Delete a vehicle by license plate
    public boolean deleteVehicle(String licensePlate) {
        String query = "DELETE FROM vehicles WHERE license_plate = ?";

        try (
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, licensePlate);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            return false;
        }
    }

    // Filter vehicles by type
    public List<Vehicle> getVehiclesByType(String type) {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles WHERE type = ?";

        try (
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, type);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicles by type: " + e.getMessage());
        }

        return vehicles;
    }

    // Filter vehicles by status
    public List<Vehicle> getVehiclesByStatus(String status) {
        List<Vehicle> vehicles = new ArrayList<>();
        String query = "SELECT * FROM vehicles WHERE status = ?";

        try (
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving vehicles by status: " + e.getMessage());
        }

        return vehicles;
    }

    // Helper method to map ResultSet to Vehicle object
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        try {
            vehicle.setLicensePlate(rs.getString("license_plate"));
            vehicle.setType(rs.getString("type"));
            vehicle.setBrand(rs.getString("brand"));
            vehicle.setModel(rs.getString("model"));
            vehicle.setYear(rs.getInt("year"));
            vehicle.setStatus(rs.getString("status"));

            // Convert BigDecimal to double
            BigDecimal price = rs.getBigDecimal("price_per_hour");
            vehicle.setPricePerHour(price != null ? price.doubleValue() : 0.0);

            BigDecimal lat = rs.getBigDecimal("location_lat");
            vehicle.setLocationLat(lat != null ? lat.doubleValue() : 0.0);

            BigDecimal lng = rs.getBigDecimal("location_lng");
            vehicle.setLocationLng(lng != null ? lng.doubleValue() : 0.0);

            vehicle.setImageUrl(rs.getString("image_url"));
        } catch (SQLException e) {
            System.err.println("Error mapping ResultSet to Vehicle: " + e.getMessage());
            throw e;
        }
        return vehicle;
    }
}
