package utils;

import entities.User;

public class Session {
    private static Session instance;
    private User currentUser;

    // Private constructor to prevent direct instantiation
    private Session() {}

    // Get the singleton instance
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // Get the currently logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Set the currently logged-in user
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Clear session data (e.g., on logout)
    public void clear() {
        this.currentUser = null;
    }

    // Optional: Check if someone is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}

