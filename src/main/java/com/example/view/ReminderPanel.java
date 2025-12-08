package com.example.view;

import com.example.model.Reminder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ReminderPanel extends JPanel {
    private JTable reminderTable;
    private DefaultTableModel tableModel;
    private ArrayList<Reminder> reminders;

    public ReminderPanel() {
        reminders = new ArrayList<>();
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Reminder Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Medicine Name", "Time"};
        tableModel = new DefaultTableModel(columns, 0);
        reminderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reminderTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel medicineLabel = new JLabel("Medicine Name:");
        JTextField medicineField = new JTextField();
        
        JLabel timeLabel = new JLabel("Time (HH:MM):");
        JTextField timeField = new JTextField();
        
        JButton saveButton = new JButton("Save Reminder");
        saveButton.addActionListener(e -> {
            String medicineName = medicineField.getText();
            String time = timeField.getText();
            
            if (!medicineName.isEmpty() && !time.isEmpty()) {
                Reminder reminder = new Reminder(medicineName, time);
                reminders.add(reminder);
                tableModel.addRow(new Object[]{medicineName, time});
                medicineField.setText("");
                timeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
            }
        });
        
        formPanel.add(medicineLabel);
        formPanel.add(medicineField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);
        formPanel.add(new JLabel());
        formPanel.add(saveButton);
        
        add(formPanel, BorderLayout.SOUTH);
    }
}
