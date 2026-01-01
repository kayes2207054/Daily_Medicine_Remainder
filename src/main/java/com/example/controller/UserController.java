package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.GuardianPatientLink;
import com.example.model.Notification;
import com.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * UserController
 * Manages user authentication, registration, and guardian-patient relationships
 */
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final DatabaseManager dbManager;
    private User currentUser;

    public UserController() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing password", e);
            return password; // Fallback (not recommended for production)
        }
    }

    /**
     * Register a new user
     */
    public boolean registerUser(String username, String password, User.Role role, String fullName, String email) {
        // Check if username already exists
        if (dbManager.getUserByUsername(username) != null) {
            logger.warn("Username already exists: {}", username);
            return false;
        }

        String passwordHash = hashPassword(password);
        User user = new User(username, passwordHash, role, fullName, email);
        int id = dbManager.addUser(user);
        
        if (id > 0) {
            logger.info("User registered successfully: {} ({})", username, role);
            return true;
        }
        return false;
    }

    /**
     * Authenticate user login
     */
    public User login(String username, String password) {
        User user = dbManager.getUserByUsername(username);
        if (user != null && user.isActive()) {
            String passwordHash = hashPassword(password);
            if (user.getPasswordHash().equals(passwordHash)) {
                this.currentUser = user;
                logger.info("User logged in: {} ({})", username, user.getRole());
                return user;
            }
        }
        logger.warn("Login failed for username: {}", username);
        return null;
    }

    /**
     * Get currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Get user by ID
     * আইডি দিয়ে ইউজার খুঁজুন
     */
    public User getUserById(int userId) {
        return dbManager.getUserById(userId);
    }

    /**
     * Set current user (for session management)
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
            currentUser = null;
        }
    }

    /**
     * Update user profile
     */
    public boolean updateUserProfile(User user) {
        return dbManager.updateUser(user);
    }

    /**
     * Change user password
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = dbManager.getUserById(userId);
        if (user != null) {
            String oldPasswordHash = hashPassword(oldPassword);
            if (user.getPasswordHash().equals(oldPasswordHash)) {
                user.setPasswordHash(hashPassword(newPassword));
                return dbManager.updateUser(user);
            }
        }
        return false;
    }

    /**
     * Link a guardian to a patient
     */
    public boolean linkGuardianToPatient(int guardianId, int patientId) {
        // Verify guardian is actually a guardian
        User guardian = dbManager.getUserById(guardianId);
        if (guardian == null || !guardian.isGuardian()) {
            logger.error("Invalid guardian ID: {}", guardianId);
            return false;
        }

        // Verify patient is actually a patient
        User patient = dbManager.getUserById(patientId);
        if (patient == null || !patient.isPatient()) {
            logger.error("Invalid patient ID: {}", patientId);
            return false;
        }

        int linkId = dbManager.linkGuardianToPatient(guardianId, patientId);
        if (linkId > 0) {
            logger.info("Guardian {} linked to patient {}", guardianId, patientId);
            return true;
        }
        return false;
    }

    /**
     * Link guardian to patient by username
     */
    public boolean linkGuardianToPatientByUsername(String guardianUsername, String patientUsername) {
        User guardian = dbManager.getUserByUsername(guardianUsername);
        User patient = dbManager.getUserByUsername(patientUsername);
        
        if (guardian != null && patient != null) {
            return linkGuardianToPatient(guardian.getId(), patient.getId());
        }
        return false;
    }

    /**
     * Unlink guardian from patient
     */
    public boolean unlinkGuardianFromPatient(int guardianId, int patientId) {
        return dbManager.unlinkGuardianFromPatient(guardianId, patientId);
    }

    /**
     * Get all patients for a guardian
     */
    public List<GuardianPatientLink> getPatientsForGuardian(int guardianId) {
        return dbManager.getPatientsByGuardianId(guardianId);
    }

    /**
     * Get all guardians for a patient
     */
    public List<GuardianPatientLink> getGuardiansForPatient(int patientId) {
        return dbManager.getGuardiansByPatientId(patientId);
    }

    /**
     * Get patient by username (for guardian lookup)
     */
    public User getPatientByUsername(String username) {
        User user = dbManager.getUserByUsername(username);
        if (user != null && user.isPatient()) {
            return user;
        }
        return null;
    }

    /**
     * Send notification to all guardians of a patient
     */
    public void notifyGuardians(int patientId, Notification.Type type, String message, String details) {
        List<GuardianPatientLink> guardians = dbManager.getGuardiansByPatientId(patientId);
        for (GuardianPatientLink link : guardians) {
            Notification notification = new Notification(link.getGuardianId(), patientId, type, message);
            notification.setDetails(details);
            int notifId = dbManager.addNotification(notification);
            logger.info("Notification sent to guardian {}: {}", link.getGuardianId(), message);
        }
    }

    /**
     * Get notifications for guardian
     */
    public List<Notification> getNotificationsForGuardian(int guardianId) {
        return dbManager.getNotificationsByGuardianId(guardianId);
    }

    /**
     * Get unread notification count
     */
    public int getUnreadNotificationCount(int guardianId) {
        return dbManager.getUnreadNotificationCount(guardianId);
    }

    /**
     * Mark notification as read
     */
    public boolean markNotificationAsRead(int notificationId) {
        return dbManager.markNotificationAsRead(notificationId);
    }

    /**
     * Mark all notifications as read
     */
    public boolean markAllNotificationsAsRead(int guardianId) {
        return dbManager.markAllNotificationsAsRead(guardianId);
    }
}
