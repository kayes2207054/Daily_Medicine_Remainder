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
import javafx.scene.layout.GridPane;
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
        
        // Enter key on password field triggers login
        passwordField.setOnAction(e -> handleLogin(new ActionEvent()));
        
        // Set focus on username field when view loads
        javafx.application.Platform.runLater(() -> usernameField.requestFocus());
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
        
        // Create signup dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Account");
        dialog.setHeaderText("Sign Up for DailyDose");
        
        // Create form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Username");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(newUsernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(newPasswordField, 1, 2);
        grid.add(new Label("Confirm:"), 0, 3);
        grid.add(confirmPasswordField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText().trim();
                String username = newUsernameField.getText().trim();
                String password = newPasswordField.getText();
                String confirm = confirmPasswordField.getText();
                
                if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    showError("All fields are required");
                    return;
                }
                
                if (!password.equals(confirm)) {
                    showError("Passwords do not match");
                    return;
                }
                
                boolean success = authController.signup(name, username, password);
                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Account created successfully! Please login.");
                    successAlert.showAndWait();
                } else {
                    showError(authController.getLastError());
                }
            }
        });
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
