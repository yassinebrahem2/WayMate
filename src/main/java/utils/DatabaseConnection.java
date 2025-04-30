package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final String dbUrl = "jdbc:mysql://localhost:3306/waymate_db";
    private final String dbUsername = "root";
    private final String dbPassword = "";
    private Connection connection;

    private static DatabaseConnection instance;

    // Private constructor to prevent instantiation
    private DatabaseConnection(){
        try{
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Connection established");
        } catch (SQLException ex){
            System.out.println(ex.getMessage());
        }
    }

    // Singleton access
    public static DatabaseConnection getInstance(){
        if(instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Get the database connection
    public Connection getConnection() {
        return connection;
    }

    // Close the connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException ex) {
            System.out.println("Error closing the connection: " + ex.getMessage());
        }
    }
}
