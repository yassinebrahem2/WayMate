package test;

import entities.Booking;
import entities.User;
import services.BookingService;

import java.sql.SQLException;


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
        updatedBooking.setStartTime("2025-05-10 10:00:00");
        updatedBooking.setEndTime("2025-05-10 12:00:00");
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
