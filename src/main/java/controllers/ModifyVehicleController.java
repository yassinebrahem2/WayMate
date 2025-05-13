package controllers;

import entities.Vehicle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import services.VehicleService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.VehicleService;

import java.io.File;
import java.sql.*;

public class ModifyVehicleController {

    @FXML private TextField licensePlateField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField priceField;
    @FXML private TextField latField;
    @FXML private TextField lngField;
    @FXML private ImageView vehicleImageView;

    private String imagePath;
    private Connection connection;
    private File selectedImageFile;

    private String vehicleToEditLicensePlate;
    private final VehicleService vehicleService = new VehicleService();

    public void setLicensePlateToEdit(String licensePlate) {
        this.vehicleToEditLicensePlate = licensePlate;
        loadVehicleData();
    }

    public void initialize() {
        // Populate ComboBoxes
        typeCombo.getItems().addAll("Voiture", "Moto", "Camion", "Autre");
        statusCombo.getItems().addAll("Disponible", "Indisponible", "En maintenance");
    }


    public void setData(Vehicle v) {
        licensePlateField.setText(v.getLicensePlate());
        brandField.setText(v.getBrand());
        modelField.setText(v.getModel());
        yearField.setText(String.valueOf(v.getYear()));
        statusCombo.setValue(v.getStatus());
        priceField.setText(String.valueOf(v.getPricePerHour()));
        typeCombo.setValue(v.getType());
        latField.setText(String.valueOf(v.getLocationLat()));
        lngField.setText(String.valueOf(v.getLocationLng()));
        vehicleImageView.setImage(new Image(v.getImageUrl()));

    }

    private void loadVehicleData() {
        try {

            String sql = "SELECT * FROM vehicles WHERE license_plate = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, vehicleToEditLicensePlate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                licensePlateField.setText(rs.getString("license_plate"));
                typeCombo.setValue(rs.getString("type"));
                brandField.setText(rs.getString("brand"));
                modelField.setText(rs.getString("model"));
                yearField.setText(rs.getString("year"));
                statusCombo.setValue(rs.getString("status"));
                priceField.setText(rs.getString("price_per_hour"));
                latField.setText(rs.getString("location_lat"));
                lngField.setText(rs.getString("location_lng"));
                imagePath = rs.getString("image_url");
                if (imagePath != null && !imagePath.isEmpty()) {
                    vehicleImageView.setImage(new Image(new File(imagePath).toURI().toString()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @FXML
    public void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de véhicule");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            vehicleImageView.setImage(image);
        }
    }

    @FXML
    public void saveVehicle(ActionEvent event) {
        try {
            // Validation
            if (licensePlateField.getText().isEmpty() ||
                    typeCombo.getValue() == null ||
                    brandField.getText().isEmpty() ||
                    modelField.getText().isEmpty() ||
                    yearField.getText().isEmpty() ||
                    statusCombo.getValue() == null ||
                    priceField.getText().isEmpty() ||
                    latField.getText().isEmpty() ||
                    lngField.getText().isEmpty()) {

                showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            int year = Integer.parseInt(yearField.getText());
            double price = Double.parseDouble(priceField.getText());
            double lat = Double.parseDouble(latField.getText());
            double lng = Double.parseDouble(lngField.getText());

            String imageUrl = selectedImageFile != null ? selectedImageFile.toURI().toString() : null;

            Vehicle vehicle = new Vehicle(
                    licensePlateField.getText(),
                    typeCombo.getValue(),
                    brandField.getText(),
                    modelField.getText(),
                    year,
                    statusCombo.getValue(),
                    price,
                    lat,
                    lng,
                    imageUrl
            );
            vehicleService.deleteVehicle(vehicle.getLicensePlate());
            boolean success = vehicleService.addVehicle(vehicle);



            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Véhicule modifie avec succès !");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setAlwaysOnTop(true);
                stage.close();




            } else {
                showAlert(Alert.AlertType.ERROR, "Échec", "Échec de l'ajout du véhicule.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Année, prix, latitude et longitude doivent être numériques.");
        }
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imagePath = file.getAbsolutePath();
            vehicleImageView.setImage(new Image(file.toURI().toString()));
        }
    }


    @FXML
    private void cancel(javafx.event.ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


