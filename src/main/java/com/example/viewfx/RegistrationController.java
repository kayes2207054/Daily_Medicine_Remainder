package com.example.viewfx;

import com.example.DailyDoseApp;
import com.example.controller.UserController;
import com.example.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registration Controller
 * Handles new user registration
 * 
 * নিবন্ধন কন্ট্রোলার - নতুন ব্যবহারকারী নিবন্ধন পরিচালনা করে
 */
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private RadioButton patientRadio;
    @FXML private RadioButton guardianRadio;
    @FXML private Label messageLabel;
    
    private final UserController userController;
    
    public RegistrationController() {
        this.userController = new UserController();
    }
    
    @FXML
    private void initialize() {
        logger.info("Registration screen initialized");
    }
    
    /**
     * Handle Register Button
     * নিবন্ধন বাটন পরিচালনা করে
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validation
        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            showError("Please fill all required fields!");
            return;
        }
        
        if (username.length() < 3) {
            showError("Username must be at least 3 characters!");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            return;
        }
        
        // Determine role
        User.Role role = patientRadio.isSelected() ? User.Role.PATIENT : User.Role.GUARDIAN;
        
        // Attempt registration
        boolean success = userController.registerUser(username, password, role, fullName, email);
        
        if (success) {
            logger.info("User registered successfully: {} ({})", username, role);
            showSuccess("Registration successful! You can now login.");
            
            // Navigate to login after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            DailyDoseApp.showLoginScreen();
                        } catch (Exception e) {
                            logger.error("Error navigating to login", e);
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            showError("Username already exists! Please choose another.");
        }
    }
    
    /**
     * Handle Back to Login
     */
    @FXML
    private void handleBackToLogin() {
        try {
            DailyDoseApp.showLoginScreen();
        } catch (Exception e) {
            logger.error("Error navigating to login", e);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-background-color: white; -fx-padding: 10px; -fx-background-radius: 6px;");
        messageLabel.setVisible(true);
    }
    
    /**
     * Show success message
     */
    private void showSuccess(String message) {
        messageLabel.setText("✅ " + message);
        messageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-background-color: white; -fx-padding: 10px; -fx-background-radius: 6px;");
        messageLabel.setVisible(true);
    }
}
