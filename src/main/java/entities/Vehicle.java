package entities;

public class Vehicle {
    private String licensePlate;
    private String type; // 'voiture', 'vélo', 'trotinette', 'van'
    private String brand;
    private String model;
    private int year;
    private String status; // 'disponible', 'loué', 'maintenance'
    private double pricePerHour;
    private double locationLat;
    private double locationLng;
    private String imageUrl;

    // Constructors
    public Vehicle() {}

    public Vehicle(String licensePlate, String type, String brand, String model, int year, String status,
                   double pricePerHour, double locationLat, double locationLng, String imageUrl) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.status = status;
        this.pricePerHour = pricePerHour;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(double locationLng) {
        this.locationLng = locationLng;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // toString
    @Override
    public String toString() {
        return "Vehicle {" +
                "licensePlate='" + licensePlate + '\'' +
                ", type='" + type + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", status='" + status + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", locationLat=" + locationLat +
                ", locationLng=" + locationLng +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}