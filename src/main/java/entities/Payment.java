package entities;

import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int bookingId;
    private double amount;
    private LocalDateTime paymentDate; // Représente la date et l'heure du paiement
    private String method;             // e.g. 'credit_card', 'paypal', 'cash'
    private String status;             // 'payé', 'en_attente', 'échoué', 'en_retard'

    // Constructors
    public Payment() {
    }

    public Payment(int id, int bookingId, double amount, LocalDateTime paymentDate, String method, String status) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.status = status;
    }

    public Payment(int bookingId, double amount, String method, String status) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString
    @Override
    public String toString() {
        return "Payment {" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
