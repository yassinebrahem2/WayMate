package entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private String vehicleLicensePlate;
    private LocalDateTime startTime; // Use java.time.LocalDateTime in real apps
    private LocalDateTime endTime;
    private double totalPrice;
    private String status; // 'en_attente', 'confirmée', 'annulée', 'terminée'
    private LocalDateTime createdAt;
    private int connectedUserId;

    public Booking(int id, int userId, String vehicleLicensePlate, Timestamp startTime, Timestamp endTime, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.startTime = startTime.toLocalDateTime();
        this.endTime = endTime.toLocalDateTime();
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = LocalDateTime.now();



    }

    public int getConnectedUserId() {
        return connectedUserId;
    }

    public void setConnectedUserId(int connectedUserId) {
        this.connectedUserId = connectedUserId;
    }

    // Constructors
    public Booking() {
    }

    public Booking(int id, int userId, String vehicleLicensePlate, LocalDateTime startTime, LocalDateTime endTime,
                   double totalPrice, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;

    }

    public Booking(int userId, String vehicleLicensePlate, LocalDateTime startTime, LocalDateTime endTime,
                   double totalPrice, String status) {
        this.userId = userId;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString
    @Override
    public String toString() {
        return "Booking {" +
                "id=" + id +
                ", userId=" + userId +
                ", vehicleLicensePlate='" + vehicleLicensePlate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }


}
