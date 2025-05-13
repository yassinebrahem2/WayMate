package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.NotificationService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiqueNotificationController {
    @FXML private Button downloadReportButton;
    @FXML private Button backButton;
    @FXML private Label totalNotificationsLabel;
    @FXML private Label notificationsLuesLabel;
    @FXML private Label notificationsNonLuesLabel;
    @FXML private Label totalPercentageLabel;
    @FXML private Label luesPercentageLabel;
    @FXML private Label nonLuesPercentageLabel;
    @FXML private LineChart<String, Number> notificationChart;
    @FXML private VBox userStatsContainer;
    @FXML private VBox periodStatsContainer;

    private  NotificationService notificationService=new NotificationService();


    @FXML
    public void initialize() {
        try {
            int total = notificationService.getTotalNotifications();
            int lues = notificationService.getNotificationsLues();
            int nonLues = notificationService.getNotificationsNonLues();

            totalNotificationsLabel.setText(String.format("%,d", total));
            notificationsLuesLabel.setText(String.format("%,d", lues));
            notificationsNonLuesLabel.setText(String.format("%,d", nonLues));

            updatePercentages(total, lues, nonLues);
            loadUserStatistics(total);
            loadPeriodStatistics(total);
            setupChart();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Gestion du téléchargement du rapport
        downloadReportButton.setOnAction(this::handleDownloadReport);

        // Gestion du retour
        backButton.setOnAction(this::handleBackButton);
    }
    private void handleDownloadReport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport de notifications");
        fileChooser.setInitialFileName("rapport_notifications_" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));

        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // En-tête du rapport avec style
                writer.println("Rapport des Notifications,");
                writer.println("Généré le," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                writer.println();

                // Section Statistiques Globales
                writer.println("Statistiques Globales,");
                writer.println("Catégorie,Nombre,Pourcentage");
                writer.println("Total Notifications," + totalNotificationsLabel.getText() + "," + totalPercentageLabel.getText().replace("~ ", ""));
                writer.println("Notifications lues," + notificationsLuesLabel.getText() + "," + luesPercentageLabel.getText().replace("~ ", ""));
                writer.println("Notifications non lues," + notificationsNonLuesLabel.getText() + "," + nonLuesPercentageLabel.getText().replace("~ ", ""));
                writer.println();

                // Section par Utilisateur
                writer.println("Répartition par Utilisateur,");
                writer.println("Utilisateur,Nombre,Pourcentage");

                try {
                    Map<String, Integer> userStats = notificationService.getNotificationsByUser();
                    int total = Integer.parseInt(totalNotificationsLabel.getText().replace(",", ""));

                    for (Map.Entry<String, Integer> entry : userStats.entrySet()) {
                        double percentage = total > 0 ? (entry.getValue() * 100.0 / total) : 0;
                        writer.println(entry.getKey() + "," +
                                String.format("%,d", entry.getValue()) + "," +
                                String.format("%.1f%%", percentage));
                    }
                } catch (SQLException e) {
                    showErrorAlert("Erreur de récupération",
                            "Erreur lors de la récupération des données utilisateurs",
                            e.getMessage());
                    return;
                }
                writer.println();

                // Section par Période
                writer.println("Répartition par Période,");
                writer.println("Période,Nombre,Pourcentage");

                try {
                    Map<String, Integer> periodStats = notificationService.getNotificationsByPeriod();
                    int total = Integer.parseInt(totalNotificationsLabel.getText().replace(",", ""));

                    for (Map.Entry<String, Integer> entry : periodStats.entrySet()) {
                        double percentage = total > 0 ? (entry.getValue() * 100.0 / total) : 0;
                        writer.println(entry.getKey() + "," +
                                String.format("%,d", entry.getValue()) + "," +
                                String.format("%.1f%%", percentage));
                    }
                } catch (SQLException e) {
                    showErrorAlert("Erreur de récupération",
                            "Erreur lors de la récupération des données par période",
                            e.getMessage());
                    return;
                }
                writer.println();

                // Section Données Journalières (pour le graphique)
                writer.println("Données Journalières (30 derniers jours),");
                writer.println("Date,Nombre de notifications");

                try {
                    Map<String, Integer> dailyStats = notificationService.getDailyNotifications();
                    dailyStats.forEach((date, count) -> writer.println(date + "," + count));
                } catch (SQLException e) {
                    showErrorAlert("Erreur de récupération",
                            "Erreur lors de la récupération des données journalières",
                            e.getMessage());
                    return;
                }

                // Message de succès
                showSuccessAlert("Rapport généré avec succès",
                        "Le rapport a été enregistré dans:\n" + file.getAbsolutePath());

            } catch (IOException e) {
                showErrorAlert("Erreur d'écriture",
                        "Erreur lors de l'écriture du fichier",
                        e.getMessage());
            }
        }
    }

    // Méthode utilitaire pour afficher les erreurs
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode utilitaire pour afficher les succès
    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Gestion du clic sur le bouton "Retour"
    private void handleBackButton(ActionEvent event) {
        // Fermer la fenêtre actuelle (ou revenir à la vue précédente)
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close(); // Ferme la fenêtre actuelle
    }

    private void updatePercentages(int total, int lues, int nonLues) {
        if (total > 0) {
            totalPercentageLabel.setText(String.format("~ %.1f%%", 100.0));
            luesPercentageLabel.setText(String.format("~ %.1f%%", lues * 100.0 / total));
            nonLuesPercentageLabel.setText(String.format("~ %.1f%%", nonLues * 100.0 / total));
        }
    }

    private void loadUserStatistics(int total) throws SQLException {
        userStatsContainer.getChildren().clear();

        Map<String, Integer> userStats = notificationService.getNotificationsByUser();

        List<Map.Entry<String, Integer>> sortedUsers = userStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());

        int othersCount = 0;

        for (int i = 0; i < sortedUsers.size(); i++) {
            String user = sortedUsers.get(i).getKey();
            int count = sortedUsers.get(i).getValue();

            if (i < 3) {
                double percentage = total > 0 ? (count * 100.0 / total) : 0;
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label userLabel = new Label(user);
                userLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120; -fx-text-fill: black;");

                Label countLabel = new Label(String.format("%,d", count));
                countLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

                Label percentageLabel = new Label(String.format("~ %.1f%%", percentage));
                percentageLabel.setStyle("-fx-text-fill: black;");

                row.getChildren().addAll(userLabel, countLabel, percentageLabel);
                userStatsContainer.getChildren().add(row);
            } else {
                othersCount += count;
            }
        }

        if (othersCount > 0) {
            double otherPercentage = total > 0 ? (othersCount * 100.0 / total) : 0;
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label otherLabel = new Label("Autres");
            otherLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120; -fx-text-fill: black;");

            Label countLabel = new Label(String.format("%,d", othersCount));
            countLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

            Label percentageLabel = new Label(String.format("~ %.1f%%", otherPercentage));
            percentageLabel.setStyle("-fx-text-fill: black;");

            row.getChildren().addAll(otherLabel, countLabel, percentageLabel);
            userStatsContainer.getChildren().add(row);
        }
    }


    private void loadPeriodStatistics(int total) throws SQLException {
        periodStatsContainer.getChildren().clear();
        Map<String, Integer> periodStats = notificationService.getNotificationsByPeriod();

        for (Map.Entry<String, Integer> entry : periodStats.entrySet()) {
            String period = entry.getKey();
            int count = entry.getValue();
            double percentage = total > 0 ? (count * 100.0 / total) : 0;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label periodLabel = new Label(period);
            periodLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120; -fx-text-fill: black;");

            Label countLabel = new Label(String.format("%,d", count));
            countLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

            Label percentageLabel = new Label(String.format("~ %.1f%%", percentage));
            percentageLabel.setStyle("-fx-text-fill: black;");

            row.getChildren().addAll(periodLabel, countLabel, percentageLabel);
            periodStatsContainer.getChildren().add(row);
        }
    }

    private void setupChart() throws SQLException {
        notificationChart.getData().clear();  // clear any previous data

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Notifications");

        Map<String, Integer> dailyStats = notificationService.getDailyNotifications();
        for (Map.Entry<String, Integer> entry : dailyStats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        notificationChart.getData().add(series);

        // Optional: Style the line (JavaFX doesn't always support this by default for dynamically added lines)
        if (!series.getData().isEmpty()) {
            notificationChart.applyCss(); // force style application
        }
    }
}
