package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import entities.User;
import services.UserService;


public class AdminController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;

    private final UserService userService = new UserService();

    private User loggedInUser;

    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        createdAtColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt()));

        ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsers());
        usersTable.setItems(users);
    }

    public void setLoggedInAdmin(User user) {
        this.loggedInUser = user;

    }
}

