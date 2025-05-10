package services;

import entities.Review;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {

    private final Connection connection;

    public ReviewService() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void ajouter(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (user_id, vehicle_license_plate, rating, comment, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, review.getUser_id());
            ps.setString(2, review.getVehicle_license_plate());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.setString(5, review.getCreated_at());
            ps.executeUpdate();
        }
    }

    public List<Review> afficher() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("id"),
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("vehicle_license_plate"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getString("created_at")
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Aucun avis trouvé avec l'ID : " + id);
            }
        }
    }

    public void modifier(Review review) throws SQLException {
        String sql = "UPDATE reviews SET user_id = ?, vehicle_license_plate = ?, rating = ?, comment = ?, created_at = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, review.getUser_id());
            ps.setString(2, review.getVehicle_license_plate());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.setString(5, review.getCreated_at());
            ps.setInt(6, review.getId());
            ps.executeUpdate();
        }
    }
}