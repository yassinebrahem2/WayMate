package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserMotherView.fxml"));
        Parent root = loader.load();

        // Create a responsive scene
        Scene scene = new Scene(root);

        // Create a button to toggle full-screen mode
        Button toggleFullScreenButton = new Button("Toggle Full-Screen");
        toggleFullScreenButton.setOnAction(e -> {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());  // Toggle full-screen state
        });



        // Set scene and show stage
        primaryStage.setTitle("WayMate");
        primaryStage.setScene(scene);

        // Optional: Add keyboard shortcut for toggling full-screen mode (e.g., F11)
        scene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("F11")) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });

        // Show the window
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
