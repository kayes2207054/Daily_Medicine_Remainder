package com.example.view;

import com.example.controller.ReminderController;
import com.example.model.Reminder;
import com.example.model.Reminder.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReminderPanel extends JPanel {
    private final ReminderController controller;
    private JTable reminderTable;
    private DefaultTableModel tableModel;
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReminderPanel(ReminderController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Reminders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Medicine", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reminderTable = new JTable(tableModel);
        add(new JScrollPane(reminderTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Reminder");
        JButton deleteBtn = new JButton("Delete");
        JButton takenBtn = new JButton("Mark Taken");
        JButton missedBtn = new JButton("Mark Missed");

        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        takenBtn.addActionListener(e -> markSelected(Status.TAKEN));
        missedBtn.addActionListener(e -> markSelected(Status.MISSED));

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(takenBtn);
        buttonPanel.add(missedBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadReminders();
    }

    private void loadReminders() {
        tableModel.setRowCount(0);
        List<Reminder> reminders = controller.getRemindersSortedByTime();
        for (Reminder r : reminders) {
            tableModel.addRow(new Object[]{r.getId(), r.getMedicineName(),
                    r.getReminderTime().format(DISPLAY_FMT), r.getStatus()});
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add Reminder", true);
        dialog.setSize(400, 220);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        JTextField medField = new JTextField();
        JTextField timeField = new JTextField("08:00");

        dialog.add(new JLabel("Medicine Name:"));
        dialog.add(medField);
        dialog.add(new JLabel("Time (HH:mm):"));
        dialog.add(timeField);

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        dialog.add(save);
        dialog.add(cancel);

        save.addActionListener(e -> {
            String med = medField.getText().trim();
            String time = timeField.getText().trim();
            if (med.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields");
                return;
            }
            try {
                LocalTime lt = LocalTime.parse(time);
                LocalDateTime dt = LocalDate.now().atTime(lt);
                Reminder r = new Reminder(med, dt);
                controller.addReminder(r);
                loadReminders();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Use HH:mm format, e.g., 08:30");
            }
        });

        cancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int row = reminderTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a reminder first"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        controller.deleteReminder(id);
        loadReminders();
    }

    private void markSelected(Status status) {
        int row = reminderTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a reminder first");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (status == Status.TAKEN) {
            controller.markTaken(id);
        } else if (status == Status.MISSED) {
            controller.markMissed(id);
        }
        loadReminders();
    }
}
