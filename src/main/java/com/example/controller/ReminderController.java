package com.example.controller;

import com.example.model.Reminder;
import java.util.ArrayList;

public class ReminderController {
    private ArrayList<Reminder> reminders;

    public ReminderController() {
        this.reminders = new ArrayList<>();
    }

    public void add(Reminder reminder) {
        // TODO: Implement add logic
    }

    public void delete(String medicineName) {
        // TODO: Implement delete logic
    }

    public void edit(String medicineName, Reminder updatedReminder) {
        // TODO: Implement edit logic
    }

    public ArrayList<Reminder> getAll() {
        return reminders;
    }
}
