package com.example.model;

import java.time.LocalDateTime;

/**
 * GuardianPatientLink Model
 * Represents the relationship between a guardian and a patient
 */
public class GuardianPatientLink {
    
    private int id;
    private int guardianId;
    private int patientId;
    private String guardianUsername;
    private String patientUsername;
    private String patientFullName;
    private LocalDateTime linkedAt;
    private boolean active;
    
    // Constructors
    public GuardianPatientLink() {
        this.linkedAt = LocalDateTime.now();
        this.active = true;
    }
    
    public GuardianPatientLink(int guardianId, int patientId) {
        this();
        this.guardianId = guardianId;
        this.patientId = patientId;
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
    
    public String getGuardianUsername() {
        return guardianUsername;
    }
    
    public void setGuardianUsername(String guardianUsername) {
        this.guardianUsername = guardianUsername;
    }
    
    public String getPatientUsername() {
        return patientUsername;
    }
    
    public void setPatientUsername(String patientUsername) {
        this.patientUsername = patientUsername;
    }
    
    public String getPatientFullName() {
        return patientFullName;
    }
    
    public void setPatientFullName(String patientFullName) {
        this.patientFullName = patientFullName;
    }
    
    public LocalDateTime getLinkedAt() {
        return linkedAt;
    }
    
    public void setLinkedAt(LocalDateTime linkedAt) {
        this.linkedAt = linkedAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "GuardianPatientLink{" +
                "id=" + id +
                ", guardianId=" + guardianId +
                ", patientId=" + patientId +
                ", active=" + active +
                '}';
    }
}
