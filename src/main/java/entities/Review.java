package entities;

public class Review {
    private int id;
    private int userId;
    private String vehicleLicensePlate;
    private int rating;           // 1 to 5
    private String comment;
    private String createdAt;     // Ideally use java.time.LocalDateTime

    // Constructors
    public Review() {}

    public Review(int id, int userId, String vehicleLicensePlate, int rating, String comment, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Review(int userId, String vehicleLicensePlate, int rating, String comment) {
        this.userId = userId;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.rating = rating;
        this.comment = comment;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        } else {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // toString
    @Override
    public String toString() {
        return "Review {" +
                "id=" + id +
                ", userId=" + userId +
                ", vehicleLicensePlate='" + vehicleLicensePlate + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
