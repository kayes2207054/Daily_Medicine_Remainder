package com.example.controller;

import com.example.model.Medicine;
import java.util.ArrayList;

public class MedicineController {
    private ArrayList<Medicine> medicines;

    public MedicineController() {
        this.medicines = new ArrayList<>();
    }

    public void add(Medicine medicine) {
        // TODO: Implement add logic
    }

    public void delete(String medicineName) {
        // TODO: Implement delete logic
    }

    public void edit(String medicineName, Medicine updatedMedicine) {
        // TODO: Implement edit logic
    }

    public ArrayList<Medicine> getAll() {
        return medicines;
    }
}
