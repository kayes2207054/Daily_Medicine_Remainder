package com.example.view;

import com.example.controller.InventoryController;
import com.example.controller.MedicineController;
import com.example.model.Inventory;
import com.example.model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private InventoryController controller;
    private MedicineController medicineController;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryPanel(InventoryController controller, MedicineController medicineController) {
        this.controller = controller;
        this.medicineController = medicineController;
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Inventory Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Medicine", "Quantity", "Threshold", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        loadInventory();
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> showAddDialog());
        JButton updateButton = new JButton("Update Quantity");
        updateButton.addActionListener(e -> showUpdateDialog());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadInventory() {
        tableModel.setRowCount(0);
        List<Inventory> items = controller.getAllInventory();
        for (Inventory item : items) {
            String status = item.isLowStock() ? "LOW STOCK" : "OK";
            tableModel.addRow(new Object[]{item.getId(), item.getMedicineName(), item.getQuantity(), item.getThreshold(), status});
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add Inventory Item");
        dialog.setSize(400, 250);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setLocationRelativeTo(this);
        
        JLabel medicineLabel = new JLabel("Medicine:");
        List<Medicine> medicines = medicineController.getAllMedicines();
        String[] medicineNames = medicines.stream().map(Medicine::getName).toArray(String[]::new);
        JComboBox<String> medicineCombo = new JComboBox<>(medicineNames);
        
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        
        JLabel thresholdLabel = new JLabel("Low Stock Threshold:");
        JTextField thresholdField = new JTextField("10");
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String medicineName = (String) medicineCombo.getSelectedItem();
            String quantityStr = quantityField.getText();
            String thresholdStr = thresholdField.getText();
            
            if (medicineName != null && !quantityStr.isEmpty() && !thresholdStr.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    int threshold = Integer.parseInt(thresholdStr);
                    Medicine med = medicineController.getMedicineByName(medicineName);
                    Inventory item = new Inventory(med.getId(), medicineName, quantity, threshold, 1);
                    controller.addInventory(item);
                    loadInventory();
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid numbers!");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(medicineLabel);
        dialog.add(medicineCombo);
        dialog.add(quantityLabel);
        dialog.add(quantityField);
        dialog.add(thresholdLabel);
        dialog.add(thresholdField);
        dialog.add(saveButton);
        dialog.add(cancelButton);
        
        dialog.setVisible(true);
    }

    private void showUpdateDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to update!");
            return;
        }
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Update Quantity");
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(2, 2, 10, 10));
        dialog.setLocationRelativeTo(this);
        
        JLabel quantityLabel = new JLabel("New Quantity:");
        JTextField quantityField = new JTextField();
        
        JButton saveButton = new JButton("Update");
        saveButton.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText());
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                controller.refillMedicine(id, newQuantity);
                loadInventory();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(quantityLabel);
        dialog.add(quantityField);
        dialog.add(saveButton);
        dialog.add(cancelButton);
        
        dialog.setVisible(true);
    }
}
