package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReminderController Class
 * Handles all business logic for reminder operations.
 * Manages reminder scheduling and notifications.
 */
public class ReminderController {
    private static final Logger logger = LoggerFactory.getLogger(ReminderController.class);
    private DatabaseManager dbManager;
    private List<Reminder> reminders;

    public ReminderController() {
        this.dbManager = DatabaseManager.getInstance();
        this.reminders = new ArrayList<>();
        loadReminders();
    }

    /**
     * Load all reminders from database
     */
    public void loadReminders() {
        this.reminders = dbManager.getAllReminders();
        logger.info("Loaded " + reminders.size() + " reminders from database");
    }

    /**
     * Add new reminder
     */
    public int addReminder(Reminder reminder) {
        if (reminder == null || reminder.getMedicineName() == null) {
            logger.warn("Cannot add invalid reminder");
            return -1;
        }

        int id = dbManager.addReminder(reminder);
        if (id > 0) {
            reminder.setId(id);
            reminders.add(reminder);
            logger.info("Reminder added for: " + reminder.getMedicineName() + " at " + reminder.getTime());
        }
        return id;
    }

    /**
     * Update reminder
     */
    public boolean updateReminder(Reminder reminder) {
        if (reminder == null || reminder.getId() <= 0) {
            logger.warn("Invalid reminder for update");
            return false;
        }

        boolean success = dbManager.updateReminder(reminder);
        if (success) {
            for (int i = 0; i < reminders.size(); i++) {
                if (reminders.get(i).getId() == reminder.getId()) {
                    reminders.set(i, reminder);
                    break;
                }
            }
            logger.info("Reminder updated for: " + reminder.getMedicineName());
        }
        return success;
    }

    /**
     * Delete reminder by ID
     */
    public boolean deleteReminder(int reminderId) {
        boolean success = dbManager.deleteReminder(reminderId);
        if (success) {
            reminders.removeIf(r -> r.getId() == reminderId);
            logger.info("Reminder deleted with ID: " + reminderId);
        }
        return success;
    }

    /**
     * Get all reminders
     */
    public List<Reminder> getAllReminders() {
        return new ArrayList<>(reminders);
    }

    /**
     * Get reminders by medicine ID
     */
    public List<Reminder> getRemindersByMedicineId(int medicineId) {
        return reminders.stream()
                .filter(r -> r.getMedicineId() == medicineId)
                .collect(Collectors.toList());
    }

    /**
     * Get reminders by medicine name
     */
    public List<Reminder> getRemindersByMedicineName(String medicineName) {
        return reminders.stream()
                .filter(r -> r.getMedicineName().equalsIgnoreCase(medicineName))
                .collect(Collectors.toList());
    }

    /**
     * Get pending reminders (not taken today)
     */
    public List<Reminder> getPendingReminders() {
        return reminders.stream()
                .filter(r -> !r.isTaken())
                .collect(Collectors.toList());
    }

    /**
     * Get reminders due at specific time
     */
    public List<Reminder> getRemindersDueAt(LocalTime time) {
        return reminders.stream()
                .filter(r -> r.getTime().equals(time) && !r.isTaken())
                .collect(Collectors.toList());
    }

    /**
     * Mark reminder as taken
     */
    public boolean markAsTaken(int reminderId) {
        for (Reminder reminder : reminders) {
            if (reminder.getId() == reminderId) {
                reminder.setTaken(true);
                return updateReminder(reminder);
            }
        }
        return false;
    }

    /**
     * Get reminder by ID
     */
    public Reminder getReminderById(int reminderId) {
        return reminders.stream()
                .filter(r -> r.getId() == reminderId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Reset all reminders (mark as not taken for new day)
     */
    public void resetAllReminders() {
        for (Reminder reminder : reminders) {
            reminder.setTaken(false);
            updateReminder(reminder);
        }
        logger.info("All reminders reset for new day");
    }

    /**
     * Get reminders sorted by time
     */
    public List<Reminder> getRemindersSortedByTime() {
        return reminders.stream()
                .sorted((r1, r2) -> r1.getTime().compareTo(r2.getTime()))
                .collect(Collectors.toList());
    }

    /**
     * Get total number of reminders
     */
    public int getTotalReminders() {
        return reminders.size();
    }

    /**
     * Get total pending reminders
     */
    public int getPendingCount() {
        return (int) reminders.stream().filter(r -> !r.isTaken()).count();
    }

    /**
     * Refresh reminders from database
     */
    public void refresh() {
        loadReminders();
        logger.info("Reminder list refreshed");
    }
}
