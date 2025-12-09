package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Medicine Model Class
 * Represents a medicine entity with dosage, frequency, and usage instructions.
 * Used for tracking medications throughout the application.
 */
public class Medicine implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;  // Unique identifier for database
    private String name;
    private String dosage;
    private String frequency;  // "1 times", "2 times", "3 times" per day
    private String instructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor for Medicine
     */
    public Medicine() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor with basic parameters
     */
    public Medicine(String name, String dosage, String frequency, String instructions) {
        this();
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.instructions = instructions;
    }

    /**
     * Constructor with all parameters including ID
     */
    public Medicine(int id, String name, String dosage, String frequency, String instructions, LocalDateTime createdAt) {
        this(name, dosage, frequency, instructions);
        this.id = id;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return name + " (" + dosage + " - " + frequency + " times/day)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Medicine medicine = (Medicine) obj;
        return id == medicine.id || (name != null && name.equals(medicine.name));
    }

    @Override
    public int hashCode() {
        return id != 0 ? Integer.hashCode(id) : (name != null ? name.hashCode() : 0);
    }
}
