package com.example.model;

import java.io.Serializable;

/**
 * Schedule Model Class
 * Represents a specific dosage time and condition (e.g., Morning - Before Meal)
 */
public class Schedule implements Serializable {
    private int id;
    private int medicineId;
    private String timeOfDay; // MORNING, NOON, NIGHT, CUSTOM
    private String mealTiming; // BEFORE_MEAL, AFTER_MEAL, NONE
    private double doseAmount; // Amount to take
    private String customNote; // For custom time/instructions

    public Schedule() {}

    public Schedule(String timeOfDay, String mealTiming, double doseAmount) {
        this.timeOfDay = timeOfDay;
        this.mealTiming = mealTiming;
        this.doseAmount = doseAmount;
    }
    
    public Schedule(String timeOfDay, String mealTiming, double doseAmount, String customNote) {
        this(timeOfDay, mealTiming, doseAmount);
        this.customNote = customNote;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
    public String getMealTiming() { return mealTiming; }
    public void setMealTiming(String mealTiming) { this.mealTiming = mealTiming; }
    public double getDoseAmount() { return doseAmount; }
    public void setDoseAmount(double doseAmount) { this.doseAmount = doseAmount; }
    public String getCustomNote() { return customNote; }
    public void setCustomNote(String customNote) { this.customNote = customNote; }
    
    @Override
    public String toString() {
        if ("CUSTOM".equals(timeOfDay)) {
             return "Custom: " + customNote;
        }
        return timeOfDay + " (" + mealTiming.replace("_", " ") + ")";
    }
}
