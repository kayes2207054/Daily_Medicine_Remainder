package com.example.utils;

import com.example.model.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * NotificationService Class
 * Manages reminder notifications and scheduling.
 * Handles desktop notifications and reminder scheduling.
 */
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private Timer scheduler;
    private List<Reminder> reminders;
    private static final int CHECK_INTERVAL_MS = 60000;  // Check every minute

    public NotificationService() {
        this.reminders = new ArrayList<>();
        this.scheduler = new Timer("ReminderScheduler", true);
        startScheduler();
    }

    /**
     * Start the reminder scheduler
     */
    public void startScheduler() {
        scheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndNotify();
            }
        }, 0, CHECK_INTERVAL_MS);
        logger.info("Reminder scheduler started");
    }

    /**
     * Stop the reminder scheduler
     */
    public void stopScheduler() {
        if (scheduler != null) {
            scheduler.cancel();
            logger.info("Reminder scheduler stopped");
        }
    }

    /**
     * Add reminder to be scheduled
     */
    public void addReminder(Reminder reminder) {
        if (!reminders.contains(reminder)) {
            reminders.add(reminder);
            logger.info("Reminder added: " + reminder.getMedicineName() + " at " + reminder.getTime());
        }
    }

    /**
     * Remove reminder from scheduling
     */
    public void removeReminder(Reminder reminder) {
        reminders.remove(reminder);
        logger.info("Reminder removed: " + reminder.getMedicineName());
    }

    /**
     * Update reminder list
     */
    public void setReminders(List<Reminder> reminders) {
        this.reminders = new ArrayList<>(reminders);
    }

    /**
     * Check all reminders and send notifications if time matches
     */
    private void checkAndNotify() {
        LocalTime currentTime = LocalTime.now();
        for (Reminder reminder : reminders) {
            if (!reminder.isTaken() && isReminderTime(reminder.getTime(), currentTime)) {
                sendNotification(reminder);
            }
        }
    }

    /**
     * Check if current time matches reminder time (within 1 minute tolerance)
     */
    private boolean isReminderTime(LocalTime reminderTime, LocalTime currentTime) {
        return reminderTime.getHour() == currentTime.getHour() &&
                reminderTime.getMinute() == currentTime.getMinute();
    }

    /**
     * Send desktop notification
     */
    public void sendNotification(Reminder reminder) {
        String title = "Medicine Reminder";
        String message = "Time to take " + reminder.getMedicineName() + "!";
        showNotificationDialog(title, message, reminder);
        logger.info("Notification sent for: " + reminder.getMedicineName());
    }

    /**
     * Send custom notification
     */
    public void sendCustomNotification(String title, String message) {
        showCustomNotificationDialog(title, message);
        logger.info("Custom notification sent: " + title + " - " + message);
    }

    /**
     * Show notification dialog with reminder action buttons
     */
    private void showNotificationDialog(String title, String message, Reminder reminder) {
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(titleLabel.getFont().deriveFont(14f));
            panel.add(titleLabel);
            
            JLabel messageLabel = new JLabel(message);
            messageLabel.setFont(messageLabel.getFont().deriveFont(12f));
            panel.add(messageLabel);

            int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "DailyDose - " + title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                reminder.setTaken(true);
                logger.info("Dose marked as taken: " + reminder.getMedicineName());
            }
        });
    }

    /**
     * Show custom notification dialog
     */
    private void showCustomNotificationDialog(String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    message,
                    "DailyDose - " + title,
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    /**
     * Schedule a specific reminder time for tomorrow
     */
    public void scheduleReminder(Reminder reminder) {
        addReminder(reminder);
    }

    /**
     * Send low stock warning
     */
    public void sendLowStockWarning(String medicineName, int remainingQuantity) {
        String title = "Low Stock Warning";
        String message = medicineName + " has only " + remainingQuantity + " pills left. Please refill soon!";
        sendCustomNotification(title, message);
    }

    /**
     * Send missed dose notification
     */
    public void sendMissedDoseNotification(String medicineName, String time) {
        String title = "Missed Dose Alert";
        String message = "You missed the dose of " + medicineName + " at " + time;
        sendCustomNotification(title, message);
    }

    /**
     * Get all scheduled reminders
     */
    public List<Reminder> getScheduledReminders() {
        return new ArrayList<>(reminders);
    }

    /**
     * Get reminders due now
     */
    public List<Reminder> getRemindersduenow() {
        LocalTime currentTime = LocalTime.now();
        List<Reminder> dueReminders = new ArrayList<>();
        for (Reminder reminder : reminders) {
            if (!reminder.isTaken() && isReminderTime(reminder.getTime(), currentTime)) {
                dueReminders.add(reminder);
            }
        }
        return dueReminders;
    }
}
