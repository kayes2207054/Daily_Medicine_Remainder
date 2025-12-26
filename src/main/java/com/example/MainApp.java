package com.example;

// JavaFX + FXML Migration — Phase 1
// Entry point for JavaFX version of DailyDose

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

    @Override
    public void start(Stage primaryStage) {
        try {
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
            
            logger.info("JavaFX application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start JavaFX application", e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Initialize database (reuse existing logic)
        com.example.utils.DataSeeder.seedSampleData();
        
        // Launch JavaFX
        launch(args);
    }
}
