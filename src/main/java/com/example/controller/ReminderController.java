package com.example.controller;

import com.example.model.Medicine;
import com.example.model.Schedule;
import com.example.model.Reminder;
import com.example.model.DoseHistory;
import com.example.utils.DataChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderController {
    private static final Logger logger = LoggerFactory.getLogger(ReminderController.class);
    private MedicineController medicineController;
    private HistoryController historyController;
    private List<Reminder> dailyReminders = new ArrayList<>();
    private List<DataChangeListener> listeners = new ArrayList<>();

    public ReminderController() { }

    public void setMedicineController(MedicineController medicineController) {
        this.medicineController = medicineController;
    }
    
    public MedicineController getMedicineController() {
        return medicineController;
    }

    public void setHistoryController(HistoryController historyController) {
        this.historyController = historyController;
    }
    
    public void generateDailyReminders() {
        if(medicineController == null) return;
        
        dailyReminders.clear();
        List<Medicine> medicines = medicineController.getAllMedicines();
        LocalDate today = LocalDate.now();
        
        for(Medicine m : medicines) {
            for(Schedule s : m.getSchedules()) {
                LocalDateTime dueTime = calculateDueTime(today, s);
                if(dueTime != null) {
                    Reminder r = new Reminder(m.getName(), dueTime);
                    r.setId(s.getId() * 10000 + dueTime.getHour()); // Pseudo ID
                    // Check status
                    updateStatus(r, m.getId());
                    dailyReminders.add(r);
                }
            }
        }
        Collections.sort(dailyReminders, (a, b) -> a.getReminderTime().compareTo(b.getReminderTime()));
        notifyDataChanged();
    }
    
    private LocalDateTime calculateDueTime(LocalDate date, Schedule s) {
        LocalTime time;
        switch(s.getTimeOfDay()) {
            case "MORNING": time = LocalTime.of(8, 0); break;
            case "NOON": time = LocalTime.of(13, 0); break;
            case "NIGHT": time = LocalTime.of(20, 0); break;
            case "CUSTOM": 
                // Parsing custom notes for time is complex, returning null or default
                // Ideally Schedule should have a LocalTime field if Custom
                // Returning null for now to avoid crashes
                return null; 
            default: return null;
        }
        return LocalDateTime.of(date, time);
    }
    
    private void updateStatus(Reminder r, int medicineId) {
        if(historyController == null) return;
        List<DoseHistory> history = historyController.getHistoryList();
        // Check if there is a history for this medicine ~around~ this time today
        boolean taken = history.stream().anyMatch(h -> 
            h.getMedicineId() == medicineId && 
            h.getScheduledTime() != null &&
            Math.abs(java.time.Duration.between(h.getScheduledTime(), r.getReminderTime()).toMinutes()) < 60
        );
        
        if(taken) r.setStatus(Reminder.Status.TAKEN);
        else if(r.getReminderTime().isBefore(LocalDateTime.now())) r.setStatus(Reminder.Status.MISSED);
        else r.setStatus(Reminder.Status.PENDING);
        
        notifyDataChanged();
    }
    
    public List<Reminder> getDailyReminders() {
         generateDailyReminders(); // Refresh on get
         return dailyReminders;
    }
    
    public long getPendingCount() {
        return dailyReminders.stream()
            .filter(r -> r.getStatus() == Reminder.Status.PENDING)
            .count();
    }

    public void markAsTaken(Reminder r, Medicine m) {
        // Logic to add to history and decrement stock
        if(historyController != null) {
            DoseHistory h = new DoseHistory(m.getId(), r.getReminderTime(), DoseHistory.STATUS_TAKEN);
            h.setTakenTime(LocalDateTime.now());
            historyController.addHistory(h);
        }
        if(medicineController != null) {
            // Find schedule to get dose amount? 
            // Simplified: decrease by 1 or use schedule info if attached
            int newQty = Math.max(0, m.getStockQuantity() - 1);
            medicineController.updateStock(m.getId(), newQty, "Dose Taken");
        }
        r.setStatus(Reminder.Status.TAKEN);
        notifyDataChanged();
    }
    
    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }
    
    private void notifyDataChanged() {
        for (DataChangeListener l : listeners) {
            l.onReminderDataChanged();
        }
    }
}
