package com.example.database;

import com.example.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple file-backed user database using a JSON-like line format per user.
 * Stored at project root as users.txt (one record per line: username|passwordHash).
 */
public class UserDatabase {
    private static final String USERS_FILE = "users.txt";

    public synchronized boolean saveUser(User u) {
        try {
            List<User> all = loadAll();
            for (User existing : all) {
                if (existing.getUsername().equalsIgnoreCase(u.getUsername())) {
                    return false; // already exists
                }
            }
            all.add(u);
            persist(all);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized User getUser(String username) {
        try {
            List<User> all = loadAll();
            for (User u : all) {
                if (u.getUsername().equalsIgnoreCase(username)) {
                    return u;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public synchronized boolean userExists(String username) {
        return getUser(username) != null;
    }

    public synchronized boolean hasAnyUser() {
        try {
            File f = new File(USERS_FILE);
            if (!f.exists()) return false;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                return br.readLine() != null;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private List<User> loadAll() throws Exception {
        List<User> list = new ArrayList<>();
        File f = new File(USERS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    list.add(new User(parts[0], parts[1]));
                }
            }
        }
        return list;
    }

    private void persist(List<User> users) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                bw.write(u.getUsername() + "|" + u.getPasswordHash());
                bw.newLine();
            }
        }
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
