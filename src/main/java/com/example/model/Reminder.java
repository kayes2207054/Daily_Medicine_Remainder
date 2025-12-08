package com.example.model;

public class Reminder {
    private String medicineName;
    private String time;

    public Reminder() {
    }

    public Reminder(String medicineName, String time) {
        this.medicineName = medicineName;
        this.time = time;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
