package controllers;

import entities.Notification;
import entities.User;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.NotificationService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class AfficherNotificationController {

    @FXML private TableView<Notification> tableNotifications;
    @FXML private TableColumn<Notification, String> colNomPrenom;
    @FXML private TableColumn<Notification, String> colMessage;
    @FXML private TableColumn<Notification, Timestamp> colDate;
    @FXML private TableColumn<Notification, String> colRead;
    @FXML private TableColumn<Notification, Void> colActions;
    @FXML private CheckBox checkUnreadOnly;
    @FXML private Button btnRefresh;
    @FXML private Button btnAjouter;
    @FXML private Button btnAfficherStats;
    @FXML private Button btnSendEmail;
    private boolean triAscendant = true;
   @FXML private Button btnSortDate;
   @FXML private TextField searchField;
   private final ContextMenu autoCompletePopup = new ContextMenu();
    private final UserService userService = new UserService();

    private User utilisateurConnecte = new User(2, "Bob", "Martin", null, null, null, null, "admin", null);
    private NotificationService notificationService = new NotificationService();
    private Map<Integer, User> userMap;

    @FXML
    private void initialize() {
        try {
            // Charger tous les utilisateurs dans une Map
            userMap = notificationService.recupererTousLesUtilisateurs();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des utilisateurs").showAndWait();
        }

        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        colRead.setCellValueFactory(data -> {
            String value = data.getValue().getIs_read() ? "Oui" : "Non";
            return new SimpleStringProperty(value);
        });

        // Afficher "Nom Prénom" à partir de la Map des utilisateurs
        colNomPrenom.setCellValueFactory(data -> {
            try {
                User user = userMap.get(data.getValue().getUser_id());
                if (user != null) {
                    return new SimpleStringProperty(user.getFirstName() + " " + user.getLastName());
                }
                return new SimpleStringProperty("Inconnu");
            } catch (Exception e) {
                return new SimpleStringProperty("Inconnu");
            }
        });

        // Bouton dans la colonne Actions
        colActions.setCellFactory(getActionCellFactory());

        // Charger les données
        chargerNotifications();

        // Bouton actualiser
        btnRefresh.setOnAction(e -> {
            chargerNotifications();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Actualisation");
            alert.setHeaderText(null);
            alert.setContentText("Notifications mises à jour.");
            alert.showAndWait();
        });

        if (utilisateurConnecte != null && utilisateurConnecte.getRole().equalsIgnoreCase("admin")) {
            btnAjouter.setManaged(true);  // Le rendre visible uniquement pour l'admin
        } else {
            btnAjouter.setManaged(false);  // Le cacher pour les autres utilisateurs
        }
        btnAjouter.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterNotification.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Ajouter Notification");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir l'interface d'ajout").showAndWait();
            }
        });
        // Afficher le bouton "Statistiques" uniquement pour l'admin
        if (utilisateurConnecte != null && utilisateurConnecte.getRole().equalsIgnoreCase("admin")) {
            btnAfficherStats.setManaged(true);
            btnAfficherStats.setVisible(true);
        } else {
            btnAfficherStats.setManaged(false);
            btnAfficherStats.setVisible(false);
        }
        btnAfficherStats.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/statistiqueNotification.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Statistiques des Notifications");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la fenêtre des statistiques").showAndWait();
            }
        });
        // Gestion du bouton "Envoyer Email"
        if (utilisateurConnecte != null && utilisateurConnecte.getRole().equalsIgnoreCase("admin")) {
            btnSendEmail.setManaged(true);
            btnSendEmail.setVisible(true);

            btnSendEmail.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/emailNotification.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Envoyer un Email");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de l'interface d'email").showAndWait();
                }
            });

        } else {
            btnSendEmail.setManaged(false);
            btnSendEmail.setVisible(false);
        }
        //Tri par date
        btnSortDate.setText("Trier par date ↑");
        //Auto-completion de la notification
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                autoCompletePopup.hide();
                return;
            }

            String search = newText.toLowerCase();

            List<String> suggestions = userMap.values().stream()
                    .map(u -> u.getFirstName() + " " + u.getLastName())
                    .filter(name -> name.toLowerCase().contains(search))
                    .limit(5)
                    .toList();

            if (suggestions.isEmpty()) {
                autoCompletePopup.hide();
            } else {
                List<MenuItem> menuItems = suggestions.stream().map(name -> {
                    MenuItem item = new MenuItem(name);
                    item.setOnAction(e -> {
                        searchField.setText(name);
                        autoCompletePopup.hide();
                        filtrerNotifications(name);
                    });
                    return item;
                }).toList();

                autoCompletePopup.getItems().setAll(menuItems);
                autoCompletePopup.show(searchField, Side.BOTTOM, 0, 0);
            }
        });

        //Filtre par nom ou prenom
        searchField.setOnAction(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            filtrerNotifications(keyword);
        });

        // Filtre non lues
        checkUnreadOnly.setOnAction(e -> chargerNotifications());
    }
    private void filtrerNotifications(String keyword) {
        try {
            List<Notification> notifications;

            if (utilisateurConnecte != null && utilisateurConnecte.getRole().equalsIgnoreCase("admin")) {
                notifications = notificationService.afficher();
            } else if (utilisateurConnecte != null) {
                notifications = notificationService.afficherPourUtilisateur(utilisateurConnecte.getId());
            } else {
                notifications = List.of();
            }

            // Filtrage par lecture si nécessaire
            if (checkUnreadOnly.isSelected()) {
                notifications = notifications.stream()
                        .filter(n -> !n.getIs_read())
                        .toList();
            }

            // Filtrage par mot-clé
            List<Notification> filtres = notifications.stream()
                    .filter(n -> {
                        User user = userMap.get(n.getUser_id());
                        if (user != null) {
                            String nomComplet = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                            return nomComplet.contains(keyword);
                        }
                        return false;
                    })
                    .toList();

            tableNotifications.setItems(FXCollections.observableArrayList(filtres));

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du filtrage").showAndWait();
        }
    }

    public void trierParDate() {
        ObservableList<Notification> items = tableNotifications.getItems();

        FXCollections.sort(items, (n1, n2) -> {
            if (triAscendant) {
                return n1.getCreated_at().compareTo(n2.getCreated_at());
            } else {
                return n2.getCreated_at().compareTo(n1.getCreated_at());
            }
        });

        // Mettre à jour l'icône sur le bouton
        btnSortDate.setText(triAscendant ? "Trier par date ↑" : "Trier par date ↓");

        triAscendant = !triAscendant;
    }

    private void chargerNotifications() {
        try {
            List<Notification> notifications;

            if (utilisateurConnecte != null && utilisateurConnecte.getRole().equalsIgnoreCase("admin")) {
                notifications = notificationService.afficher(); // Toutes les notifications
            } else if (utilisateurConnecte != null) {
                notifications = notificationService.afficherPourUtilisateur(utilisateurConnecte.getId()); // Seulement les siennes
            } else {
                notifications = FXCollections.observableArrayList(); // Aucun utilisateur connecté
            }

            ObservableList<Notification> data;

            if (checkUnreadOnly.isSelected()) {
                data = FXCollections.observableArrayList(
                        notifications.stream().filter(n -> !n.getIs_read()).toList()
                );
            } else {
                data = FXCollections.observableArrayList(notifications);
            }

            // Important : Réassigner la fabrique à chaque rechargement
            tableNotifications.setItems(data);
            colActions.setCellFactory(getActionCellFactory());

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des notifications").showAndWait();
        }
    }

    private Callback<TableColumn<Notification, Void>, TableCell<Notification, Void>> getActionCellFactory() {
        return param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Button btnLire = new Button("📖");
                    Button btnModifier = new Button("✏️");
                    Button btnSupprimer = new Button("🗑");

                    btnLire.setStyle("-fx-font-family: 'Segoe UI Emoji';");
                    btnModifier.setStyle("-fx-font-family: 'Segoe UI Emoji';");
                    btnSupprimer.setStyle("-fx-font-family: 'Segoe UI Emoji';");

                    Notification notif = getTableView().getItems().get(getIndex());

                    // Bouton Lire - Afficher les détails de la notification
                    btnLire.setOnAction(e -> {
                        afficherDetailsNotification(notif);
                        try {
                            // Marquer la notification comme lue
                            notificationService.marquerCommeLue(notif.getId());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });

                    // Bouton Supprimer - Demander confirmation
                    btnSupprimer.setOnAction(e -> {
                        confirmerSuppression(notif);
                    });

                    // Bouton Modifier - Ouvrir un popup prérempli
                    btnModifier.setOnAction(e -> {
                        ouvrirPopupModification(notif);
                    });

                    // Créer un HBox pour contenir les boutons
                    HBox hbox = new HBox(10);
                    hbox.getChildren().add(btnSupprimer);

                    if (utilisateurConnecte != null && "admin".equalsIgnoreCase(utilisateurConnecte.getRole())) {
                        hbox.getChildren().add(btnModifier);
                    } else {
                        hbox.getChildren().add(btnLire);
                    }

                    // Attribuer le HBox à la cellule
                    setGraphic(hbox);
                }
            }
        };
    }

    private void afficherDetailsNotification(Notification notif) {
        try {
            // Marquer comme lue
            notificationService.marquerCommeLue(notif.getId());

            // Réactualiser la table
            notif.setIs_read(true);
            chargerNotifications();

            // Afficher les détails
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Détails de la notification");
            alert.setHeaderText("Message de la notification");
            alert.setContentText("Message: " + notif.getMessage() + "\nDate: " + notif.getCreated_at());
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour du statut de lecture").showAndWait();
        }
    }

    private void confirmerSuppression(Notification notif) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette notification ?");
        alert.setContentText("Cela supprimera définitivement cette notification.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    notificationService.supprimer(notif.getId());
                    chargerNotifications();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Impossible de supprimer").showAndWait();
                }
            }
        });
    }

    private void ouvrirPopupModification(Notification notif) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierNotifications.fxml"));
            Parent root = loader.load();

            ModifierNotificationController controller = loader.getController();
            controller.initialiserAvecNotification(notif);

            // Recharger la table après la modification
            controller.setOnModificationComplete(() -> chargerNotifications());

            Stage stage = new Stage();
            stage.setTitle("Modifier Notification");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de modification").showAndWait();
        }
    }

    @FXML
    private void handleProfileButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-profile-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleVehiclesButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-vehicle-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleReviewsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-reviews-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    @FXML
    private void handleNotificationsButton(ActionEvent event) {
        try {
            // Load the profile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-notifications-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load profile view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
