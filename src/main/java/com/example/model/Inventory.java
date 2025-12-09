package com.example.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Inventory Model Class
 * Tracks pill counts and stock levels for medicines.
 * Provides low-stock warnings and refill date estimation.
 */
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int medicineId;  // Foreign key to Medicine
    private String medicineName;
    private int quantity;  // Current pill count
    private int threshold;  // Low-stock warning threshold (default 10)
    private int dailyUsage;  // Pills consumed per day (for refill estimation)
    private LocalDate lastRefillDate;
    private LocalDate estimatedRefillDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor
     */
    public Inventory() {
        this.threshold = 10;  // Default low-stock threshold
        this.dailyUsage = 1;  // Default daily usage
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastRefillDate = LocalDate.now();
        calculateRefillDate();
    }

    /**
     * Constructor with basic parameters
     */
    public Inventory(String medicineName, int quantity) {
        this();
        this.medicineName = medicineName;
        this.quantity = quantity;
    }

    /**
     * Constructor with all parameters
     */
    public Inventory(int medicineId, String medicineName, int quantity, int threshold, int dailyUsage) {
        this();
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.threshold = threshold;
        this.dailyUsage = dailyUsage;
        calculateRefillDate();
    }

    /**
     * Calculate estimated refill date based on current quantity and daily usage
     */
    private void calculateRefillDate() {
        if (dailyUsage > 0) {
            int daysRemaining = quantity / dailyUsage;
            this.estimatedRefillDate = LocalDate.now().plusDays(daysRemaining);
        }
    }

    /**
     * Decrease quantity when a dose is taken
     */
    public void decreaseQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
        this.updatedAt = LocalDateTime.now();
        calculateRefillDate();
    }

    /**
     * Check if stock is low
     */
    public boolean isLowStock() {
        return quantity <= threshold;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateRefillDate();
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getDailyUsage() {
        return dailyUsage;
    }

    public void setDailyUsage(int dailyUsage) {
        this.dailyUsage = dailyUsage;
        calculateRefillDate();
    }

    public LocalDate getLastRefillDate() {
        return lastRefillDate;
    }

    public void setLastRefillDate(LocalDate lastRefillDate) {
        this.lastRefillDate = lastRefillDate;
        calculateRefillDate();
    }

    public LocalDate getEstimatedRefillDate() {
        return estimatedRefillDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return medicineName + " - " + quantity + " pills (Refill: " + estimatedRefillDate + ")";
    }
}
