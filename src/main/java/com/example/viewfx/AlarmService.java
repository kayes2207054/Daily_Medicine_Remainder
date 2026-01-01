package com.example.viewfx;

import com.example.controller.ReminderController;
import com.example.controller.UserController;
import com.example.model.Notification;
import com.example.model.Reminder;
import com.example.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Alarm Service for JavaFX
 * Checks reminders every second and triggers alarm popups with sound
 * 
 * জাভাএফএক্স অ্যালার্ম সার্ভিস
 * প্রতি সেকেন্ডে রিমাইন্ডার চেক করে এবং শব্দ সহ অ্যালার্ম পপআপ ট্রিগার করে
 */
public class AlarmService {
    private static final Logger logger = LoggerFactory.getLogger(AlarmService.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    
    private final ReminderController reminderController;
    private final UserController userController;
    private Timeline timeline;
    private final Set<Integer> alertedReminderIds;
    private MediaPlayer alarmPlayer;
    
    public AlarmService(ReminderController reminderController, UserController userController) {
        this.reminderController = reminderController;
        this.userController = userController;
        this.alertedReminderIds = new HashSet<>();
    }
    
    /**
     * Start alarm checking service
     * অ্যালার্ম চেকিং সার্ভিস শুরু করুন
     */
    public void start() {
        if (timeline != null) {
            timeline.stop();
        }
        
        // Check every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> checkReminders()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        logger.info("Alarm service started - checking every second");
    }
    
    /**
     * Stop alarm service
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        if (alarmPlayer != null) {
            alarmPlayer.stop();
        }
        logger.info("Alarm service stopped");
    }
    
    /**
     * Check for due reminders
     * নির্ধারিত সময়ের রিমাইন্ডার চেক করুন
     */
    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> dueReminders = reminderController.getDueReminders(now);
        
        // Clean up alerted list for non-pending reminders
        alertedReminderIds.removeIf(id -> {
            Reminder r = reminderController.getReminderById(id);
            return r == null || r.getStatus() != Reminder.Status.PENDING;
        });
        
        // Show alarm for due reminders
        for (Reminder reminder : dueReminders) {
            if (!alertedReminderIds.contains(reminder.getId())) {
                alertedReminderIds.add(reminder.getId());
                Platform.runLater(() -> showAlarmDialog(reminder));
            }
        }
    }
    
    /**
     * Show alarm dialog with sound
     * শব্দ সহ অ্যালার্ম ডায়ালগ প্রদর্শন করুন
     */
    private void showAlarmDialog(Reminder reminder) {
        // Play alarm sound
        playAlarmSound();
        
        // Create alarm window
        Stage alarmStage = new Stage();
        alarmStage.initModality(Modality.APPLICATION_MODAL);
        alarmStage.initStyle(StageStyle.UTILITY);
        alarmStage.setTitle("⏰ MEDICINE REMINDER");
        alarmStage.setAlwaysOnTop(true);
        
        // Content
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b);");
        
        // Title
        Label titleLabel = new Label("⏰ TIME TO TAKE YOUR MEDICINE!");
        titleLabel.setFont(Font.font("System Bold", 22));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        // Medicine name
        Label medicineLabel = new Label("Medicine: " + reminder.getMedicineName());
        medicineLabel.setFont(Font.font(18));
        medicineLabel.setStyle("-fx-text-fill: white;");
        
        // Time
        String timeStr = reminder.getReminderTime() != null ? 
            reminder.getReminderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "now";
        Label timeLabel = new Label("Time: " + timeStr);
        timeLabel.setFont(Font.font(16));
        timeLabel.setStyle("-fx-text-fill: #ecf0f1;");
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button takenButton = createAlarmButton("✓ TAKEN", "#2ecc71", "#27ae60");
        takenButton.setOnAction(e -> {
            stopAlarmSound();
            handleTaken(reminder);
            alarmStage.close();
        });
        
