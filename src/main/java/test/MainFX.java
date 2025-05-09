package test;

import controllers.*;
import entities.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Fetch user info from UserService (assuming user ID 8 for this example)
        UserService userService = new UserService();
        User loggedInUser = userService.getUserById(8); // Replace with dynamic user ID

        if (loggedInUser == null) {
            System.err.println("User not found!");
            return;
        }

        // Load all four FXML views
        FXMLLoader fxmlLoaderAdminEdit = new FXMLLoader(getClass().getResource("/admin-edit-bookings-view.fxml"));
        Parent adminEditBookingsView = fxmlLoaderAdminEdit.load();
        AdminEditBookingsController adminEditController = fxmlLoaderAdminEdit.getController();
        adminEditController.setConnectedUserId(loggedInUser.getId());

        FXMLLoader fxmlLoaderAdminCalendar = new FXMLLoader(getClass().getResource("/booking-admin-calendar.fxml"));
        Parent bookingAdminCalendarView = fxmlLoaderAdminCalendar.load();
        BookingAdminCalendarController calendarController = fxmlLoaderAdminCalendar.getController();
        calendarController.setConnectedUserId(loggedInUser.getId());

        FXMLLoader fxmlLoaderUserBooking = new FXMLLoader(getClass().getResource("/booking-user-view.fxml"));
        Parent bookingUserView = fxmlLoaderUserBooking.load();
        UserBookingController userBookingController = fxmlLoaderUserBooking.getController();
        userBookingController.setConnectedUserId(loggedInUser.getId());

        FXMLLoader fxmlLoaderUserList = new FXMLLoader(getClass().getResource("/user-booking-list.fxml"));
        Parent userBookingListView = fxmlLoaderUserList.load();
        UserBookingListController userBookingListController = fxmlLoaderUserList.getController();
        userBookingListController.setConnectedUserId(loggedInUser.getId());

        FXMLLoader fxmlLoaderAdminHistorique = new FXMLLoader(getClass().getResource("/AdminHistoriqueBookings.fxml"));
        Parent adminHistoriqueView = fxmlLoaderAdminHistorique.load();
        AdminHistoriqueBookingsController adminHistoriqueBookingsController = fxmlLoaderAdminHistorique.getController();
        adminHistoriqueBookingsController.setConnectedUserId(loggedInUser.getId());

        // Create the main layout with BorderPane
        BorderPane borderPane = new BorderPane();

        // Create navigation menu on the left
        VBox navMenu = new VBox(10); // Spacing between buttons
        navMenu.setStyle("-fx-padding: 10; -fx-background-color: #f0f4f8;");

        Button adminEditBookingsButton = new Button("Admin Edit Bookings");
        adminEditBookingsButton.setOnAction(e -> borderPane.setCenter(adminEditBookingsView));
        adminEditBookingsButton.setMaxWidth(Double.MAX_VALUE);

        Button bookingAdminCalendarButton = new Button("Booking Admin Calendar");
        bookingAdminCalendarButton.setOnAction(e -> borderPane.setCenter(bookingAdminCalendarView));
        bookingAdminCalendarButton.setMaxWidth(Double.MAX_VALUE);

        Button bookingUserButton = new Button("Booking User View");
        bookingUserButton.setOnAction(e -> borderPane.setCenter(bookingUserView));
        bookingUserButton.setMaxWidth(Double.MAX_VALUE);

        Button userBookingListButton = new Button("User Booking List");
        userBookingListButton.setOnAction(e -> borderPane.setCenter(userBookingListView));
        userBookingListButton.setMaxWidth(Double.MAX_VALUE);

        Button adminBookingHistoriqueButton = new Button("Historique Booking");
        adminBookingHistoriqueButton.setOnAction(e -> borderPane.setCenter(adminHistoriqueView));
        adminBookingHistoriqueButton.setMaxWidth(Double.MAX_VALUE);

        navMenu.getChildren().addAll(adminEditBookingsButton, bookingAdminCalendarButton, bookingUserButton, userBookingListButton, adminBookingHistoriqueButton);

        // Set the navigation menu on the left and default view in the center
        borderPane.setLeft(navMenu);
        borderPane.setCenter(adminEditBookingsView); // Default view

        // Set up the scene and show the stage
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("/style.css"); // Apply the CSS file

        stage.setFullScreen(true); // Set to fullscreen
        stage.setFullScreenExitHint(""); // Optional: Remove exit hint
        stage.setTitle("User Booking Dashboard");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0)); // Ensure proper closing
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}