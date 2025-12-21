package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.DoseHistory;
import com.example.model.Inventory;
import com.example.model.Reminder;
import com.example.model.Reminder.Status;
import com.example.controller.InventoryController;
import com.example.controller.HistoryController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ReminderController manages in-memory reminders and exposes operations
 * for the UI and background reminder service.
 */
public class ReminderController {
    private static final Logger logger = LoggerFactory.getLogger(ReminderController.class);
    private final List<Reminder> reminders = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final DatabaseManager dbManager;
    private InventoryController inventoryController;
    private HistoryController historyController;

    public ReminderController() {
        this.dbManager = DatabaseManager.getInstance();
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        List<Reminder> stored = dbManager.getAllReminders();
        reminders.clear();
        reminders.addAll(stored);
        int maxId = stored.stream().mapToInt(Reminder::getId).max().orElse(0);
        idGenerator.set(maxId + 1);
        logger.info("Loaded {} reminders from database", stored.size());
    }

    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    public void setHistoryController(HistoryController historyController) {
        this.historyController = historyController;
    }

    public Reminder addReminder(Reminder reminder) {
        if (reminder == null || reminder.getMedicineName() == null || reminder.getReminderTime() == null) {
            logger.warn("Cannot add invalid reminder");
            return null;
        }
        reminder.setStatus(Status.PENDING);
        int newId = dbManager.addReminder(reminder);
        if (newId > 0) {
            reminder.setId(newId);
        } else {
            reminder.setId(idGenerator.getAndIncrement());
        }
        reminders.add(reminder);
        logger.info("Reminder added: {} at {}", reminder.getMedicineName(), reminder.getReminderTime());
        return reminder;
    }

    public boolean deleteReminder(int reminderId) {
        boolean removed = dbManager.deleteReminder(reminderId);
        if (removed) {
            reminders.removeIf(r -> r.getId() == reminderId);
            logger.info("Reminder deleted: {}", reminderId);
        }
        return removed;
    }

    public boolean updateReminder(Reminder reminder) {
        if (reminder == null || reminder.getId() <= 0) return false;
        boolean success = dbManager.updateReminder(reminder);
        if (!success) {
            return false;
        }
        synchronized (reminders) {
            for (int i = 0; i < reminders.size(); i++) {
                if (reminders.get(i).getId() == reminder.getId()) {
                    reminders.set(i, reminder);
                    return true;
                }
            }
        }
        return true;
    }

    public List<Reminder> getAllReminders() {
        synchronized (reminders) {
            return new ArrayList<>(reminders);
        }
    }

    public Reminder getReminderById(int reminderId) {
        synchronized (reminders) {
            return reminders.stream().filter(r -> r.getId() == reminderId).findFirst().orElse(null);
        }
    }

    public List<Reminder> getPendingReminders() {
        synchronized (reminders) {
            return reminders.stream()
                    .filter(r -> r.getStatus() == Status.PENDING)
                    .collect(Collectors.toList());
        }
    }

    public List<Reminder> getDueReminders(LocalDateTime now) {
        synchronized (reminders) {
            return reminders.stream()
                    .filter(r -> r.getStatus() == Status.PENDING && !r.getReminderTime().isAfter(now))
                    .collect(Collectors.toList());
        }
    }

    public void markTaken(int reminderId) {
        Reminder r = getReminderById(reminderId);
        if (r != null) {
            r.setStatus(Status.TAKEN);
            updateReminder(r);
            recordHistory(r, DoseHistory.STATUS_TAKEN);
            decreaseInventory(r);
        }
    }

    public void markMissed(int reminderId) {
        Reminder r = getReminderById(reminderId);
        if (r != null) {
            r.setStatus(Status.MISSED);
            updateReminder(r);
            recordHistory(r, DoseHistory.STATUS_MISSED);
        }
    }

    public void snooze(int reminderId, int minutes) {
        Reminder r = getReminderById(reminderId);
        if (r != null) {
            r.setReminderTime(r.getReminderTime().plusMinutes(minutes));
            r.setStatus(Status.PENDING);
            updateReminder(r);
        }
    }

    public int getPendingCount() {
        return getPendingReminders().size();
    }

    public List<Reminder> getRemindersSortedByTime() {
        return getAllReminders().stream()
                .sorted(Comparator.comparing(Reminder::getReminderTime))
                .collect(Collectors.toList());
    }

    private void recordHistory(Reminder reminder, String statusLabel) {
        if (historyController == null || reminder.getReminderTime() == null) {
            return;
        }
        DoseHistory history = new DoseHistory();
        history.setMedicineId(0);
        history.setReminderId(reminder.getId());
        history.setMedicineName(reminder.getMedicineName());
        history.setDate(reminder.getReminderTime().toLocalDate());
        history.setTime(reminder.getReminderTime().toLocalTime());
        history.setStatus(statusLabel);
        historyController.addHistory(history);
    }

    private void decreaseInventory(Reminder reminder) {
        if (inventoryController == null) {
            return;
        }
        Inventory inv = inventoryController.getInventoryByMedicineName(reminder.getMedicineName());
        if (inv != null) {
            inventoryController.decreaseQuantity(inv.getMedicineId(), 1);
        }
    }
}
