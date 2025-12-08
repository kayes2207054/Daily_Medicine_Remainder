package com.example.controller;

import com.example.model.Inventory;
import java.util.ArrayList;

public class InventoryController {
    private ArrayList<Inventory> inventoryList;

    public InventoryController() {
        this.inventoryList = new ArrayList<>();
    }

    public void add(Inventory item) {
        // TODO: Implement add logic
    }

    public void delete(String medicineName) {
        // TODO: Implement delete logic
    }

    public void edit(String medicineName, Inventory updatedItem) {
        // TODO: Implement edit logic
    }

    public ArrayList<Inventory> getAll() {
        return inventoryList;
    }
}
