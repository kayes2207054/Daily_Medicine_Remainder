package com.example.view;

import com.example.controller.*;
import com.example.model.DoseHistory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Dashboard Panel with Statistics and Charts
 */
public class DashboardPanel extends JPanel {
    private final MedicineController medicineController;
    private final ReminderController reminderController;
    private final InventoryController inventoryController;
    private final HistoryController historyController;
    private Timer refreshTimer;

    public DashboardPanel(MedicineController medicineController, ReminderController reminderController,
                          InventoryController inventoryController, HistoryController historyController) {
        this.medicineController = medicineController;
        this.reminderController = reminderController;
        this.inventoryController = inventoryController;
        this.historyController = historyController;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        refreshDashboard();
        startAutoRefresh();
    }

    private void refreshDashboard() {
        removeAll();
        add(createWelcomePanel(), BorderLayout.NORTH);
        add(createStatsPanel(), BorderLayout.CENTER);
        add(createRecentActivityPanel(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("🏥 DailyDose Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(52, 73, 94));

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(127, 140, 141));

        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(dateLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Statistics
        int totalMedicines = medicineController.getTotalMedicines();
        int pendingReminders = reminderController.getPendingCount();
        int lowStock = inventoryController.getLowStockItems().size();
        int takenToday = historyController.getTakenTodayCount();
        int missedToday = historyController.getMissedTodayCount();
        int totalDoses = takenToday + missedToday;
        double adherence = totalDoses > 0 ? (takenToday * 100.0 / totalDoses) : 0;

        panel.add(createStatCard("💊 Total Medicines", String.valueOf(totalMedicines), new Color(52, 152, 219)));
        panel.add(createStatCard("⏰ Pending Reminders", String.valueOf(pendingReminders), new Color(241, 196, 15)));
        panel.add(createStatCard("📦 Low Stock Items", String.valueOf(lowStock), new Color(231, 76, 60)));
        panel.add(createStatCard("✅ Taken Today", String.valueOf(takenToday), new Color(46, 204, 113)));
        panel.add(createStatCard("❌ Missed Today", String.valueOf(missedToday), new Color(231, 76, 60)));
        panel.add(createStatCard("📊 Total Doses Today", String.valueOf(totalDoses), new Color(155, 89, 182)));
        panel.add(createStatCard("📈 Adherence Rate", String.format("%.1f%%", adherence), new Color(26, 188, 156)));
        panel.add(createStatCard("📅 Active Days", "30 days", new Color(52, 73, 94)));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 200));

        JLabel titleLabel = new JLabel("📋 Recent Activity (Last 7 Days)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));

        String[] columns = {"Date", "Medicine", "Time", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(236, 240, 241));

        // Custom renderer for status column
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("TAKEN".equals(status)) {
                    c.setForeground(new Color(46, 204, 113));
                } else if ("MISSED".equals(status)) {
                    c.setForeground(new Color(231, 76, 60));
                } else {
                    c.setForeground(new Color(241, 196, 15));
                }
                return c;
            }
        });

        // Load recent history
        List<DoseHistory> recentHistory = historyController.getRecentHistory(7);
        for (DoseHistory h : recentHistory) {
            tableModel.addRow(new Object[]{
                    h.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    h.getMedicineName(),
                    h.getTime().toString(),
                    h.getStatus()
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void startAutoRefresh() {
        // Auto-refresh dashboard every 30 seconds
        refreshTimer = new Timer(30000, e -> refreshDashboard());
        refreshTimer.start();
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    public void cleanup() {
        stopAutoRefresh();
    }
}
