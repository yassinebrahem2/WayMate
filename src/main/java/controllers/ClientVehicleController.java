package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.geometry.*;
import javafx.collections.*;
import java.util.List;


import entities.Vehicle;
import services.VehicleService;


public class ClientVehicleController {
    @FXML private FlowPane vehicleCardsContainer;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> priceFilter;
    @FXML private TextField searchField;

    VehicleService vehicleService = new VehicleService();
    List<Vehicle> vehicles;

    @FXML
    private void initialize() {
        this.vehicles = vehicleService.getAllVehicles();
        generateVehicleCards();
    }

    @FXML
    private void applyFilters() {
        String type = typeFilter.getValue();
        if (!typeFilter.getValue().equals("All Types")) {
            this.vehicles = vehicleService.getVehiclesByType(type);
        } else {
            this.vehicles = vehicleService.getAllVehicles();
        }


        generateVehicleCards(); // Regenerate based on filters
    }

    private void generateVehicleCards() {
        vehicleCardsContainer.getChildren().clear();



        for (Vehicle vehicle : vehicles) {
            VBox card = createVehicleCard(vehicle);
            vehicleCardsContainer.getChildren().add(card);
        }
    }

    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox();
        card.getStyleClass().add("vehicle-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(10);

        // Vehicle Image
        ImageView imageView = new ImageView(new Image(vehicle.getImageUrl()));
        imageView.getStyleClass().add("vehicle-image");
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Vehicle Name
        Label nameLabel = new Label(vehicle.getModel());
        nameLabel.getStyleClass().add("vehicle-name");

        // Vehicle Details
        Label detailsLabel = new Label(vehicle.getType());
        detailsLabel.getStyleClass().add("vehicle-details");

        // Vehicle Price
        Label priceLabel = new Label(String.format("$%.2f/hour", vehicle.getPricePerHour()));
        priceLabel.getStyleClass().add("vehicle-price");

        // Book Button
        Button bookButton = new Button("BOOK NOW");
        bookButton.getStyleClass().add("book-button");
        bookButton.setOnAction(e -> bookVehicle(vehicle));

        card.getChildren().addAll(imageView, nameLabel, detailsLabel, priceLabel, bookButton);
        return card;
    }

    @FXML
    private void bookVehicle(Vehicle vehicle) {
        try {
            // Load the booking view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book-vehicle-view.fxml"));
            Parent root = loader.load();

            // Get the booking controller and pass the vehicle data
            BookVehicleController bookingController = loader.getController();
            bookingController.setVehicleData(vehicle);

            // Create new scene and stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Book Vehicle");
            stage.show();

            // Optional: Close the current window if needed
            // ((Stage) bookButton.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            // Show error alert
        }
    }

}