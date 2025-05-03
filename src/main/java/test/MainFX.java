package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {


//     public void start(Stage primaryStage) throws Exception {
//         // Load the FXML file
//         FXMLLoader loader = new FXMLLoader(getClass().getResource("/vehicle-main-view.fxml"));
//         Parent root = loader.load();

//         // Create the scene
//         Scene scene = new Scene(root);

//         // Load the CSS file
//         scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());

//         // Set up the stage
//         primaryStage.setTitle("Système de Gestion des Véhicules");
//         primaryStage.setScene(scene);
//         primaryStage.setMaximized(true); // Optional: Start maximized
//         primaryStage.show();

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainFX.class.getResource("/login-view.fxml"));
        Scene scene = new Scene((Parent) fxmlLoader.load());
        stage.setTitle("Login!");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}