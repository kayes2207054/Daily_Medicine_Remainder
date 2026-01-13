package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.Medicine;
import com.example.utils.DataChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MedicineController {
    private static final Logger logger = LoggerFactory.getLogger(MedicineController.class);
    private DatabaseManager dbManager;
    private List<Medicine> medicines;
    private List<DataChangeListener> listeners = new ArrayList<>();

    public MedicineController() {
        this.dbManager = DatabaseManager.getInstance();
        this.medicines = new ArrayList<>();
        loadMedicines();
    }

    public void loadMedicines() {
        this.medicines = dbManager.getAllMedicines();
    }

    public int addMedicine(Medicine medicine) {
        if (medicine == null || medicine.getName() == null || medicine.getName().isEmpty()) {
            return -1;
        }
        
        int id = dbManager.addMedicine(medicine);
        if (id > 0) {
            medicine.setId(id);
            // Re-fetch to get schedules properly set up if any ID generation happened
            medicines.add(medicine); 
            notifyMedicineDataChanged();
        }
        return id;
    }

    public boolean updateMedicine(Medicine medicine) {
        if (medicine == null || medicine.getId() <= 0) return false;

        boolean success = dbManager.updateMedicine(medicine);
        if (success) {
             loadMedicines(); // Reload to refresh everything
             notifyMedicineDataChanged();
        }
        return success;
    }

    public boolean deleteMedicine(int medicineId) {
        boolean success = dbManager.deleteMedicine(medicineId);
        if (success) {
            medicines.removeIf(m -> m.getId() == medicineId);
            notifyMedicineDataChanged();
        }
        return success;
    }

    public List<Medicine> getAllMedicines() {
        return new ArrayList<>(medicines); // Return copy
    }

    public List<Medicine> searchMedicines(String query, String frequency, String meal) {
        if ((query == null || query.isEmpty()) && (frequency == null || frequency.isEmpty()) && (meal == null || meal.isEmpty())) {
            return getAllMedicines();
        }
        return dbManager.searchMedicines(query, frequency, meal);
    }
    
    // Inventory Management
    public boolean updateStock(int medicineId, int newQuantity, String reason) {
        boolean success = dbManager.updateStock(medicineId, newQuantity, reason);
        if(success) {
            loadMedicines();
            notifyMedicineDataChanged();
        }
        return success;
    }

    // Observer Pattern
    public void addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyMedicineDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onMedicineDataChanged();
        }
    }

    public List<Medicine> getLowStockMedicines() {
        return medicines.stream()
            .filter(m -> m.getStockQuantity() <= m.getLowStockThreshold())
            .collect(Collectors.toList());
    }

    public int getTotalMedicinesCount() {
        return medicines.size();
    }
    
    public Medicine getMedicineById(int id) {
        return medicines.stream()
            .filter(m -> m.getId() == id)
            .findFirst()
            .orElse(null);
    }}