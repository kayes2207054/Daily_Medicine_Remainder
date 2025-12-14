package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reminder Model Class
 * Represents an alarm-like reminder for a medicine at a specific date/time.
 * Stored in-memory (ArrayList) for now; no database persistence yet.
 */
public class Reminder implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING,
        TAKEN,
        MISSED
    }

    private int id;
    private String medicineName;
    private LocalDateTime reminderTime;
    private Status status;
    private LocalDateTime createdAt;

    public Reminder() {
        this.createdAt = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public Reminder(String medicineName, LocalDateTime reminderTime) {
        this();
        this.medicineName = medicineName;
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        String time = reminderTime != null
                ? reminderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "";
        return medicineName + " at " + time + " (" + status + ")";
    }
}
