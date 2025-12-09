package com.example.view;

import com.example.controller.MedicineController;
import com.example.controller.ReminderController;
import com.example.model.Medicine;
import com.example.model.Reminder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

public class ReminderPanel extends JPanel {
    private ReminderController controller;
    private MedicineController medicineController;
    private JTable reminderTable;
    private DefaultTableModel tableModel;

    public ReminderPanel(ReminderController controller, MedicineController medicineController) {
        this.controller = controller;
        this.medicineController = medicineController;
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Reminder Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Medicine", "Time", "Type", "Taken"};
        tableModel = new DefaultTableModel(columns, 0);
        reminderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reminderTable);
        add(scrollPane, BorderLayout.CENTER);
        
        loadReminders();
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Reminder");
        addButton.addActionListener(e -> showAddDialog());
        
        JButton takenButton = new JButton("✓ Mark as Taken");
        takenButton.setBackground(new Color(46, 204, 113));
        takenButton.setForeground(Color.WHITE);
        takenButton.addActionListener(e -> markAsTaken());
        
        JButton notTakenButton = new JButton("✗ Mark as Not Taken");
        notTakenButton.setBackground(new Color(231, 76, 60));
        notTakenButton.setForeground(Color.WHITE);
        notTakenButton.addActionListener(e -> markAsNotTaken());
        
        buttonPanel.add(addButton);
        buttonPanel.add(takenButton);
        buttonPanel.add(notTakenButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReminders() {
        tableModel.setRowCount(0);
        List<Reminder> reminders = controller.getAllReminders();
        for (Reminder r : reminders) {
            tableModel.addRow(new Object[]{r.getId(), r.getMedicineName(), r.getTime(), r.getReminderType(), r.isTaken() ? "Yes" : "No"});
        }
    }

    private void markAsTaken() {
        int selectedRow = reminderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reminder!");
            return;
        }
        
        int reminderId = (int) tableModel.getValueAt(selectedRow, 0);
        Reminder reminder = controller.getReminderById(reminderId);
        
        if (reminder != null) {
            reminder.setTaken(true);
            controller.updateReminder(reminder);
            loadReminders();
            JOptionPane.showMessageDialog(this, "Medicine marked as TAKEN!");
        }
    }

    private void markAsNotTaken() {
        int selectedRow = reminderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reminder!");
            return;
        }
        
        int reminderId = (int) tableModel.getValueAt(selectedRow, 0);
        Reminder reminder = controller.getReminderById(reminderId);
        
        if (reminder != null) {
            reminder.setTaken(false);
            controller.updateReminder(reminder);
            loadReminders();
            JOptionPane.showMessageDialog(this, "Medicine marked as NOT TAKEN!");
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add Reminder");
        dialog.setSize(400, 250);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setLocationRelativeTo(this);
        
        JLabel medicineLabel = new JLabel("Medicine:");
        List<Medicine> medicines = medicineController.getAllMedicines();
        String[] medicineNames = medicines.stream().map(Medicine::getName).toArray(String[]::new);
        JComboBox<String> medicineCombo = new JComboBox<>(medicineNames);
        
        JLabel timeLabel = new JLabel("Time (HH:MM):");
        JTextField timeField = new JTextField();
        
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"morning", "noon", "evening", "custom"});
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String medicineName = (String) medicineCombo.getSelectedItem();
            String time = timeField.getText();
            String type = (String) typeCombo.getSelectedItem();
            
            if (medicineName != null && !time.isEmpty()) {
                try {
                    LocalTime parsedTime = LocalTime.parse(time);
                    Medicine med = medicineController.getMedicineByName(medicineName);
                    Reminder reminder = new Reminder(med.getId(), medicineName, parsedTime, type);
                    controller.addReminder(reminder);
                    loadReminders();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid time format!");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(medicineLabel);
        dialog.add(medicineCombo);
        dialog.add(timeLabel);
        dialog.add(timeField);
        dialog.add(typeLabel);
        dialog.add(typeCombo);
        dialog.add(saveButton);
        dialog.add(cancelButton);
        
        dialog.setVisible(true);
    }
}
