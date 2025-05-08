package entities;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private boolean isRead;
    private String createdAt; // Consider using LocalDateTime in real projects

    // Constructors
    public Notification() {
    }

    public Notification(int id, int userId, String message, boolean isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
        this.isRead = false; // default
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
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
        return "Notification {" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
