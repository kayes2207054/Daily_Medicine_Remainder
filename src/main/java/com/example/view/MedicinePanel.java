package com.example.view;

import com.example.controller.MedicineController;
import com.example.model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicinePanel extends JPanel {
    private MedicineController controller;
    private JTable medicineTable;
    private DefaultTableModel tableModel;

    public MedicinePanel(MedicineController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Medicine Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "Dosage", "Frequency"};
        tableModel = new DefaultTableModel(columns, 0);
        medicineTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        add(scrollPane, BorderLayout.CENTER);
        
        loadMedicines();
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Medicine");
        addButton.addActionListener(e -> showAddDialog());
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMedicines() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = controller.getAllMedicines();
        for (Medicine med : medicines) {
            tableModel.addRow(new Object[]{med.getId(), med.getName(), med.getDosage(), med.getFrequency()});
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add Medicine");
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setLocationRelativeTo(this);
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        
        JLabel dosageLabel = new JLabel("Dosage:");
        JTextField dosageField = new JTextField();
        
        JLabel frequencyLabel = new JLabel("Frequency:");
        JTextField frequencyField = new JTextField();
        
        JLabel instructionsLabel = new JLabel("Instructions:");
        JTextField instructionsField = new JTextField();
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String dosage = dosageField.getText();
            String frequency = frequencyField.getText();
            String instructions = instructionsField.getText();
            
            if (!name.isEmpty()) {
                Medicine medicine = new Medicine(name, dosage, frequency, instructions);
                controller.addMedicine(medicine);
                loadMedicines();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Name is required!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(nameLabel);
        dialog.add(nameField);
        dialog.add(dosageLabel);
        dialog.add(dosageField);
        dialog.add(frequencyLabel);
        dialog.add(frequencyField);
        dialog.add(instructionsLabel);
        dialog.add(instructionsField);
        dialog.add(saveButton);
        dialog.add(cancelButton);
        
        dialog.setVisible(true);
    }
}
