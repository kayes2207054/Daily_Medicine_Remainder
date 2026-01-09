package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.Medicine;
import com.example.utils.DataChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MedicineController Class
 * Handles all business logic for medicine operations.
 * Communicates between view and database for CRUD operations.
 */
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

    /**
     * Load all medicines from database
     */
    public void loadMedicines() {
        this.medicines = dbManager.getAllMedicines();
        logger.info("Loaded " + medicines.size() + " medicines from database");
    }

    /**
     * Add new medicine
     */
    public int addMedicine(Medicine medicine) {
        if (medicine == null || medicine.getName() == null || medicine.getName().isEmpty()) {
            logger.warn("Cannot add medicine with empty name");
            return -1;
        }
        
        // Check if medicine already exists
        if (medicineExists(medicine.getName())) {
            logger.warn("Medicine already exists: " + medicine.getName());
            return -1;
        }

        int id = dbManager.addMedicine(medicine);
        if (id > 0) {
            medicine.setId(id);
            medicines.add(medicine);
            logger.info("Medicine added: " + medicine.getName() + " with ID: " + id);
            notifyMedicineDataChanged();
        }
        return id;
    }

    /**
     * Update medicine
     */
    public boolean updateMedicine(Medicine medicine) {
        if (medicine == null || medicine.getId() <= 0) {
            logger.warn("Invalid medicine for update");
            return false;
        }

        boolean success = dbManager.updateMedicine(medicine);
        if (success) {
            // Update in local list
            for (int i = 0; i < medicines.size(); i++) {
                if (medicines.get(i).getId() == medicine.getId()) {
                    medicines.set(i, medicine);
                    break;
                }
            }
            logger.info("Medicine updated: " + medicine.getName());
            notifyMedicineDataChanged();
        }
        return success;
    }

    /**
     * Delete medicine by ID
     */
    public boolean deleteMedicine(int medicineId) {
        boolean success = dbManager.deleteMedicine(medicineId);
        if (success) {
            medicines.removeIf(m -> m.getId() == medicineId);
            logger.info("Medicine deleted with ID: " + medicineId);
            notifyMedicineDataChanged();
        }
        return success;
    }

    /**
     * Delete medicine by name
     */
    public boolean deleteMedicineByName(String medicineName) {
        Medicine medicine = getMedicineByName(medicineName);
        if (medicine != null) {
            return deleteMedicine(medicine.getId());
        }
        return false;
    }

    /**
     * Get all medicines
     */
    public List<Medicine> getAllMedicines() {
        return new ArrayList<>(medicines);
    }

    /**
     * Get medicine by ID
     */
    public Medicine getMedicineById(int id) {
        return dbManager.getMedicineById(id);
    }

    /**
     * Get medicine by name
     */
    public Medicine getMedicineByName(String name) {
        return medicines.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Search medicines by name (partial match)
     */
    public List<Medicine> searchMedicines(String query) {
        String lowerQuery = query.toLowerCase();
        return medicines.stream()
                .filter(m -> m.getName().toLowerCase().contains(lowerQuery) ||
                        m.getDosage().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Sort medicines by name
     */
    public List<Medicine> sortByName() {
        return medicines.stream()
                .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Sort medicines by frequency (descending)
     */
    public List<Medicine> sortByFrequency() {
        return medicines.stream()
                .sorted((m1, m2) -> extractFrequency(m2) - extractFrequency(m1))
                .collect(Collectors.toList());
    }

    /**
     * Extract numeric frequency from string
     */
    private int extractFrequency(Medicine medicine) {
        String freq = medicine.getFrequency();
        if (freq != null && !freq.isEmpty()) {
            try {
                return Integer.parseInt(freq.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Check if medicine exists
     */
    public boolean medicineExists(String medicineName) {
        return medicines.stream()
                .anyMatch(m -> m.getName().equalsIgnoreCase(medicineName));
    }

    /**
     * Get total number of medicines
     */
    public int getTotalMedicines() {
        return medicines.size();
    }

    /**
     * Refresh medicines from database
     */
    public void refresh() {
        loadMedicines();
        logger.info("Medicine list refreshed");
    }
    
    /**
     * Add data change listener
     */
    public void addDataChangeListener(DataChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove data change listener
     */
    public void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that medicine data has changed
     */
    private void notifyMedicineDataChanged() {
        for (DataChangeListener listener : listeners) {
            try {
                listener.onMedicineDataChanged();
            } catch (Exception e) {
                logger.error("Error notifying listener", e);
            }
        }
    }
}

