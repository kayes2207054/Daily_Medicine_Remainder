package com.example.controller.fx;

// JavaFX + FXML Migration — Phase 1
// JavaFX Controller for LoginView.fxml

import com.example.controller.AuthController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoginViewController - JavaFX Controller for Login Screen
 * 
 * Migration Notes:
 * - Reuses existing AuthController for business logic
 * - UI components defined in LoginView.fxml
 * - Event handlers reference FXML via @FXML annotations
 * 
 * TODO Phase 2: Navigate to MainDashboard after successful login
 * TODO Phase 3: Show SignupView when signup button is clicked
 * TODO Phase 4: Add input validation with visual feedback
 * TODO Phase 5: Add "Remember Me" checkbox with persistence
 */
public class LoginViewController {
    private static final Logger logger = LoggerFactory.getLogger(LoginViewController.class);
    
    // FXML-injected components (must match fx:id in FXML)
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button signupButton;
    
    // Reuse existing business logic
    private final AuthController authController = new AuthController();
    
    /**
     * Initialize method (called automatically after FXML load)
     */
    @FXML
    public void initialize() {
        logger.info("LoginViewController initialized");
        
        // TODO: Add Enter key listener on password field to trigger login
        // TODO: Set focus on username field
    }
    
    /**
     * Handle Login button click
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Clear previous errors
        errorLabel.setVisible(false);
        
        // Validate inputs (basic)
        if (username == null || username.trim().isEmpty()) {
            showError("Username is required");
            return;
        }
        
        if (password == null || password.isEmpty()) {
            showError("Password is required");
            return;
        }
        
        // Authenticate using existing controller
        boolean success = authController.login(username, password);
        
        if (success) {
            logger.info("Login successful for user: {}", username);
            try {
                // JavaFX + FXML Migration — Phase 2: Navigate to dashboard
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainDashboard.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 700));
                stage.setTitle("DailyDose - Dashboard (JavaFX)");
            } catch (Exception ex) {
                logger.error("Failed to load MainDashboard.fxml", ex);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not open dashboard. Please try again.");
                alert.showAndWait();
            }
            
        } else {
            String error = authController.getLastError();
            showError(error != null ? error : "Invalid credentials");
            logger.warn("Login failed for user: {}", username);
        }
    }
    
    /**
     * Handle Sign Up button click
     */
    @FXML
    private void handleSignup(ActionEvent event) {
        logger.info("Signup button clicked");
        
        // TODO Phase 3: Load SignupView.fxml
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText(null);
        alert.setContentText("Sign up screen coming in Phase 3.\n\nFor now, use the Swing version to create an account.");
        alert.showAndWait();
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
