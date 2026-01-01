package com.example.model;

import java.time.LocalDateTime;

/**
 * Notification Model
 * Represents notifications sent to guardians about patient activities
 */
public class Notification {
    
    public enum Type {
        DOSE_TAKEN,
        DOSE_MISSED,
        DOSE_SNOOZED,
        LOW_ADHERENCE,
        SYSTEM_ALERT
    }
    
    private int id;
    private int guardianId;
    private int patientId;
    private String patientName;
    private Type type;
    private String message;
    private String details;
    private LocalDateTime createdAt;
    private boolean read;
    
    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }
    
    public Notification(int guardianId, int patientId, Type type, String message) {
        this();
        this.guardianId = guardianId;
        this.patientId = patientId;
        this.type = type;
        this.message = message;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getGuardianId() {
        return guardianId;
    }
    
    public void setGuardianId(int guardianId) {
        this.guardianId = guardianId;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", guardianId=" + guardianId +
                ", patientId=" + patientId +
                ", type=" + type +
                ", message='" + message + '\'' +
                ", read=" + read +
                '}';
    }
}
