package com.example.controller;

import com.example.model.Reminder;
import com.example.model.Reminder.Status;
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

    public ReminderController() {
        // starts empty; can be extended to load from storage later
    }

    public Reminder addReminder(Reminder reminder) {
        if (reminder == null || reminder.getMedicineName() == null || reminder.getReminderTime() == null) {
            logger.warn("Cannot add invalid reminder");
            return null;
        }
        reminder.setId(idGenerator.getAndIncrement());
        reminder.setStatus(Status.PENDING);
        reminders.add(reminder);
        logger.info("Reminder added: {} at {}", reminder.getMedicineName(), reminder.getReminderTime());
        return reminder;
    }

    public boolean deleteReminder(int reminderId) {
        boolean removed = reminders.removeIf(r -> r.getId() == reminderId);
        if (removed) {
            logger.info("Reminder deleted: {}", reminderId);
        }
        return removed;
    }

    public boolean updateReminder(Reminder reminder) {
        if (reminder == null || reminder.getId() <= 0) return false;
        synchronized (reminders) {
            for (int i = 0; i < reminders.size(); i++) {
                if (reminders.get(i).getId() == reminder.getId()) {
                    reminders.set(i, reminder);
                    return true;
                }
            }
        }
        return false;
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
        }
    }

    public void markMissed(int reminderId) {
        Reminder r = getReminderById(reminderId);
        if (r != null) {
            r.setStatus(Status.MISSED);
            updateReminder(r);
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
}
