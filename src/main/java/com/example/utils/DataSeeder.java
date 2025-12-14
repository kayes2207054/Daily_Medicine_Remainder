package com.example.utils;

import com.example.database.DatabaseManager;
import com.example.model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DataSeeder Class
 * Populates the database with sample data for testing and demonstration.
 * Can be called during application startup if database is empty.
 */
public class DataSeeder {

    /**
     * Seed database with sample medicines
     */
    public static void seedSampleData() {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Check if medicines already exist
        if (dbManager.getAllMedicines().size() > 0) {
            return; // Data already seeded
        }

        // Sample medicines
        Medicine aspirin = new Medicine("Aspirin", "500mg", "2 times", "Take with water after meals");
        Medicine lisinopril = new Medicine("Lisinopril", "10mg", "1 time", "Take in the morning");
        Medicine metformin = new Medicine("Metformin", "1000mg", "3 times", "Take with meals, morning, noon, evening");
        Medicine omeprazole = new Medicine("Omeprazole", "20mg", "1 time", "Take 30 minutes before breakfast");
        Medicine atorvastatin = new Medicine("Atorvastatin", "20mg", "1 time", "Take at night");

        int aspirinId = dbManager.addMedicine(aspirin);
        int lisinoprilId = dbManager.addMedicine(lisinopril);
        int metforminId = dbManager.addMedicine(metformin);
        int omeprazoleId = dbManager.addMedicine(omeprazole);
        int atorvastatinId = dbManager.addMedicine(atorvastatin);

        // Sample reminders
        LocalDate todayDate = LocalDate.now();
        Reminder reminder1 = new Reminder("Aspirin", LocalDateTime.of(todayDate, LocalTime.of(9, 0)));
        Reminder reminder2 = new Reminder("Aspirin", LocalDateTime.of(todayDate, LocalTime.of(21, 0)));
        Reminder reminder3 = new Reminder("Lisinopril", LocalDateTime.of(todayDate, LocalTime.of(8, 0)));
        Reminder reminder4 = new Reminder("Metformin", LocalDateTime.of(todayDate, LocalTime.of(8, 30)));
        Reminder reminder5 = new Reminder("Metformin", LocalDateTime.of(todayDate, LocalTime.of(13, 30)));
        Reminder reminder6 = new Reminder("Metformin", LocalDateTime.of(todayDate, LocalTime.of(20, 30)));
        Reminder reminder7 = new Reminder("Omeprazole", LocalDateTime.of(todayDate, LocalTime.of(7, 0)));
        Reminder reminder8 = new Reminder("Atorvastatin", LocalDateTime.of(todayDate, LocalTime.of(22, 0)));

        dbManager.addReminder(reminder1);
        dbManager.addReminder(reminder2);
        dbManager.addReminder(reminder3);
        dbManager.addReminder(reminder4);
        dbManager.addReminder(reminder5);
        dbManager.addReminder(reminder6);
        dbManager.addReminder(reminder7);
        dbManager.addReminder(reminder8);

        // Sample inventory
        Inventory inv1 = new Inventory(aspirinId, "Aspirin", 60, 10, 2);
        Inventory inv2 = new Inventory(lisinoprilId, "Lisinopril", 30, 5, 1);
        Inventory inv3 = new Inventory(metforminId, "Metformin", 90, 15, 3);
        Inventory inv4 = new Inventory(omeprazoleId, "Omeprazole", 30, 5, 1);
        Inventory inv5 = new Inventory(atorvastatinId, "Atorvastatin", 30, 5, 1);

        dbManager.addInventory(inv1);
        dbManager.addInventory(inv2);
        dbManager.addInventory(inv3);
        dbManager.addInventory(inv4);
        dbManager.addInventory(inv5);

        // Sample dose history for today and past days
        LocalDate today = LocalDate.now();
        
        // Today's doses
        DoseHistory dh1 = new DoseHistory(aspirinId, 1, "Aspirin", today, LocalTime.of(9, 0), DoseHistory.STATUS_TAKEN);
        DoseHistory dh2 = new DoseHistory(lisinoprilId, 3, "Lisinopril", today, LocalTime.of(8, 0), DoseHistory.STATUS_TAKEN);
        DoseHistory dh3 = new DoseHistory(metforminId, 4, "Metformin", today, LocalTime.of(8, 30), DoseHistory.STATUS_TAKEN);
        DoseHistory dh4 = new DoseHistory(metforminId, 5, "Metformin", today, LocalTime.of(13, 30), DoseHistory.STATUS_PENDING);
        DoseHistory dh5 = new DoseHistory(omeprazoleId, 7, "Omeprazole", today, LocalTime.of(7, 0), DoseHistory.STATUS_TAKEN);
        DoseHistory dh6 = new DoseHistory(atorvastatinId, 8, "Atorvastatin", today, LocalTime.of(22, 0), DoseHistory.STATUS_PENDING);

        dbManager.addDoseHistory(dh1);
        dbManager.addDoseHistory(dh2);
        dbManager.addDoseHistory(dh3);
        dbManager.addDoseHistory(dh4);
        dbManager.addDoseHistory(dh5);
        dbManager.addDoseHistory(dh6);

        // Yesterday's doses
        LocalDate yesterday = today.minusDays(1);
        DoseHistory dh7 = new DoseHistory(aspirinId, 1, "Aspirin", yesterday, LocalTime.of(9, 0), DoseHistory.STATUS_TAKEN);
        DoseHistory dh8 = new DoseHistory(aspirinId, 2, "Aspirin", yesterday, LocalTime.of(21, 0), DoseHistory.STATUS_TAKEN);
        DoseHistory dh9 = new DoseHistory(lisinoprilId, 3, "Lisinopril", yesterday, LocalTime.of(8, 0), DoseHistory.STATUS_TAKEN);
        DoseHistory dh10 = new DoseHistory(metforminId, 4, "Metformin", yesterday, LocalTime.of(8, 30), DoseHistory.STATUS_TAKEN);
        DoseHistory dh11 = new DoseHistory(metforminId, 5, "Metformin", yesterday, LocalTime.of(13, 30), DoseHistory.STATUS_MISSED);
        DoseHistory dh12 = new DoseHistory(omeprazoleId, 7, "Omeprazole", yesterday, LocalTime.of(7, 0), DoseHistory.STATUS_TAKEN);

        dbManager.addDoseHistory(dh7);
        dbManager.addDoseHistory(dh8);
        dbManager.addDoseHistory(dh9);
        dbManager.addDoseHistory(dh10);
        dbManager.addDoseHistory(dh11);
        dbManager.addDoseHistory(dh12);

        System.out.println("Sample data seeded successfully!");
    }
}
