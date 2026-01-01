package com.example.view;

import com.example.controller.ReminderController;
import com.example.model.Reminder;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Enhanced Reminder Panel with full CRUD operations and alarm controls
 */
public class ReminderPanel extends JPanel {
    private final ReminderController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private Timer refreshTimer;

    public ReminderPanel(ReminderController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        refreshTable();
        startAutoRefresh();
    }

    private void initComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("⏰ Reminder Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));

        JLabel subtitleLabel = new JLabel("Set alarms for your medicines");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Table columns
        String[] columns = {"ID", "Medicine Name", "Reminder Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Custom renderer for status column
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                if (!isSelected) {
                    String status = (String) value;
                    if ("PENDING".equals(status)) {
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(243, 156, 18));
                    } else if ("TAKEN".equals(status)) {
                        c.setBackground(new Color(212, 237, 218));
                        c.setForeground(new Color(46, 204, 113));
                    } else if ("MISSED".equals(status)) {
                        c.setBackground(new Color(248, 215, 218));
                        c.setForeground(new Color(231, 76, 60));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);

        JButton addButton = createStyledButton("➕ Add Reminder", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddReminderDialog());
        panel.add(addButton);

        JButton editButton = createStyledButton("✏️ Edit", new Color(52, 152, 219));
        editButton.addActionListener(e -> editReminder());
        panel.add(editButton);

        JButton deleteButton = createStyledButton("🗑️ Delete", new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteReminder());
        panel.add(deleteButton);

        JButton markTakenButton = createStyledButton("✅ Mark Taken", new Color(46, 204, 113));
        markTakenButton.addActionListener(e -> markAsTaken());
        panel.add(markTakenButton);

        JButton snoozeButton = createStyledButton("⏰ Snooze 5min", new Color(241, 196, 15));
        snoozeButton.addActionListener(e -> snoozeReminder());
        panel.add(snoozeButton);

        JButton refreshButton = createStyledButton("🔄 Refresh", new Color(155, 89, 182));
        refreshButton.addActionListener(e -> refreshTable());
        panel.add(refreshButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void showAddReminderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Reminder", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Medicine Name:"));
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        JTextField dateField = new JTextField(LocalDate.now().toString());
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Time (HH:mm):"));
        JTextField timeField = new JTextField("08:00");
        timeField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(timeField);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = createStyledButton("💾 Save", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String dateStr = dateField.getText().trim();
                String timeStr = timeField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter medicine name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate date = LocalDate.parse(dateStr);
                LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                LocalDateTime reminderTime = LocalDateTime.of(date, time);

                // Validate not in past
                if (reminderTime.isBefore(LocalDateTime.now())) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                        "This time is in the past. Set anyway?",
                        "Past Time Warning", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (confirm != JOptionPane.YES_OPTION) return;
                }

                Reminder reminder = new Reminder(name, reminderTime);
                Reminder addedReminder = controller.addReminder(reminder);

                if (addedReminder != null) {
                    JOptionPane.showMessageDialog(dialog, "Reminder added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add reminder!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time format!\nDate: yyyy-MM-dd\nTime: HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editReminder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reminder to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Reminder reminder = controller.getReminderById(id);

        if (reminder == null) {
            JOptionPane.showMessageDialog(this, "Reminder not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Reminder", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Medicine Name:"));
        JTextField nameField = new JTextField(reminder.getMedicineName());
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        JTextField dateField = new JTextField(reminder.getReminderTime().toLocalDate().toString());
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Time (HH:mm):"));
        JTextField timeField = new JTextField(reminder.getReminderTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(timeField);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = createStyledButton("💾 Update", new Color(52, 152, 219));
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String dateStr = dateField.getText().trim();
                String timeStr = timeField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter medicine name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate date = LocalDate.parse(dateStr);
                LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                LocalDateTime reminderTime = LocalDateTime.of(date, time);

                reminder.setMedicineName(name);
                reminder.setReminderTime(reminderTime);

                boolean success = controller.updateReminder(reminder);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Reminder updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update reminder!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time format!\nDate: yyyy-MM-dd\nTime: HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = createStyledButton("❌ Cancel", new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteReminder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reminder to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the reminder for '" + medicineName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteReminder(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Reminder deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete reminder!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markAsTaken() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reminder to mark as taken!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        controller.markTaken(id);
        JOptionPane.showMessageDialog(this, "Reminder marked as TAKEN!", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshTable();
    }

    private void snoozeReminder() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reminder to snooze!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        controller.snooze(id, 5); // Snooze for 5 minutes
        JOptionPane.showMessageDialog(this, "Reminder snoozed for 5 minutes!", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Reminder> reminders = controller.getAllReminders();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Reminder r : reminders) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getMedicineName(),
                r.getReminderTime().format(formatter),
                r.getStatus().toString()
            });
        }
    }

    private void startAutoRefresh() {
        // Auto-refresh every 10 seconds to show updated statuses
        refreshTimer = new Timer(10000, e -> refreshTable());
        refreshTimer.start();
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    /**
     * Cleanup method - call when panel is removed
     */
    public void cleanup() {
        stopAutoRefresh();
    }
}
