package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Reminder Model Class
 * Represents a reminder for a specific medicine at a designated time.
 * Supports multiple reminder times (morning, noon, evening, custom).
 */
public class Reminder implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int medicineId;  // Foreign key to Medicine
    private String medicineName;
    private LocalTime time;  // Time in HH:MM format
    private String reminderType;  // "morning", "noon", "evening", "custom"
    private boolean taken;  // Has the dose been taken today?
    private LocalDateTime lastTakenAt;
    private LocalDateTime createdAt;

    /**
     * Default constructor
     */
    public Reminder() {
        this.createdAt = LocalDateTime.now();
        this.taken = false;
    }

    /**
     * Constructor with basic parameters
     */
    public Reminder(String medicineName, String time) {
        this();
        this.medicineName = medicineName;
        this.time = LocalTime.parse(time);
        this.reminderType = "custom";
    }

    /**
     * Constructor with type
     */
    public Reminder(int medicineId, String medicineName, LocalTime time, String reminderType) {
        this();
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.time = time;
        this.reminderType = reminderType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
        if (taken) {
            this.lastTakenAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getLastTakenAt() {
        return lastTakenAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return medicineName + " at " + time + " (" + reminderType + ")";
    }
}
