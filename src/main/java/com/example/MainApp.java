package com.example;

// JavaFX + FXML Migration — Phase 1
// Entry point for JavaFX version of DailyDose

import com.example.controller.fx.MainDashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MainApp - JavaFX Application Entry Point
 * 
 * Migration Phase 1: Basic JavaFX setup with FXML login screen.
 * 
 * TODO Phase 2: Migrate MainFrame and dashboard
 * TODO Phase 3: Migrate Medicine, Reminder, Inventory, History panels
 * TODO Phase 4: Integrate ReminderService with JavaFX threading
 * TODO Phase 5: Apply CSS styling
 * 
 * NOTE: Existing Swing code (Main.java, LoginFrame, etc.) remains intact
 *       for reference and can be removed after full migration.
 */
public class MainApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static MainApp instance;
    private Stage primaryStage;
    private MainDashboardController dashboardController;

    public MainApp() {
        instance = this;
    }

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            logger.info("Starting JavaFX DailyDose Application");
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            // Create scene
            Scene scene = new Scene(root, 500, 400);
            
            // Configure stage
            primaryStage.setTitle("DailyDose - Login (JavaFX)");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            // Cleanup resources when window closes (stops alarm timer)
            primaryStage.setOnCloseRequest(ev -> {
                logger.info("Application closing, shutting down resources...");
                shutdown();
            });
            
            logger.info("JavaFX application started successfully");
            
        } catch (java.io.IOException e) {
            logger.error("FXML file not found or could not be loaded", e);
            showErrorAlert("Startup Error", "Could not load the login screen.\n\nError: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Failed to start JavaFX application", e);
            showErrorAlert("Startup Error", "Application failed to start.\n\nError: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Show error alert dialog
     */
    private void showErrorAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Called when app window closes to clean up resources.
     * Finds and shuts down the dashboard controller if it exists.
     */
    private void shutdown() {
        if (dashboardController != null) {
            dashboardController.shutdown();
        }
    }

    /**
     * Called by MainDashboardController to register itself for cleanup on shutdown.
     * This allows the main stage to properly stop the alarm timer.
     */
    public void setDashboardController(MainDashboardController controller) {
        this.dashboardController = controller;
    }

    public static void main(String[] args) {
        try {
            // Initialize database (reuse existing logic)
            com.example.utils.DataSeeder.seedSampleData();
            logger.info("Database initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            System.err.println("Database initialization error: " + e.getMessage());
        }
        
        // Launch JavaFX
        launch(args);
    }
}
