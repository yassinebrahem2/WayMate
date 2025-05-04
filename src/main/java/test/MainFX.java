package test;

import controllers.UserBookingListController;
import entities.Booking;
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
    public void start(Stage stage) throws Exception {

        // Simulate the logged-in user
        UserService userService = new UserService();
        User loggedInUser = userService.getUserById(4); // Replace with your login logic

        if (loggedInUser == null) {
            System.out.println("User not found.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/user-booking-list.fxml"));
        Parent root = fxmlLoader.load();

        // Get controller after loading
        UserBookingListController controller = fxmlLoader.getController();
        controller.setConnectedUserId(loggedInUser.getId());

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Liste des Réservations");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}