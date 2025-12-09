package com.example.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DoseHistory Model Class
 * Tracks the history of each dose taken, missed, or pending.
 * Used for adherence reporting and pattern analysis.
 */
public class DoseHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Status constants
    public static final String STATUS_TAKEN = "Taken";
    public static final String STATUS_MISSED = "Missed";
    public static final String STATUS_PENDING = "Pending";
    
    private int id;
    private int medicineId;  // Foreign key to Medicine
    private int reminderId;  // Foreign key to Reminder
    private String medicineName;
    private LocalDate date;
    private LocalTime time;
    private String status;  // "Taken", "Missed", "Pending"
    private LocalDateTime recordedAt;
    private String notes;  // User notes (e.g., reason for missing)

    /**
     * Default constructor
     */
    public DoseHistory() {
        this.recordedAt = LocalDateTime.now();
        this.status = STATUS_PENDING;
    }

    /**
     * Constructor with basic parameters
     */
    public DoseHistory(String date, String status) {
        this();
        this.date = LocalDate.parse(date);
        this.status = status;
    }

    /**
     * Constructor with all parameters
     */
    public DoseHistory(int medicineId, int reminderId, String medicineName, LocalDate date, LocalTime time, String status) {
        this();
        this.medicineId = medicineId;
        this.reminderId = reminderId;
        this.medicineName = medicineName;
        this.date = date;
        this.time = time;
        this.status = status;
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

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return medicineName + " on " + date + " at " + time + " - " + status;
    }
}
