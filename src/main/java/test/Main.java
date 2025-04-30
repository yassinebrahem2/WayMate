package test;

import entities.User;

public class Main {
    public static void main(String[] args) {
        // Create a user object using the constructor
        User user = new User(
                "Alice",
                "Smith",
                "alice_smith",
                "alice@example.com",
                "+1234567890",
                "securePassword123",
                "client"
        );

        // Print the user object
        System.out.println(user);
    }
}
