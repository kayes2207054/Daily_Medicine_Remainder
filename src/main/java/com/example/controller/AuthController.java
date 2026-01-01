package com.example.controller;

import com.example.database.UserDatabase;
import com.example.model.User;

public class AuthController {
    private final UserDatabase userDb = new UserDatabase();
    private String lastError;
    private static User currentUser;

    public boolean isFirstTimeUser() {
        return !userDb.hasAnyUser();
    }

    public boolean register(String username, String password, String confirmPassword) {
        lastError = null;
        if (username == null || username.trim().length() < 3) {
            lastError = "Username must be at least 3 characters.";
            return false;
        }
        if (password == null || password.length() < 6) {
            lastError = "Password must be at least 6 characters.";
            return false;
        }
        if (!password.equals(confirmPassword)) {
            lastError = "Confirm password must match.";
            return false;
        }
        if (userDb.userExists(username)) {
            lastError = "Username already exists.";
            return false;
        }
        String hash = UserDatabase.sha256(password);
        User u = new User(username.trim(), hash);
        boolean ok = userDb.saveUser(u);
        if (!ok) {
            lastError = "Failed to save user.";
        }
        return ok;
    }

    public boolean signup(String name, String username, String password) {
        // Simplified signup for JavaFX (name parameter ignored for now)
        return register(username, password, password);
    }

    public boolean login(String username, String password) {
        lastError = null;
        if (username == null || username.trim().isEmpty()) {
            lastError = "Username required";
            return false;
        }
        if (password == null || password.length() == 0) {
            lastError = "Password required";
            return false;
        }

        User u = userDb.getUser(username.trim());
        if (u == null) {
            lastError = "User not found";
            return false;
        }

        String hash = UserDatabase.sha256(password);
        if (hash != null && hash.equals(u.getPasswordHash())) {
            currentUser = u;
            return true;
        }

        lastError = "Invalid password";
        return false;
    }

    public String getLastError() {
        return lastError;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
