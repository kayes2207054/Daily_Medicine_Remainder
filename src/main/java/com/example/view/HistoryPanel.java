package com.example.view;

import com.example.controller.HistoryController;
import com.example.model.DoseHistory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private HistoryController controller;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public HistoryPanel(HistoryController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Dose History & Reports", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Medicine", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);
        
        loadHistory();
        
        // Button panel with filter options
        JPanel buttonPanel = new JPanel();
        JButton todayBtn = new JButton("Today");
        todayBtn.addActionListener(e -> showTodayHistory());
        JButton weekBtn = new JButton("This Week");
        weekBtn.addActionListener(e -> showWeekHistory());
        JButton monthBtn = new JButton("This Month");
        monthBtn.addActionListener(e -> showMonthHistory());
        JButton allBtn = new JButton("All");
        allBtn.addActionListener(e -> loadHistory());
        
        JButton takenBtn = new JButton("✓ Mark as Taken");
        takenBtn.setBackground(new Color(46, 204, 113));
        takenBtn.setForeground(Color.WHITE);
        takenBtn.addActionListener(e -> markAsTaken());
        
        JButton missedBtn = new JButton("✗ Mark as Missed");
        missedBtn.setBackground(new Color(231, 76, 60));
        missedBtn.setForeground(Color.WHITE);
        missedBtn.addActionListener(e -> markAsMissed());
        
        JButton pendingBtn = new JButton("? Mark as Pending");
        pendingBtn.setBackground(new Color(241, 196, 15));
        pendingBtn.setForeground(Color.WHITE);
        pendingBtn.addActionListener(e -> markAsPending());
        
        buttonPanel.add(todayBtn);
        buttonPanel.add(weekBtn);
        buttonPanel.add(monthBtn);
        buttonPanel.add(allBtn);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(takenBtn);
        buttonPanel.add(missedBtn);
        buttonPanel.add(pendingBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadHistory() {
        tableModel.setRowCount(0);
        List<DoseHistory> histories = controller.getAllHistory();
        for (DoseHistory h : histories) {
            tableModel.addRow(new Object[]{h.getId(), h.getMedicineName(), h.getDate(), h.getTime(), h.getStatus()});
        }
    }

    private void markAsTaken() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dose history record!");
            return;
        }
        
        int historyId = (int) tableModel.getValueAt(selectedRow, 0);
        if (controller.markDoseAsTaken(historyId)) {
            loadHistory();
            JOptionPane.showMessageDialog(this, "Dose marked as TAKEN!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update dose status!");
        }
    }

    private void markAsMissed() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dose history record!");
            return;
        }
        
        int historyId = (int) tableModel.getValueAt(selectedRow, 0);
        if (controller.markDoseAsMissed(historyId)) {
            loadHistory();
            JOptionPane.showMessageDialog(this, "Dose marked as MISSED!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update dose status!");
        }
    }

    private void markAsPending() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dose history record!");
            return;
        }
        
        int historyId = (int) tableModel.getValueAt(selectedRow, 0);
        if (controller.markDoseAsPending(historyId)) {
            loadHistory();
            JOptionPane.showMessageDialog(this, "Dose marked as PENDING!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update dose status!");
        }
    }

    private void showTodayHistory() {
        tableModel.setRowCount(0);
        List<DoseHistory> histories = controller.getTodayHistory();
        for (DoseHistory h : histories) {
            tableModel.addRow(new Object[]{h.getId(), h.getMedicineName(), h.getDate(), h.getTime(), h.getStatus()});
        }
    }

    private void showWeekHistory() {
        tableModel.setRowCount(0);
        List<DoseHistory> histories = controller.getThisWeekHistory();
        for (DoseHistory h : histories) {
            tableModel.addRow(new Object[]{h.getId(), h.getMedicineName(), h.getDate(), h.getTime(), h.getStatus()});
        }
    }

    private void showMonthHistory() {
        tableModel.setRowCount(0);
        List<DoseHistory> histories = controller.getThisMonthHistory();
        for (DoseHistory h : histories) {
            tableModel.addRow(new Object[]{h.getId(), h.getMedicineName(), h.getDate(), h.getTime(), h.getStatus()});
        }
    }
}
