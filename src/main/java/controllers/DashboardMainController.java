package controllers;
import utils.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.DatabaseConnection;

public class DashboardMainController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private VBox sidebar;

    // Statistics Labels
    @FXML private Label totalVehiclesLabel;
    @FXML private Label availableVehiclesLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label newUsersLabel;
    @FXML private Label activeBookingsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label totalReviewsLabel;

    // Charts
    @FXML private PieChart vehicleTypesChart;
    @FXML private LineChart<String, Number> bookingsTimeChart;
    @FXML private BarChart<String, Number> revenueByVehicleChart;
    @FXML private PieChart paymentStatusChart;

    // Database connection
    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Initialize database connection using your singleton
            connection = DatabaseConnection.getInstance().getConnection();

            // Load statistics from database
            loadStatistics();

            // Initialize charts
            initVehicleTypesChart();
            initBookingsTimeChart();
            initRevenueByVehicleChart();
            initPaymentStatusChart();
        } catch (Exception e) {
            e.printStackTrace();
            // If database connection fails, load sample data
            loadSampleData();
        }
    }

    private void loadStatistics() {
        try {
            // Get total vehicles count
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) as total, " +
                            "SUM(CASE WHEN status = 'disponible' THEN 1 ELSE 0 END) as available " +
                            "FROM vehicles"
            );
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalVehiclesLabel.setText(String.valueOf(rs.getInt("total")));
                availableVehiclesLabel.setText(String.valueOf(rs.getInt("available")));
            }

            // Get users count
            stmt = connection.prepareStatement(
                    "SELECT COUNT(*) as total, " +
                            "SUM(CASE WHEN created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 ELSE 0 END) as new_users " +
                            "FROM users"
            );
            rs = stmt.executeQuery();
            if (rs.next()) {
                totalUsersLabel.setText(String.valueOf(rs.getInt("total")));
                newUsersLabel.setText(String.valueOf(rs.getInt("new_users")));
            }

            // Get active bookings and revenue
            stmt = connection.prepareStatement(
                    "SELECT COUNT(*) as active, SUM(total_price) as revenue " +
                            "FROM bookings " +
                            "WHERE status = 'confirmée' OR status = 'en_attente'"
            );
            rs = stmt.executeQuery();
            if (rs.next()) {
                activeBookingsLabel.setText(String.valueOf(rs.getInt("active")));
                double revenue = rs.getDouble("revenue");
                totalRevenueLabel.setText(String.format("€%.2f", revenue));
            }

            // Get average rating and total reviews
            stmt = connection.prepareStatement(
                    "SELECT COUNT(*) as total, AVG(rating) as avg_rating " +
                            "FROM reviews"
            );
            rs = stmt.executeQuery();
            if (rs.next()) {
                totalReviewsLabel.setText(String.valueOf(rs.getInt("total")));
                double avgRating = rs.getDouble("avg_rating");
                avgRatingLabel.setText(String.format("%.1f", avgRating));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // If any query fails, load sample data
            loadSampleData();
        }
    }

    private void loadSampleData() {
        // Vehicle stats
        totalVehiclesLabel.setText("48");
        availableVehiclesLabel.setText("32");

        // User stats
        totalUsersLabel.setText("257");
        newUsersLabel.setText("23");

        // Booking stats
        activeBookingsLabel.setText("15");
        totalRevenueLabel.setText("€3,254");

        // Review stats
        avgRatingLabel.setText("4.3");
        totalReviewsLabel.setText("186");
    }

    private void initVehicleTypesChart() {
        try {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT type, COUNT(*) as count FROM vehicles GROUP BY type"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("count");
                pieChartData.add(new PieChart.Data(type, count));
            }

            vehicleTypesChart.setData(pieChartData);
            vehicleTypesChart.setLabelsVisible(true);
            vehicleTypesChart.setLegendVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            // If query fails, load sample data
            loadSampleVehicleTypesChart();
        }
    }

    private void loadSampleVehicleTypesChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Voiture", 25),
                new PieChart.Data("Vélo", 12),
                new PieChart.Data("Trotinette", 8),
                new PieChart.Data("Van", 3)
        );

        vehicleTypesChart.setData(pieChartData);
        vehicleTypesChart.setLabelsVisible(true);
        vehicleTypesChart.setLegendVisible(true);
    }

    private void initBookingsTimeChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total Bookings");

            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT DATE_FORMAT(created_at, '%b %d') as date, COUNT(*) as total " +
                            "FROM bookings " +
                            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                            "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') " +
                            "ORDER BY created_at"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                int count = rs.getInt("total");
                series.getData().add(new XYChart.Data<>(date, count));
            }

            bookingsTimeChart.getData().add(series);
            bookingsTimeChart.setAnimated(false);
        } catch (SQLException e) {
            e.printStackTrace();
            // If query fails, load sample data
            loadSampleBookingsTimeChart();
        }
    }

    private void loadSampleBookingsTimeChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Bookings");

        // Sample data for last 7 days
        series.getData().add(new XYChart.Data<>("May 8", 12));
        series.getData().add(new XYChart.Data<>("May 9", 15));
        series.getData().add(new XYChart.Data<>("May 10", 10));
        series.getData().add(new XYChart.Data<>("May 11", 18));
        series.getData().add(new XYChart.Data<>("May 12", 20));
        series.getData().add(new XYChart.Data<>("May 13", 16));
        series.getData().add(new XYChart.Data<>("May 14", 15));

        bookingsTimeChart.getData().add(series);
        bookingsTimeChart.setAnimated(false);
    }

    private void initRevenueByVehicleChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Revenue");

            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT v.type, SUM(b.total_price) as revenue " +
                            "FROM bookings b " +
                            "JOIN vehicles v ON b.vehicle_license_plate = v.license_plate " +
                            "WHERE b.status = 'confirmée' OR b.status = 'terminée' " +
                            "GROUP BY v.type"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                double revenue = rs.getDouble("revenue");
                series.getData().add(new XYChart.Data<>(type, revenue));
            }

            revenueByVehicleChart.getData().add(series);
            revenueByVehicleChart.setAnimated(false);
        } catch (SQLException e) {
            e.printStackTrace();
            // If query fails, load sample data
            loadSampleRevenueByVehicleChart();
        }
    }

    private void loadSampleRevenueByVehicleChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weekly Revenue");

        series.getData().add(new XYChart.Data<>("Voiture", 1850));
        series.getData().add(new XYChart.Data<>("Vélo", 720));
        series.getData().add(new XYChart.Data<>("Trotinette", 480));
        series.getData().add(new XYChart.Data<>("Van", 920));

        revenueByVehicleChart.getData().add(series);
        revenueByVehicleChart.setAnimated(false);
    }

    private void initPaymentStatusChart() {
        try {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT status, COUNT(*) as count FROM payments GROUP BY status"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                pieChartData.add(new PieChart.Data(status, count));
            }

            paymentStatusChart.setData(pieChartData);
            paymentStatusChart.setLabelsVisible(true);
            paymentStatusChart.setLegendVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            // If query fails, load sample data
            loadSamplePaymentStatusChart();
        }
    }

    private void loadSamplePaymentStatusChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Payé", 165),
                new PieChart.Data("En attente", 12),
                new PieChart.Data("Échoué", 3),
                new PieChart.Data("En retard", 6)
        );

        paymentStatusChart.setData(pieChartData);
        paymentStatusChart.setLabelsVisible(true);
        paymentStatusChart.setLegendVisible(true);
    }

    // Original navigation methods from your controller

    @FXML
    void openNotificationsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/review-form-admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Reservation");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openPaymentsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/payment-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Payments");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openReservationsAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminHistoriqueBookings.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Reservation");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openUsersTableAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Users");
            stage.setScene(new Scene(root));
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openVehiclesAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Véhicule");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void bringNotif(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichernotification.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Véhicule");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            Stage originalStage = (Stage) mainPane.getScene().getWindow();
            originalStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}