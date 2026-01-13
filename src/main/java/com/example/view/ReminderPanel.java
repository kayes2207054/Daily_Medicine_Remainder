package com.example.view;

import com.example.controller.MedicineController;
import com.example.controller.ReminderController;
import com.example.model.Medicine;
import com.example.model.Reminder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReminderPanel extends JPanel {
    private ReminderController controller;
    private MedicineController medicineController;
    private JTable table;
    private DefaultTableModel tableModel;
    
    public ReminderPanel(ReminderController controller) {
        this.controller = controller;
        this.medicineController = controller.getMedicineController();
        setLayout(new BorderLayout());
        setBackground(ModernUIUtils.BACKGROUND);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        refreshTable();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIUtils.PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("⏰");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        titlePanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Today's Reminders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        // Refresh button
        JButton refreshBtn = ModernUIUtils.createButton("🔄 Refresh", ModernUIUtils.SUCCESS);
        refreshBtn.setPreferredSize(new Dimension(110, 32));
        refreshBtn.addActionListener(e -> refreshTable());
        panel.add(refreshBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        panel.setBackground(ModernUIUtils.BACKGROUND);
        
        String[] columns = {"ID", "Medicine", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        ModernUIUtils.styleTable(table);
        
        // Custom renderer for status column
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        
        // Custom renderer for time column
        table.getColumnModel().getColumn(2).setCellRenderer(new TimeCellRenderer());
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        JScrollPane scrollPane = ModernUIUtils.createModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Add Schedule Button - Bright Green
        JButton btnAdd = new JButton("➕ Add Schedule");
        btnAdd.setPreferredSize(new Dimension(170, 45));
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 142, 60), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnAdd.addActionListener(e -> showAddScheduleDialog());
        panel.add(btnAdd);
        
        // Edit Schedule Button - Bright Blue
        JButton btnEdit = new JButton("✏️ Edit Schedule");
        btnEdit.setPreferredSize(new Dimension(170, 45));
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEdit.setFocusPainted(false);
        btnEdit.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(21, 101, 192), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnEdit.addActionListener(e -> showEditScheduleDialog());
        panel.add(btnEdit);
        
        // Mark as Taken Button - Bright Teal
        JButton btnTaken = new JButton("✅ Mark as Taken");
        btnTaken.setPreferredSize(new Dimension(180, 45));
        btnTaken.setBackground(new Color(0, 150, 136));
        btnTaken.setForeground(Color.WHITE);
        btnTaken.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTaken.setFocusPainted(false);
        btnTaken.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 121, 107), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnTaken.addActionListener(e -> markSelectedAsTaken());
        panel.add(btnTaken);
        
        // Delete Schedule Button - Bright Red
        JButton btnDelete = new JButton("🗑️ Delete Schedule");
        btnDelete.setPreferredSize(new Dimension(180, 45));
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(198, 40, 40), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnDelete.addActionListener(e -> deleteSchedule());
        panel.add(btnDelete);
        
        return panel;
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Reminder> list = controller.getDailyReminders();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        
        for(Reminder r : list) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getMedicineName(),
                r.getReminderTime().format(fmt),
                r.getStatus()
            });
        }
    }
    
    private void markSelectedAsTaken() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a reminder first.");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        String medName = (String) tableModel.getValueAt(row, 1);
        
        List<Reminder> list = controller.getDailyReminders();
        Reminder r = list.stream().filter(rem -> rem.getId() == id).findFirst().orElse(null);
        
        if (r != null && !"TAKEN".equals(r.getStatus())) {
            // Get the medicine object by name
            Medicine medicine = medicineController.getAllMedicines().stream()
                .filter(m -> m.getName().equals(r.getMedicineName()))
                .findFirst()
                .orElse(null);
            
            if (medicine != null) {
                controller.markAsTaken(r, medicine);
                refreshTable();
                showSuccess("Marked '" + medName + "' as taken!");
            } else {
                showWarning("Medicine not found for this reminder.");
            }
        } else if (r != null && "TAKEN".equals(r.getStatus())) {
            showInfo("This dose has already been taken.");
        }
    }
    
    private void skipSelectedDose() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a reminder first.");
            return;
        }
        
        String medName = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to skip the dose for '" + medName + "'?",
            "Skip Dose", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            showInfo("Dose skipped for '" + medName + "'.\n(Skip feature not yet implemented in controller)");
        }
    }
    
    private void snoozeSelectedReminder() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a reminder first.");
            return;
        }
        
        String medName = (String) tableModel.getValueAt(row, 1);
        showInfo("Reminder for '" + medName + "' snoozed for 15 minutes.\n(Snooze feature not yet implemented in controller)");
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Custom renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = value != null ? value.toString() : "";
            
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            label.setOpaque(true);
            
            if (!isSelected) {
                switch (status.toUpperCase()) {
                    case "TAKEN":
                        label.setBackground(new Color(232, 245, 233));
                        label.setForeground(ModernUIUtils.SUCCESS);
                        break;
                    case "MISSED":
                        label.setBackground(new Color(255, 235, 238));
                        label.setForeground(ModernUIUtils.DANGER);
                        break;
                    case "PENDING":
                        label.setBackground(new Color(255, 243, 224));
                        label.setForeground(ModernUIUtils.WARNING);
                        break;
                    default:
                        label.setBackground(new Color(227, 242, 253));
                        label.setForeground(ModernUIUtils.PRIMARY);
                }
            }
            
            return label;
        }
    }
    
    // Custom renderer for time column
    private class TimeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            label.setOpaque(true);
            
            if (!isSelected) {
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                label.setForeground(ModernUIUtils.PRIMARY);
            }
            
            return label;
        }
    }
    
    private void showAddScheduleDialog() {
        showInfo("Add Schedule: Please go to the 'Medicines' tab to add/edit medicine schedules.\nReminders are automatically generated from medicine schedules.");
    }
    
    private void showEditScheduleDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a reminder first.");
            return;
        }
        
        String medName = (String) tableModel.getValueAt(row, 1);
        showInfo("Edit Schedule: Please go to the 'Medicines' tab and edit '" + medName + "' to modify its schedule.\nReminders are automatically generated from medicine schedules.");
    }
    
    private void deleteSchedule() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a reminder first.");
            return;
        }
        
        String medName = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "To delete this schedule, you need to edit the medicine '" + medName + "' in the Medicines tab.\nDo you want to continue?",
            "Delete Schedule", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            showInfo("Please navigate to the 'Medicines' tab and edit '" + medName + "' to remove its schedule.");
        }
    }
}
