package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DoseHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String STATUS_TAKEN = "TAKEN"; // Match enum style or string
    public static final String STATUS_MISSED = "MISSED";
    public static final String STATUS_SKIPPED = "SKIPPED";
    
    private int id;
    private int medicineId;
    private String medicineName; // Transient or joined
    private LocalDateTime scheduledTime;
    private LocalDateTime takenTime;
    private String status;
    private String notes;

    public DoseHistory() {}

    public DoseHistory(int medicineId, LocalDateTime scheduledTime, String status) {
        this.medicineId = medicineId;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public LocalDateTime getTakenTime() { return takenTime; }
    public void setTakenTime(LocalDateTime takenTime) { this.takenTime = takenTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
