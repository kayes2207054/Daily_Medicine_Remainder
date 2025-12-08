package com.example.model;

public class Medicine {
    private String name;
    private String dosage;
    private String frequency;
    private String instructions;

    public Medicine() {
    }

    public Medicine(String name, String dosage, String frequency, String instructions) {
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.instructions = instructions;
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
}
