package test;


import controllers.UserBookingController;
import entities.User;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;

public class MainFX extends Application {

//    public void start(Stage primaryStage) throws Exception {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-main.fxml"));
//        BorderPane root = loader.load();
//
//        Scene scene = new Scene(root, 1100, 700);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("WayMate Dashboard");
//        primaryStage.show();
//    }
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Load the FXML file
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-view.fxml"));
//        Scene scene = new Scene(loader.load(), 1100, 700);
//
//        // Set the stage properties
//        primaryStage.setTitle("WayMate Admin Dashboard");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }



      public void start(Stage primaryStage) throws Exception {
          // Load the FXML file
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard-main-view.fxml"));
          Parent root = loader.load();

          // Create the scene
          Scene scene = new Scene(root);

          // Load the CSS file
          scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

          // Set up the stage
          primaryStage.setTitle("Système de Gestion des Véhicules");
          primaryStage.setScene(scene);
          primaryStage.setMaximized(false); // Optional: Start maximized
          primaryStage.setResizable(true);
          primaryStage.show();
         }
//          public static void main (String[] args){
//              launch();
//          }

// }
//ya mohamed zid traitement open cv l tsawer(bch el matricule tetaba wa7dha)

//    @Override
//    public void start(Stage stage) throws IOException {
//        // Fetch user info from UserService (assuming user ID 8 for this example)
////        UserService userService = new UserService();
////        User loggedInUser = userService.getUserById(8); // Replace with dynamic user ID
////
////        if (loggedInUser == null) {
////            System.err.println("User not found!");
////            return;
////        }
////
////        // Load the booking user view
////        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/booking-user-view.fxml"));
////        Parent root = fxmlLoader.load();
////
////        // Retrieve the controller and pass the connected user's ID
////        UserBookingController controller = fxmlLoader.getController();
////        controller.setConnectedUserId(loggedInUser.getId()); // Pass the logged-in user's ID to the controller
//// Débogage
//
//        // Set up the scene and show the stage
//        FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("/login-view.fxml"));
//        stage.setTitle("Login!");
//        stage.setScene(new Scene((Parent) fxmlLoader.load()));
//        stage.show();
//    }

    public static void main(String[] args) {
        launch();
    }
}



