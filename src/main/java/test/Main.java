package test;

import entities.Booking;
import entities.User;
import services.BookingService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Main {
    public static void main(String[] args) {
        // Create a user object using the constructor
        User user = new User(
                "Alice",
                "Smith",
                "alice_smith",
                "alice@example.com",
                "+1234567890",
                "securePassword123",
                "client"
        );

        // Print the user object
        System.out.println(user);
        BookingService bookingService = new BookingService();

        //Booking booking = new Booking(4, "KK987LL", "2026-05-20 12:00:08", "2026-05-20 12:00:08" ,  20.00, "confirmée" );
        Booking updatedBooking = new Booking();
        updatedBooking.setId(1);
        updatedBooking.setUserId(2);
        updatedBooking.setVehicleLicensePlate("CC456DD");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        updatedBooking.setStartTime(LocalDateTime.parse("2025-05-10 10:00:00", formatter));
        updatedBooking.setEndTime(LocalDateTime.parse("2025-05-10 12:00:00", formatter));

        updatedBooking.setTotalPrice(50.0);
        updatedBooking.setStatus("confirmée");
        try {
            //bookingService.addBooking(booking);
            //bookingService.deleteBooking(10);
            bookingService.updateBooking(updatedBooking);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

}
