package com.example.viewfx;

import com.example.DailyDoseApp;
import com.example.controller.UserController;
import com.example.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Login Controller
 * Handles user authentication for both Patient and Guardian roles
 * 
 * লগইন কন্ট্রোলার - রোগী এবং অভিভাবক উভয়ের জন্য সত্যায়ন পরিচালনা করে
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton patientRadio;
    @FXML private RadioButton guardianRadio;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    
    private final UserController userController;
    
    public LoginController() {
        this.userController = new UserController();
    }
    
    @FXML
    private void initialize() {
        logger.info("Login screen initialized");
        
        // Create default users if database is empty
        createDefaultUsers();
        
        // Enter key on password field triggers login
        passwordField.setOnAction(e -> handleLogin());
    }
    
    /**
     * Handle Login Button Click
     * লগইন বাটন ক্লিক পরিচালনা করে
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password!");
            return;
        }
        
        // Attempt login
        User user = userController.login(username, password);
        
        if (user != null && user.isActive()) {
            // Check role matches selection
            boolean isPatientSelected = patientRadio.isSelected();
            boolean userIsPatient = user.isPatient();
            
            if (isPatientSelected != userIsPatient) {
                showError("Invalid role selected! This user is a " + user.getRole());
                passwordField.clear();
                return;
            }
            
            logger.info("Login successful: {} ({})", username, user.getRole());
            
            try {
                // Navigate to appropriate dashboard
                if (user.isPatient()) {
                    DailyDoseApp.showPatientDashboard();
                } else if (user.isGuardian()) {
                    DailyDoseApp.showGuardianDashboard();
                }
            } catch (Exception e) {
                logger.error("Error loading dashboard", e);
                showError("Error loading dashboard: " + e.getMessage());
            }
        } else {
            showError("Invalid username or password!");
            passwordField.clear();
        }
    }
    
    /**
     * Handle Register Link Click
     * নিবন্ধন লিঙ্ক ক্লিক পরিচালনা করে
     */
    @FXML
    private void handleRegister() {
        try {
            DailyDoseApp.showRegistrationScreen();
        } catch (Exception e) {
            logger.error("Error loading registration screen", e);
            showError("Error loading registration screen");
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        // Hide error after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> errorLabel.setVisible(false));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Create default users for testing
     * পরীক্ষার জন্য ডিফল্ট ব্যবহারকারী তৈরি করে
     */
    private void createDefaultUsers() {
        try {
            // Default Patient: admin/admin123
            if (userController.login("admin", "admin123") == null) {
                userController.registerUser("admin", "admin123", User.Role.PATIENT, 
                    "Default Patient", "patient@dailydose.com");
                logger.info("Created default patient user");
            }
            
            // Default Guardian: guardian/guard123
            if (userController.login("guardian", "guard123") == null) {
                userController.registerUser("guardian", "guard123", User.Role.GUARDIAN, 
                    "Default Guardian", "guardian@dailydose.com");
                logger.info("Created default guardian user");
            }
        } catch (Exception e) {
            logger.error("Error creating default users", e);
        }
    }
}
