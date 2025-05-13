package test;

import controllers.*;
import entities.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.UserService;
import utils.Session;

import java.io.IOException;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Fetch current user from Session
        User loggedInUser = Session.getInstance().getCurrentUser();
        UserService userService = new UserService();

        // If no user is in Session, fetch user with ID 7 for testing
        if (loggedInUser == null) {
            loggedInUser = userService.getUserById(7); // Test with user_id = 7
            if (loggedInUser == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur avec ID 7 non trouvé ! Vérifiez la base de données.");
                return;
            }
            Session.getInstance().setCurrentUser(loggedInUser);
            System.out.println("Initialized with test user ID: 7");
        } else {
            System.out.println("Initialized with current user ID: " + loggedInUser.getId());
        }

        // Load all views with error handling
        Parent adminEditBookingsView = loadView("/admin-edit-bookings-view.fxml", stage, "Admin Edit Bookings");
        if (adminEditBookingsView != null) {
            AdminEditBookingsController adminEditController = (AdminEditBookingsController) stage.getProperties().get("controller");
            adminEditController.setConnectedUserId(loggedInUser.getId());
        }

        Parent bookingAdminCalendarView = loadView("/booking-admin-calendar.fxml", stage, "Booking Admin Calendar");
        if (bookingAdminCalendarView != null) {
            BookingAdminCalendarController calendarController = (BookingAdminCalendarController) stage.getProperties().get("controller");
            calendarController.setConnectedUserId(loggedInUser.getId());
        }

        Parent bookingUserView = loadView("/booking-user-view.fxml", stage, "Booking User View");
        if (bookingUserView != null) {
            UserBookingController userBookingController = (UserBookingController) stage.getProperties().get("controller");
            // UserBookingController doesn't have setConnectedUserId; initialize manually if needed
            // userBookingController.setConnectedUserId(loggedInUser.getId()); // Uncomment if added
        }

        Parent userBookingListView = loadView("/user-booking-list.fxml", stage, "User Booking List");
        if (userBookingListView != null) {
            UserBookingListController userBookingListController = (UserBookingListController) stage.getProperties().get("controller");
            userBookingListController.setConnectedUserId(loggedInUser.getId());
        }

        Parent adminHistoriqueView = loadView("/AdminHistoriqueBookings.fxml", stage, "Historique Booking");
        if (adminHistoriqueView != null) {
            AdminHistoriqueBookingsController adminHistoriqueController = (AdminHistoriqueBookingsController) stage.getProperties().get("controller");
            adminHistoriqueController.setConnectedUserId(loggedInUser.getId());
        }

        // Create the main layout with BorderPane
        BorderPane borderPane = new BorderPane();

        // Create navigation menu on the left
        VBox navMenu = new VBox(10);
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

        // Add a logout button
        Button logoutButton = new Button("Déconnexion");
        logoutButton.setOnAction(e -> handleLogout(stage));
        logoutButton.setMaxWidth(Double.MAX_VALUE);

        navMenu.getChildren().addAll(adminEditBookingsButton, bookingAdminCalendarButton, bookingUserButton, userBookingListButton, adminBookingHistoriqueButton, logoutButton);

        // Set the navigation menu on the left and default view in the center
        borderPane.setLeft(navMenu);
        borderPane.setCenter(adminEditBookingsView);

        // Set up the scene and show the stage
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("/style.css");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Appuyez sur ESC pour quitter le mode plein écran");
        stage.setTitle("User Booking Dashboard");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }

    private Parent loadView(String fxmlPath, Stage stage, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            stage.getProperties().put("controller", loader.getController());
            return view;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger la vue : " + viewName);
            return null;
        }
    }

    private void handleLogout(Stage stage) {
        Session.getInstance().setCurrentUser(null); // Clear session
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml")); // Replace with your login FXML
            Parent loginView = loader.load();
            Scene loginScene = new Scene(loginView);
            loginScene.getStylesheets().add("/style.css");
            stage.setFullScreen(false);
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la vue de connexion.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}