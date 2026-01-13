package com.example.view;

import com.example.controller.*;
import com.example.model.DoseHistory;
import com.example.model.Medicine;
import com.example.utils.DataChangeListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel implements DataChangeListener {
    private MedicineController medicineController;
    private ReminderController reminderController;
    private HistoryController historyController;
    
    // Stats labels
    private JLabel totalMedsLabel;
    private JLabel pendingLabel;
    private JLabel takenLabel;
    private JLabel missedLabel;
    
    // Tables
    private JTable historyTable;
    private JTable lowStockTable;
    private DefaultTableModel historyModel;
    private DefaultTableModel lowStockModel;

    public DashboardPanel(MedicineController medController, ReminderController remController, HistoryController histController) {
        this.medicineController = medController;
        this.reminderController = remController;
        this.historyController = histController;
        
        // Listen to changes
        this.medicineController.addDataChangeListener(this);
        this.reminderController.addDataChangeListener(this);
        this.historyController.addDataChangeListener(this);
        
        setLayout(new BorderLayout(10, 10));
        setBackground(ModernUIUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        initComponents();
        refreshData();
    }
    
    private void initComponents() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Center Container with padding
        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.setBackground(ModernUIUtils.BACKGROUND);
        centerContainer.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Stats Cards Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        totalMedsLabel = createStatCard(statsPanel, "💊 Total Medicines", ModernUIUtils.PRIMARY, "0");
        pendingLabel = createStatCard(statsPanel, "⏰ Pending Today", ModernUIUtils.WARNING, "0");
        takenLabel = createStatCard(statsPanel, "✅ Taken Today", ModernUIUtils.SUCCESS, "0");
        missedLabel = createStatCard(statsPanel, "❌ Missed Today", ModernUIUtils.DANGER, "0");
        centerContainer.add(statsPanel, BorderLayout.NORTH);
        
        // Content Panel (Tables)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        
        // Recent History Table
        JPanel historyPanel = createTableCard("📋 Recent Activity", new String[]{"Medicine", "Time", "Status"});
        historyModel = (DefaultTableModel) historyTable.getModel();
        historyTable.getColumnModel().getColumn(2).setCellRenderer(new StatusRenderer());
        
        // Low Stock Table
        JPanel stockPanel = createTableCard("⚠️ Low Stock Alerts", new String[]{"Medicine", "Stock", "Threshold"});
        lowStockModel = (DefaultTableModel) lowStockTable.getModel();
        lowStockTable.getColumnModel().getColumn(1).setCellRenderer(new StockRenderer());
        
        contentPanel.add(historyPanel);
        contentPanel.add(stockPanel);
        
        centerContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(centerContainer, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIUtils.PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🏠");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        leftPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        JLabel welcomeLabel = new JLabel("Welcome back! Here's your health overview");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(200, 210, 255));
        panel.add(welcomeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JLabel createStatCard(JPanel container, String title, Color color, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        
        // Color accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(color);
        accentBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(accentBar);
        card.add(Box.createVerticalStrut(12));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(ModernUIUtils.TEXT_SECONDARY);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLbl);
        
        card.add(Box.createVerticalStrut(8));
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setForeground(color);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLbl);
        
        container.add(card);
        return valueLbl;
    }
    
    private JPanel createTableCard(String title, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(250, 250, 252));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 235, 240)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(ModernUIUtils.TEXT_PRIMARY);
        header.add(titleLbl, BorderLayout.WEST);
        
        panel.add(header, BorderLayout.NORTH);
        
        // Table
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        ModernUIUtils.styleTable(table);
        
        // Store reference based on title
        if (title.contains("Recent")) {
            historyTable = table;
        } else {
            lowStockTable = table;
        }
        
        JScrollPane scrollPane = ModernUIUtils.createModernScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshData() {
        if(medicineController == null) return;
        
        // Update Stats
        totalMedsLabel.setText(String.valueOf(medicineController.getTotalMedicinesCount()));
        pendingLabel.setText(String.valueOf(reminderController.getPendingCount()));
        takenLabel.setText(String.valueOf(historyController.getTakenTodayCount()));
        missedLabel.setText(String.valueOf(historyController.getMissedTodayCount()));
        
        // Update History
        updateHistoryTable();
        
        // Update Low Stock
        updateLowStockTable();
    }
    
    private void updateHistoryTable() {
        historyModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd HH:mm");
        List<DoseHistory> recent = historyController.getRecentHistory(10);
        
        for(DoseHistory h : recent) {
             String timeStr = h.getTakenTime() != null ? h.getTakenTime().format(fmt) 
                            : (h.getScheduledTime() != null ? h.getScheduledTime().format(fmt) : "N/A");
             
             historyModel.addRow(new Object[]{
                 h.getMedicineName() != null ? h.getMedicineName() : "Unknown",
                 timeStr,
                 h.getStatus()
             });
        }
    }
    
    private void updateLowStockTable() {
        lowStockModel.setRowCount(0);
        List<Medicine> lowStock = medicineController.getLowStockMedicines();
        for(Medicine m : lowStock) {
            lowStockModel.addRow(new Object[]{
                m.getName(),
                m.getStockQuantity(),
                m.getLowStockThreshold()
            });
        }
    }
    
    // Custom renderer for status column
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = value != null ? value.toString() : "";
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setOpaque(true);
            
            if (!isSelected) {
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                switch (status.toUpperCase()) {
                    case "TAKEN":
                        label.setForeground(ModernUIUtils.SUCCESS);
                        break;
                    case "MISSED":
                        label.setForeground(ModernUIUtils.DANGER);
                        break;
                    case "PENDING":
                        label.setForeground(ModernUIUtils.WARNING);
                        break;
                    default:
                        label.setForeground(ModernUIUtils.TEXT_PRIMARY);
                }
            }
            return label;
        }
    }
    
    // Custom renderer for stock column
    private class StockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setOpaque(true);
            
            if (!isSelected) {
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                int qty = value != null ? (int) value : 0;
                if (qty == 0) {
                    label.setForeground(ModernUIUtils.DANGER);
                } else if (qty <= 5) {
                    label.setForeground(ModernUIUtils.WARNING);
                } else {
                    label.setForeground(ModernUIUtils.SUCCESS);
                }
            }
            return label;
        }
    }
    
    @Override
    public void onMedicineDataChanged() {
        refreshData();
    }

    @Override
    public void onReminderDataChanged() {
        refreshData();
    }

    @Override
    public void onInventoryDataChanged() {
        refreshData();
    }

    @Override
    public void onHistoryDataChanged() {
        refreshData();
    }
}
