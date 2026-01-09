package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.Inventory;
import com.example.utils.DataChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InventoryController Class
 * Handles inventory management and stock tracking.
 * Monitors low stock levels and refill dates.
 */
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private DatabaseManager dbManager;
    private List<Inventory> inventoryList;
    private List<DataChangeListener> listeners = new ArrayList<>();

    public InventoryController() {
        this.dbManager = DatabaseManager.getInstance();
        this.inventoryList = new ArrayList<>();
        loadInventory();
    }

    /**
     * Load all inventory from database
     */
    public void loadInventory() {
        this.inventoryList = dbManager.getAllInventory();
        logger.info("Loaded " + inventoryList.size() + " inventory items from database");
    }

    /**
     * Add new inventory item
     */
    public int addInventory(Inventory inventory) {
        if (inventory == null || inventory.getMedicineName() == null) {
            logger.warn("Cannot add invalid inventory item");
            return -1;
        }

        int id = dbManager.addInventory(inventory);
        if (id > 0) {
            inventory.setId(id);
            inventoryList.add(inventory);
            logger.info("Inventory added for: " + inventory.getMedicineName());
            notifyInventoryDataChanged();
        }
        return id;
    }

    /**
     * Update inventory
     */
    public boolean updateInventory(Inventory inventory) {
        if (inventory == null || inventory.getId() <= 0) {
            logger.warn("Invalid inventory for update");
            return false;
        }

        boolean success = dbManager.updateInventory(inventory);
        if (success) {
            for (int i = 0; i < inventoryList.size(); i++) {
                if (inventoryList.get(i).getId() == inventory.getId()) {
                    inventoryList.set(i, inventory);
                    break;
                }
            }
            logger.info("Inventory updated for: " + inventory.getMedicineName());
            notifyInventoryDataChanged();
        }
        return success;
    }

    /**
     * Delete inventory by ID
     */
    public boolean deleteInventory(int inventoryId) {
        boolean success = dbManager.deleteInventory(inventoryId);
        if (success) {
            inventoryList.removeIf(inv -> inv.getId() == inventoryId);
            logger.info("Inventory deleted with ID: " + inventoryId);
            notifyInventoryDataChanged();
        }
        return success;
    }

    /**
     * Get all inventory
     */
    public List<Inventory> getAllInventory() {
        return new ArrayList<>(inventoryList);
    }

    /**
     * Get inventory by medicine ID
     */
    public Inventory getInventoryByMedicineId(int medicineId) {
        return dbManager.getInventoryByMedicineId(medicineId);
    }

    /**
     * Get low stock items (quantity <= threshold)
     */
    public List<Inventory> getLowStockItems() {
        return inventoryList.stream()
                .filter(Inventory::isLowStock)
                .collect(Collectors.toList());
    }

    /**
     * Get items that need refill soon
     */
    public List<Inventory> getItemsNeedingRefillSoon() {
        LocalDate today = LocalDate.now();
        return inventoryList.stream()
                .filter(inv -> inv.getEstimatedRefillDate() != null &&
                        inv.getEstimatedRefillDate().isBefore(today.plusDays(7)) &&
                        inv.getEstimatedRefillDate().isAfter(today))
                .collect(Collectors.toList());
    }

    /**
     * Decrease quantity after dose is taken
     */
    public boolean decreaseQuantity(int medicineId, int amount) {
        Inventory inv = inventoryList.stream()
                .filter(i -> i.getMedicineId() == medicineId)
                .findFirst()
                .orElse(null);

        if (inv != null) {
            inv.decreaseQuantity(amount);
            return updateInventory(inv);
        }
        return false;
    }

    /**
     * Refill medicine (set quantity and last refill date)
     */
    public boolean refillMedicine(int inventoryId, int newQuantity) {
        Inventory inv = inventoryList.stream()
                .filter(i -> i.getId() == inventoryId)
                .findFirst()
                .orElse(null);

        if (inv != null) {
            inv.setQuantity(newQuantity);
            inv.setLastRefillDate(LocalDate.now());
            boolean success = updateInventory(inv);
            if (success) {
                logger.info("Refill completed for: " + inv.getMedicineName());
            }
            return success;
        }
        return false;
    }

    /**
     * Get total quantity of all medicines
     */
    public int getTotalQuantity() {
        return inventoryList.stream().mapToInt(Inventory::getQuantity).sum();
    }

    /**
     * Get inventory by medicine name
     */
    public Inventory getInventoryByMedicineName(String medicineName) {
        return inventoryList.stream()
                .filter(inv -> inv.getMedicineName().equalsIgnoreCase(medicineName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Refresh inventory from database
     */
    public void refresh() {
        loadInventory();
        logger.info("Inventory list refreshed");
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
     * Notify all listeners that inventory data has changed
     */
    private void notifyInventoryDataChanged() {
        for (DataChangeListener listener : listeners) {
            try {
                listener.onInventoryDataChanged();
            } catch (Exception e) {
                logger.error("Error notifying listener", e);
            }
        }
    }
}
