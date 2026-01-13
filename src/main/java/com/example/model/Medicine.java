package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Medicine Model Class
 * Represents a medicine entity with normalized dosage schedules and inventory.
 */
public class Medicine implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private int stockQuantity; // Replaces 'quantity'
    private int lowStockThreshold;
    private String doseUnit; // e.g. "Tablet", "mg"
    private String instructions;
    private List<Schedule> schedules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Medicine() {
        this.schedules = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lowStockThreshold = 10; // Default
    }

    public Medicine(String name, String doseUnit, String instructions, int stockQuantity) {
        this();
        this.name = name;
        this.doseUnit = doseUnit;
        this.instructions = instructions;
        this.stockQuantity = stockQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    
    public String getDoseUnit() { return doseUnit; }
    public void setDoseUnit(String doseUnit) { this.doseUnit = doseUnit; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Helper to get simple dosage string for UI display
    public String getDosageSummary() {
        if (schedules == null || schedules.isEmpty()) return "No Schedule";
        StringBuilder sb = new StringBuilder();
        for (Schedule s : schedules) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(s.toString());
        }
        return sb.toString();
    }
}
