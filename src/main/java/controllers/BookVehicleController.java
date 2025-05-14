package controllers;

import entities.Booking;
import entities.Review;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import entities.Vehicle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.BookingService;
import services.ReviewService;
import services.UserService;
import utils.Session;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookVehicleController {
    @FXML private ImageView vehicleImage;
    @FXML private Label typeLabel;
    @FXML private Label brandLabel;
    @FXML private Label modelLabel;
    @FXML private Label yearLabel;
    @FXML private Label priceLabel;
    @FXML private Label totalPriceLabel;
    @FXML private DatePicker pickupDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private ComboBox<String> pickupHourCombo;
    @FXML private ComboBox<String> pickupMinuteCombo;
    @FXML private ComboBox<String> returnHourCombo;
    @FXML private ComboBox<String> returnMinuteCombo;
    @FXML private VBox reviewsContainer;
    @FXML private TextArea reviewCommentArea;
    @FXML private ToggleGroup ratingGroup;

    private final ReviewService reviewService = new ReviewService();
    private Vehicle currentVehicle;
    private double totalPrice;
    UserService userService = new UserService();

    @FXML
    private void initialize() {
        // Initialize with vehicle data passed from previous screen
        setupDatePickers();
        setupDatePickers();

        if (ratingGroup == null) {
            ratingGroup = new ToggleGroup();
        }
    }

    private void setupDatePickers() {
        // Initialize hour options (00-23)
        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }

        // Initialize minute options (00, 15, 30, 45)
        ObservableList<String> minutes = FXCollections.observableArrayList(
                "00", "15", "30", "45"
        );

        pickupHourCombo.setItems(hours);
        pickupMinuteCombo.setItems(minutes);
        returnHourCombo.setItems(hours);
        returnMinuteCombo.setItems(minutes);

        // Set default values
        pickupHourCombo.getSelectionModel().select("08");
        pickupMinuteCombo.getSelectionModel().select("00");
        returnHourCombo.getSelectionModel().select("17");
        returnMinuteCombo.getSelectionModel().select("00");
        // Set date restrictions and listeners
        pickupDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                returnDatePicker.setDisable(false);
                returnDatePicker.setDayCellFactory(picker -> new DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(date.isBefore(pickupDatePicker.getValue().plusDays(1)));
                    }
                });
                calculateTotalPrice();
            }
        });

        returnDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                calculateTotalPrice();
            }
        });
    }

    public LocalDateTime getPickupDateTime() {
        LocalDate date = pickupDatePicker.getValue();
        String hour = pickupHourCombo.getValue();
        String minute = pickupMinuteCombo.getValue();

        return LocalDateTime.of(date,
                LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute)));
    }

    public LocalDateTime getReturnDateTime() {
        LocalDate date = returnDatePicker.getValue();
        String hour = returnHourCombo.getValue();
        String minute = returnMinuteCombo.getValue();

        return LocalDateTime.of(date,
                LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute)));
    }

    private void calculateTotalPrice() {
        if (pickupDatePicker.getValue() != null && returnDatePicker.getValue() != null) {
            long days = ChronoUnit.DAYS.between(pickupDatePicker.getValue(), returnDatePicker.getValue());
            double pricePerDay = currentVehicle.getPricePerHour() * 24;
            double total = days * pricePerDay;
            totalPriceLabel.setText(String.format("$%.2f", total));
            totalPrice = total;
        }
    }

    @FXML
    private void handleConfirmBooking() throws IOException {
        User currentUser = Session.getInstance().getCurrentUser();
        String startDate = pickupDatePicker.getValue().toString();
        String startHour = pickupHourCombo.getValue();
        String startMinute = pickupMinuteCombo.getValue();
        String endDate = returnDatePicker.getValue().toString();
        String endHour = returnHourCombo.getValue();
        String endMinute = returnMinuteCombo.getValue();

        String fullEndTime = endDate + " " + endHour + ":" + endMinute;
        String fullStartTime = startDate + " " + startHour + ":" + startMinute;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(fullStartTime, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(fullEndTime, formatter);

        Booking booking = new Booking();
        booking.setUserId(currentUser.getId());
        booking.setVehicleLicensePlate(currentVehicle.getLicensePlate());
        booking.setStartTime(startDateTime);
        booking.setEndTime(endDateTime);
        booking.setStatus("en_attente");
        booking.setTotalPrice(totalPrice);

        BookingService bookingService = new BookingService();
        try {
            bookingService.addBooking(booking);
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de contrainte", "L'utilisateur ou le véhicule spécifié n'existe pas dans la base de données.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : " + e.getMessage());
        }

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès !");

        // When proceeding to payment from booking
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-payment-view.fxml"));
        Parent root = loader.load();

        ClientPaymentController paymentController = loader.getController();
        paymentController.setBookingDetails(
                currentVehicle.getBrand() + " " + currentVehicle.getModel(),
                "From " + fullStartTime + " to " + fullEndTime,
                "$" + totalPrice
        );

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Payment");
        stage.show();
    }

    @FXML
    private void handleCancel() {
        // Handle cancel action
    }

    // Helper method to show alert dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private void loadReviews() {
        try {
            List<Review> reviews = reviewService.getReviewsByLicensePlate(currentVehicle.getLicensePlate());
            reviewsContainer.getChildren().clear();

            for (Review review : reviews) {
                reviewsContainer.getChildren().add(createReviewCard(review));
            }

            if (reviews.isEmpty()) {
                Label noReviews = new Label("No reviews yet for this vehicle");
                noReviews.getStyleClass().add("review-comment");
                reviewsContainer.getChildren().add(noReviews);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reviews: " + e.getMessage());
        }
    }

    private Node createReviewCard(Review review) {
        VBox card = new VBox();
        card.getStyleClass().add("review-card");
        card.setSpacing(8);

        // Header with name, rating and date
        HBox header = new HBox();
        header.getStyleClass().add("review-header");

        Label nameLabel = new Label(userService.getUserById(Integer.parseInt(review.getUser_id())).getUsername());
        nameLabel.getStyleClass().add("review-name");

        Label ratingLabel = new Label("★ ".repeat(review.getRating()) + "☆ ".repeat(5 - review.getRating()));
        ratingLabel.getStyleClass().add("review-rating");

        Label dateLabel = new Label(review.getCreated_at());
        dateLabel.getStyleClass().add("review-date");

        HBox.setHgrow(dateLabel, Priority.ALWAYS);
        header.getChildren().addAll(nameLabel, ratingLabel, dateLabel);

        // Comment
        Label commentLabel = new Label(review.getComment());
        commentLabel.getStyleClass().add("review-comment");
        commentLabel.setWrapText(true);

        card.getChildren().addAll(header, commentLabel);
        return card;
    }

    // Update setVehicleData to load reviews when vehicle is set
    public void setVehicleData(Vehicle vehicle) {
        currentVehicle = vehicle;
        //vehicleImage.setImage(new Image(vehicle.getImageUrl()));
        typeLabel.setText(vehicle.getType());
        brandLabel.setText(vehicle.getBrand());
        modelLabel.setText(vehicle.getModel());
        yearLabel.setText(String.valueOf(vehicle.getYear()));
        priceLabel.setText(String.format("$%.2f/hour", vehicle.getPricePerHour()));

        // Load reviews for this vehicle
        if (reviewsContainer != null) {
            loadReviews();
        }
    }


    // New method to handle review submission
    @FXML
    private void handleSubmitReview() {
        try {
            // Get current user
            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "You must be logged in to submit a review");
                return;
            }

            // Validate rating - more robust check
            if (ratingGroup.getSelectedToggle() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a rating");
                return;
            }

            RadioButton selectedRadio = (RadioButton) ratingGroup.getSelectedToggle();
            int rating;
            try {
                rating = Integer.parseInt(selectedRadio.getUserData().toString());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid rating value");
                return;
            }

            // Validate comment
            String comment = reviewCommentArea.getText().trim();
            if (comment.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter your review comments");
                return;
            }

            // Create and save review
            Review review = new Review();
            review.setUser_id(Integer.toString(currentUser.getId()));
            review.setVehicle_license_plate(currentVehicle.getLicensePlate());
            review.setRating(rating);
            review.setComment(comment);

            // Save to database
            reviewService.Add(review);

            // Clear form and refresh reviews
            reviewCommentArea.clear();
            ratingGroup.selectToggle(null);
            loadReviews();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Thank you for your review!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit review: " + e.getMessage());
        }
    }

}