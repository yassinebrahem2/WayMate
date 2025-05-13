package test;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-view.fxml"));
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
         public static void main (String[] args){
             launch();
         }

}
//ya mohamed zid traitement open cv l tsawer(bch el matricule tetaba wa7dha)