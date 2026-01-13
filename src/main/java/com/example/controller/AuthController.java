package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.User;

public class AuthController {
    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private String lastError;
    private static User currentUser;

    public boolean register(String username, String password, String confirmPassword, String fullName) {
        lastError = null;
        if (username == null || username.trim().length() < 3) {
            lastError = "Username must be at least 3 characters.";
            return false;
        }
        if (password == null || password.length() < 4) { // Simplified requirement
            lastError = "Password must be at least 4 characters.";
            return false;
        }
        if (!password.equals(confirmPassword)) {
            lastError = "Passwords do not match.";
            return false;
        }
        
        // Check if user exists (simplification: try register and catch unique constraint or check first)
        // Since we didn't add userExists method, we rely on register failing or we should add it.
        // Better: Try to register.
        
        User u = new User(username.trim(), password, User.ROLE_PATIENT, fullName);
        if (dbManager.registerUser(u)) {
            return true;
        } else {
            lastError = "Registration failed. Username might be taken.";
            return false;
        }
    }

    public boolean login(String username, String password) {
        lastError = null;
        if (username == null || username.trim().isEmpty()) {
            lastError = "Username required";
            return false;
        }
        
        User u = dbManager.authenticateUser(username.trim(), password);
        if (u != null) {
            currentUser = u;
            return true;
        }

        lastError = "Invalid username or password";
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public String getLastError() {
        return lastError;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
