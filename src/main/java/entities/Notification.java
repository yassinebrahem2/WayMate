package entities;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

public class Notification {
    private int id,user_id;
    private String message;
    private Boolean is_read;
    private Timestamp created_at;

    public Notification(){}

    public Notification(int id, int user_id, String message, Boolean is_read, Timestamp created_at) {
        this.id = id;
        this.user_id = user_id;
        this.message = message;
        this.is_read = is_read;
        this.created_at = created_at;
    }
    public Notification(int user_id, String message, Boolean is_read, Timestamp created_at) {
        this.user_id = user_id;
        this.message = message;
        this.is_read = is_read;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIs_read() {
        return is_read;
    }

    public void setIs_read(Boolean is_read) {
        this.is_read = is_read;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", message='" + message + '\'' +
                ", is_read=" + is_read +
                ", created_at=" + created_at +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Notification that)) return false;
        return id == that.id && user_id == that.user_id;
    }

    public int hashCode(){
        int hash=30;
        hash=60+50*this.id;
        return hash;
    }

}
