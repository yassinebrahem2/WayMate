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

    public static void setInstance(Session instance) {
        Session.instance = instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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