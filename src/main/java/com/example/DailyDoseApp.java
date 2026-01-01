package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DailyDose - Personal Medicine Companion
 * Main JavaFX Application Class
 * 
 * ব্যক্তিগত ঔষধ সহায়ক অ্যাপ্লিকেশন
 * রোগী এবং অভিভাবক উভয়ের জন্য সম্পূর্ণ ঔষধ ব্যবস্থাপনা
 */
public class DailyDoseApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(DailyDoseApp.class);
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        logger.info("Starting DailyDose Application...");
        
        // Load Login Screen
        showLoginScreen();
        
        stage.setTitle("DailyDose - Personal Medicine Companion");
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.show();
        
        logger.info("Application started successfully");
    }
    
    /**
     * Show Login Screen
     * লগইন স্ক্রিন প্রদর্শন করুন
     */
    public static void showLoginScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(DailyDoseApp.class.getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(DailyDoseApp.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("DailyDose - Login");
    }
    
    /**
     * Show Patient Dashboard
     * রোগীর ড্যাশবোর্ড প্রদর্শন করুন
     */
    public static void showPatientDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(DailyDoseApp.class.getResource("/fxml/PatientDashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(DailyDoseApp.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("DailyDose - Patient Dashboard");
    }
    
    /**
     * Show Guardian Dashboard
     * অভিভাবক ড্যাশবোর্ড প্রদর্শন করুন
     */
    public static void showGuardianDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(DailyDoseApp.class.getResource("/fxml/GuardianDashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(DailyDoseApp.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("DailyDose - Guardian Dashboard");
    }
    
    /**
     * Show Registration Screen
     * নিবন্ধন স্ক্রিন প্রদর্শন করুন
     */
    public static void showRegistrationScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(DailyDoseApp.class.getResource("/fxml/Registration.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 650);
        scene.getStylesheets().add(DailyDoseApp.class.getResource("/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("DailyDose - Register");
    }
    
    /**
     * Get Primary Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
