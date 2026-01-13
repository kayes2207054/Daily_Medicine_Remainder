package com.example.view;

import com.example.controller.HistoryController;
import com.example.model.DoseHistory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * History Panel - View dose history records with modern UI
 */
public class HistoryPanel extends JPanel {
    private HistoryController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    
    public HistoryPanel(HistoryController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(ModernUIUtils.BACKGROUND);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);
        
        refreshTable();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIUtils.PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("📜");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        titlePanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Dose History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);
        
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setForeground(new Color(220, 220, 240));
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(filterLabel);
        
        filterCombo = new JComboBox<>(new String[]{"All", "Taken", "Missed", "Skipped"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(120, 32));
        filterCombo.addActionListener(e -> refreshTable());
        filterPanel.add(filterCombo);
        
        JButton refreshBtn = ModernUIUtils.createButton("🔄 Refresh", ModernUIUtils.SUCCESS);
        refreshBtn.setPreferredSize(new Dimension(110, 32));
        refreshBtn.addActionListener(e -> {
            controller.loadHistory();
            refreshTable();
        });
        filterPanel.add(refreshBtn);
        
        panel.add(filterPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        panel.setBackground(ModernUIUtils.BACKGROUND);
        
        String[] columns = {"ID", "Medicine", "Scheduled Time", "Taken Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        ModernUIUtils.styleTable(table);
        
        // Custom renderer for status column
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        
        JScrollPane scrollPane = ModernUIUtils.createModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        panel.setBackground(new Color(250, 250, 252));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUIUtils.BORDER));
        
        long taken = controller.getTakenTodayCount();
        long missed = controller.getMissedTodayCount();
        
        panel.add(createStatCard("✅ Taken Today", String.valueOf(taken), ModernUIUtils.SUCCESS));
        panel.add(createStatCard("❌ Missed Today", String.valueOf(missed), ModernUIUtils.DANGER));
        
        double rate = (taken + missed) > 0 ? (taken * 100.0 / (taken + missed)) : 0;
        panel.add(createStatCard("📊 Adherence Rate", String.format("%.1f%%", rate), ModernUIUtils.PRIMARY));
        
        return panel;
    }
    
    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(valueLabel);
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(ModernUIUtils.TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);
        
        return card;
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<DoseHistory> history = controller.getHistoryList();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        String filter = (String) filterCombo.getSelectedItem();
        
        for (DoseHistory h : history) {
            if (!"All".equals(filter) && !filter.equalsIgnoreCase(h.getStatus())) {
                continue;
            }
            
            tableModel.addRow(new Object[]{
                h.getId(),
                h.getMedicineName() != null ? h.getMedicineName() : "Unknown",
                h.getScheduledTime() != null ? h.getScheduledTime().format(dtf) : "-",
                h.getTakenTime() != null ? h.getTakenTime().format(dtf) : "-",
                h.getStatus(),
                h.getNotes() != null ? h.getNotes() : ""
            });
        }
    }
    
    // Custom renderer for status with color coding
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = value != null ? value.toString() : "";
            
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            if (!isSelected) {
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                
                switch (status.toUpperCase()) {
                    case "TAKEN":
                        label.setForeground(ModernUIUtils.SUCCESS);
                        break;
                    case "MISSED":
                        label.setForeground(ModernUIUtils.DANGER);
                        break;
                    case "SKIPPED":
                        label.setForeground(ModernUIUtils.WARNING);
                        break;
                    default:
                        label.setForeground(ModernUIUtils.TEXT_SECONDARY);
                }
            }
            
            return label;
        }
    }
}
