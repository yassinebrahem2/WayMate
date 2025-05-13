package test;

import controllers.UserBookingController;
import entities.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Fetch user info from UserService (assuming user ID 8 for this example)
//        UserService userService = new UserService();
//        User loggedInUser = userService.getUserById(8); // Replace with dynamic user ID
//
//        if (loggedInUser == null) {
//            System.err.println("User not found!");
//            return;
//        }
//
//        // Load the booking user view
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/booking-user-view.fxml"));
//        Parent root = fxmlLoader.load();
//
//        // Retrieve the controller and pass the connected user's ID
//        UserBookingController controller = fxmlLoader.getController();
//        controller.setConnectedUserId(loggedInUser.getId()); // Pass the logged-in user's ID to the controller
// Débogage

        // Set up the scene and show the stage
        FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("/login-view.fxml"));
        Scene scene = new Scene((Parent) fxmlLoader.load(), 1400, 700);
        stage.setTitle("Login!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


