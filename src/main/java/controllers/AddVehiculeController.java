package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import entities.Vehicle;
import javafx.stage.Stage;
import services.VehicleService;

import java.io.File;
import java.math.BigDecimal;

public class AddVehiculeController {

    @FXML private TextField licensePlateField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField priceField;
    @FXML private ImageView vehicleImageView;
    @FXML private TextField latField;
    @FXML private TextField lngField;

    private File selectedImageFile;
    private final VehicleService vehicleService = new VehicleService(); // your DAO/service class

    @FXML
    public void initialize() {
        if (typeCombo.getItems().isEmpty()) {
            typeCombo.getItems().addAll("Voiture", "Camion", "Moto");
        }
        if (statusCombo.getItems().isEmpty()) {
            statusCombo.getItems().addAll("Disponible", "En maintenance");
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

            boolean success = vehicleService.addVehicle(vehicle);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Véhicule ajouté avec succès !");
                clearForm();
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
    public void cancel(ActionEvent event) {
        clearForm();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        licensePlateField.clear();
        typeCombo.setValue(null);
        brandField.clear();
        modelField.clear();
        yearField.clear();
        statusCombo.setValue(null);
        priceField.clear();
        latField.clear();
        lngField.clear();
        vehicleImageView.setImage(null);
        selectedImageFile = null;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
