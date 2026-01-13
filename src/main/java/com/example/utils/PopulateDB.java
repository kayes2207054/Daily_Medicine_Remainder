package com.example.utils;

import com.example.database.DatabaseManager;
import com.example.model.DoseHistory;
import com.example.model.Medicine;
import com.example.model.Schedule;
import com.example.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PopulateDB {

    public static void main(String[] args) {
        System.out.println("Starting Database Population for User: kayes");
        
        DatabaseManager db = DatabaseManager.getInstance();
        
        // 1. Create User
        User user = new User("kayes", "1234", "PATIENT", "Kayes User");
        // Simple check if exists or just try insert
        // Since we don't have exists method cleanly exposed, we rely on register returning false/error if unique constraint fails
        // We will just try to register.
        boolean registered = db.registerUser(user);
        if (registered) {
            System.out.println("User 'kayes' created successfully.");
        } else {
            System.out.println("User 'kayes' might already exist or creation failed.");
        }

        // 2. Clear existing medicines (Optional, but good for clean slate if we want)
        // For now, we just add new ones.

        // 3. Add Medicines
        addNapa(db);
        addSeclo(db);
        addCeevit(db);
        addMonas(db);
        
        System.out.println("Database population complete!");
        System.exit(0);
    }

    private static void addNapa(DatabaseManager db) {
        Medicine m = new Medicine();
        m.setName("Napa Extend");
        m.setStockQuantity(50);
        m.setLowStockThreshold(10);
        m.setDoseUnit("Tablet");
        m.setInstructions("Take for fever or pain");
        
        List<Schedule> scheds = new ArrayList<>();
        scheds.add(new Schedule("MORNING", "AFTER_MEAL", 1.0));
        scheds.add(new Schedule("NIGHT", "AFTER_MEAL", 1.0));
        m.setSchedules(scheds);
        
        int id = db.addMedicine(m);
        if (id != -1) {
            System.out.println("Added Napa (ID: " + id + ")");
            generateHistory(db, id, scheds);
        }
    }

    private static void addSeclo(DatabaseManager db) {
        Medicine m = new Medicine();
        m.setName("Seclo 20mg");
        m.setStockQuantity(30);
        m.setLowStockThreshold(10);
        m.setDoseUnit("Capsule");
        m.setInstructions("Gastric relief");
        
        List<Schedule> scheds = new ArrayList<>();
        scheds.add(new Schedule("MORNING", "BEFORE_MEAL", 1.0));
        m.setSchedules(scheds);
        
        int id = db.addMedicine(m);
        if (id != -1) {
            System.out.println("Added Seclo (ID: " + id + ")");
            generateHistory(db, id, scheds);
        }
    }

    private static void addCeevit(DatabaseManager db) {
        Medicine m = new Medicine();
        m.setName("Ceevit");
        m.setStockQuantity(20);
        m.setLowStockThreshold(5);
        m.setDoseUnit("Tablet");
        m.setInstructions("Vitamin C Supplement");
        
        List<Schedule> scheds = new ArrayList<>();
        scheds.add(new Schedule("NOON", "AFTER_MEAL", 1.0));
        m.setSchedules(scheds);
        
        int id = db.addMedicine(m);
        if (id != -1) {
            System.out.println("Added Ceevit (ID: " + id + ")");
            generateHistory(db, id, scheds);
        }
    }

    private static void addMonas(DatabaseManager db) {
        Medicine m = new Medicine();
        m.setName("Monas 10");
        m.setStockQuantity(15);
        m.setLowStockThreshold(5);
        m.setDoseUnit("Tablet");
        m.setInstructions("For asthma/allergy");
        
        List<Schedule> scheds = new ArrayList<>();
        scheds.add(new Schedule("NIGHT", "BEFORE_MEAL", 1.0));
        m.setSchedules(scheds);
        
        int id = db.addMedicine(m);
        if (id != -1) {
            System.out.println("Added Monas (ID: " + id + ")");
            generateHistory(db, id, scheds);
        }
    }

    private static void generateHistory(DatabaseManager db, int medId, List<Schedule> scheds) {
        // Generate history for last 3 days
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random();
        
        for (int i = 0; i < 3; i++) { // 3 days back
            LocalDateTime date = now.minusDays(i + 1); // Yesterday, Day before...
            
            for (Schedule s : scheds) {
                // Determine approximate time based on Schedule time
                int hour = 9;
                if (s.getTimeOfDay().equalsIgnoreCase("NOON")) hour = 13;
                if (s.getTimeOfDay().equalsIgnoreCase("NIGHT")) hour = 21;
                
                LocalDateTime scheduledTime = date.withHour(hour).withMinute(0);
                
                // Random status
                String status = DoseHistory.STATUS_TAKEN;
                LocalDateTime takenTime = scheduledTime.plusMinutes(random.nextInt(60)); // Taken within an hour
                String notes = "Taken on time";
                
                if (random.nextDouble() > 0.8) { // 20% chance missed
                    status = DoseHistory.STATUS_MISSED;
                    takenTime = null;
                    notes = "Forgot to take";
                }
                
                DoseHistory h = new DoseHistory(medId, scheduledTime, status);
                h.setTakenTime(takenTime);
                h.setNotes(notes);
                
                db.addDoseHistory(h);
            }
        }
    }
}