        Button missedButton = createAlarmButton("✗ MISSED", "#e74c3c", "#c0392b");
        missedButton.setOnAction(e -> {
            stopAlarmSound();
            handleMissed(reminder);
            alarmStage.close();
        });
        
        Button stopButton = createAlarmButton("⏹ STOP ALARM", "#34495e", "#2c3e50");
        stopButton.setOnAction(e -> {
            stopAlarmSound();
            alertedReminderIds.remove(reminder.getId());
            alarmStage.close();
        });
        
        buttonBox.getChildren().addAll(takenButton, missedButton, stopButton);
        
        content.getChildren().addAll(titleLabel, medicineLabel, timeLabel, buttonBox);
        
        // Window close handler
        alarmStage.setOnCloseRequest(e -> {
            stopAlarmSound();
            alertedReminderIds.remove(reminder.getId());
        });
        
        Scene scene = new Scene(content, 550, 300);
        alarmStage.setScene(scene);
        alarmStage.show();
        
        logger.info("Alarm shown for: {}", reminder.getMedicineName());
    }
    
    /**
     * Create styled alarm button
     */
    private Button createAlarmButton(String text, String color1, String color2) {
        Button button = new Button(text);
        button.setFont(Font.font("System Bold", 14));
        button.setPrefWidth(150);
        button.setPrefHeight(45);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + ");" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: " + color2 + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-scale-x: 1.05;" +
            "-fx-scale-y: 1.05;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + ");" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        
        return button;
    }
    
    /**
     * Handle TAKEN action
     * ঔষধ গ্রহণ করা হয়েছে
     */
    private void handleTaken(Reminder reminder) {
        reminderController.markTaken(reminder.getId());
        logger.info("Reminder marked TAKEN: {}", reminder.getMedicineName());
        
        // Notify guardians
        notifyGuardians(reminder, "TAKEN");
    }
    
    /**
     * Handle MISSED action
     * ঔষধ মিস হয়েছে
     */
    private void handleMissed(Reminder reminder) {
        reminderController.markMissed(reminder.getId());
        logger.info("Reminder marked MISSED: {}", reminder.getMedicineName());
        
        // Notify guardians
        notifyGuardians(reminder, "MISSED");
    }
    
    /**
     * Notify guardians about patient action
     * রোগীর কাজ সম্পর্কে অভিভাবকদের অবহিত করুন
     */
    private void notifyGuardians(Reminder reminder, String action) {
        User currentUser = userController.getCurrentUser();
        if (currentUser != null && currentUser.isPatient()) {
            Notification.Type type = action.equals("TAKEN") ? 
                Notification.Type.DOSE_TAKEN : Notification.Type.DOSE_MISSED;
            
            String message = currentUser.getFullName() + " " + 
                (action.equals("TAKEN") ? "took" : "missed") + " " + 
                reminder.getMedicineName();
            
            String details = "Time: " + (reminder.getReminderTime() != null ? 
                reminder.getReminderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "now");
            
            userController.notifyGuardians(currentUser.getId(), type, message, details);
        }
    }
    
    /**
     * Play alarm sound
     * অ্যালার্ম শব্দ চালান
     */
    private void playAlarmSound() {
        try {
            // Use system beep as fallback
            java.awt.Toolkit.getDefaultToolkit().beep();
            
            // Try to play alarm sound file if exists
            /* Uncomment if you have an alarm.mp3 file in resources/sounds/
            String soundFile = getClass().getResource("/sounds/alarm.mp3").toExternalForm();
            Media sound = new Media(soundFile);
            alarmPlayer = new MediaPlayer(sound);
            alarmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            alarmPlayer.setVolume(1.0);
            alarmPlayer.play();
            */
        } catch (Exception e) {
            logger.warn("Could not play alarm sound", e);
        }
    }
    
    /**
     * Stop alarm sound
     * অ্যালার্ম শব্দ বন্ধ করুন
     */
    private void stopAlarmSound() {
        if (alarmPlayer != null) {
            alarmPlayer.stop();
            alarmPlayer = null;
        }
    }
}
