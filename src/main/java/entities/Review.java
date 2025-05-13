package entities;

public class Review {;
    private int id;
    private String user_id;
    private String vehicle_license_plate;
    private int rating;
    private String comment;
    private String created_at;


    // Constructors
    public Review() {
    }


    public Review(int id, String user_id, String vehicle_license_plate,
                  int rating, String comment, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.vehicle_license_plate = vehicle_license_plate;
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
    }


    public int getId() { return id; }
    public String getUser_id() { return user_id; }
    public String getVehicle_license_plate() { return vehicle_license_plate; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreated_at() { return created_at; }


    public void setId(int id) { this.id = id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    public void setVehicle_license_plate(String vehicle_license_plate) {
        this.vehicle_license_plate = vehicle_license_plate;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return String.format(
                "Review[id=%d, user_id='%s', vehicle_license_plate='%s', rating=%d, comment='%s', created_at='%s']",
                id, user_id, vehicle_license_plate, rating, comment, created_at
        );
    }
}
