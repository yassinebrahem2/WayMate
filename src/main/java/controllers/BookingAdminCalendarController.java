package controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import services.BookingService;
import entities.Booking;

import java.time.LocalDateTime;
import java.util.List;

public class BookingAdminCalendarController {

    @FXML
    private Button dayButton, weekButton, monthButton, yearButton;

    @FXML
    private TextField searchField;

    @FXML
    private StackPane calendarStack;

    private final Calendar calendar = new Calendar("Bookings");

    private final CalendarView calendarView = new CalendarView();

    @FXML
    public void initialize() {
        // Set up calendar source
        CalendarSource calendarSource = new CalendarSource("Booking Calendar");
        calendarSource.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(calendarSource);
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);

        // Set default view
        calendarStack.getChildren().add(calendarView);

        calendarView.showDayPage();
        calendarView.showWeekPage();
        calendarView.showMonthPage();
        calendarView.showYearPage();


        // Load bookings
        loadBookings();
    }

    private void loadBookings() {
        calendar.clear(); // Clear existing entries

        BookingService bookingService = new BookingService();
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            for (Booking booking : bookings) {
                // Vérifier si la réservation est confirmée (selon le statut de la réservation)
                if ("confirmée".equalsIgnoreCase(booking.getStatus())) {  // Remplacez "confirmed" par le statut exact utilisé dans votre système
                    LocalDateTime start = booking.getStartTime();
                    LocalDateTime end = booking.getEndTime();

                    Entry<String> entry = new Entry<>(booking.getVehicleLicensePlate());
                    entry.setInterval(start, end);
                    entry.setLocation("Booking ID: " + booking.getId());
                    entry.setTitle("Status: " + booking.getStatus() + "\nPrice: " + booking.getTotalPrice());

                    // Ajouter l'entrée au calendrier
                    calendar.addEntry(entry);

                    // Écouter les modifications d'intervalle
                    entry.intervalProperty().addListener((obs, oldInterval, newInterval) -> {
                        try {
                            Booking updatedBooking = new Booking();
                            updatedBooking.setId(booking.getId());
                            updatedBooking.setStartTime(entry.getStartAsLocalDateTime());
                            updatedBooking.setEndTime(entry.getEndAsLocalDateTime());

                            bookingService.updateBookingDates(updatedBooking); // Méthode à implémenter dans BookingService
                            System.out.println("Booking updated in database: ID = " + booking.getId());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    @FXML
    private void handleAddBooking() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Add Booking");
        alert.setHeaderText(null);
        alert.setContentText("New booking added!");
        alert.showAndWait();

        loadBookings();
    }

    @FXML
    private void handleModifyBooking() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Modify Booking");
        alert.setHeaderText(null);
        alert.setContentText("Booking modified!");
        alert.showAndWait();

        loadBookings();
    }

    @FXML
    private void handleDeleteBooking() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Delete Booking");
        alert.setHeaderText(null);
        alert.setContentText("Booking deleted!");
        alert.showAndWait();

        loadBookings();
    }
}
