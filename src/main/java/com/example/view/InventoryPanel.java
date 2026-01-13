package com.example.view;

import com.example.controller.MedicineController;
import com.example.model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Inventory Panel - View and manage medicine stock levels with modern UI
 */
public class InventoryPanel extends JPanel {
    private MedicineController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    
    public InventoryPanel(MedicineController controller) {
        this.controller = controller;
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
        
        JLabel iconLabel = new JLabel("📦");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        titlePanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        // Refresh button
        JButton refreshBtn = ModernUIUtils.createButton("🔄 Refresh", ModernUIUtils.SUCCESS);
        refreshBtn.setPreferredSize(new Dimension(110, 32));
        refreshBtn.addActionListener(e -> {
            controller.loadMedicines();
            refreshTable();
        });
        panel.add(refreshBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));
        panel.setBackground(ModernUIUtils.BACKGROUND);
        
        String[] columns = {"ID", "Medicine Name", "Stock Qty", "Low Threshold", "Status", "Unit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        ModernUIUtils.styleTable(table);
        
        // Custom renderer for status column
        table.getColumnModel().getColumn(4).setCellRenderer(new StockStatusRenderer());
        
        // Custom renderer for stock quantity (colorize based on threshold)
        table.getColumnModel().getColumn(2).setCellRenderer(new StockQtyRenderer());
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        
        JScrollPane scrollPane = ModernUIUtils.createModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Add Medicine Button - Bright Purple
        JButton addMedicineBtn = new JButton("💊 Add Medicine");
        addMedicineBtn.setPreferredSize(new Dimension(170, 45));
        addMedicineBtn.setBackground(new Color(156, 39, 176));
        addMedicineBtn.setForeground(Color.WHITE);
        addMedicineBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addMedicineBtn.setFocusPainted(false);
        addMedicineBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(123, 31, 162), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        addMedicineBtn.addActionListener(e -> showAddMedicineDialog());
        panel.add(addMedicineBtn);
        
        // Add Stock Button - Bright Green
        JButton addStockBtn = new JButton("➕ Add Stock");
        addStockBtn.setPreferredSize(new Dimension(150, 45));
        addStockBtn.setBackground(new Color(76, 175, 80));
        addStockBtn.setForeground(Color.WHITE);
        addStockBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addStockBtn.setFocusPainted(false);
        addStockBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 142, 60), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        addStockBtn.addActionListener(e -> showAddStockDialog());
        panel.add(addStockBtn);
        
        // Adjust Stock Button - Bright Blue
        JButton adjustBtn = new JButton("✏️ Adjust Stock");
        adjustBtn.setPreferredSize(new Dimension(160, 45));
        adjustBtn.setBackground(new Color(33, 150, 243));
        adjustBtn.setForeground(Color.WHITE);
        adjustBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        adjustBtn.setFocusPainted(false);
        adjustBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(21, 101, 192), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        adjustBtn.addActionListener(e -> showAdjustDialog());
        panel.add(adjustBtn);
        
        // Set Threshold Button - Bright Orange
        JButton thresholdBtn = new JButton("⚠️ Set Threshold");
        thresholdBtn.setPreferredSize(new Dimension(170, 45));
        thresholdBtn.setBackground(new Color(255, 152, 0));
        thresholdBtn.setForeground(Color.WHITE);
        thresholdBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        thresholdBtn.setFocusPainted(false);
        thresholdBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 124, 0), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        thresholdBtn.addActionListener(e -> showThresholdDialog());
        panel.add(thresholdBtn);
        
        // Delete Button - Bright Red
        JButton deleteBtn = new JButton("🗑️ Delete");
        deleteBtn.setPreferredSize(new Dimension(130, 45));
        deleteBtn.setBackground(new Color(244, 67, 54));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(198, 40, 40), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        deleteBtn.addActionListener(e -> deleteMedicine());
        panel.add(deleteBtn);
        
        return panel;
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = controller.getAllMedicines();
        
        for (Medicine m : medicines) {
            String status;
            if (m.getStockQuantity() == 0) {
                status = "OUT OF STOCK";
            } else if (m.getStockQuantity() <= m.getLowStockThreshold()) {
                status = "LOW STOCK";
            } else {
                status = "IN STOCK";
            }
            
            tableModel.addRow(new Object[]{
                m.getId(),
                m.getName(),
                m.getStockQuantity(),
                m.getLowStockThreshold(),
                status,
                m.getDoseUnit() != null ? m.getDoseUnit() : "units"
            });
        }
    }
    
    private void showAddStockDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a medicine first.");
            return;
        }
        
        int medId = (int) tableModel.getValueAt(row, 0);
        String medName = (String) tableModel.getValueAt(row, 1);
        int currentStock = (int) tableModel.getValueAt(row, 2);
        
        JPanel panel = createInputPanel(
            "Add stock for: " + medName,
            "Current stock: " + currentStock,
            "Quantity to add:"
        );
        
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JPanel) panel.getComponent(panel.getComponentCount() - 1)).add(spinner);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Stock", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int addQty = (int) spinner.getValue();
            int newQty = currentStock + addQty;
            controller.updateStock(medId, newQty, "Stock added: +" + addQty);
            refreshTable();
            showSuccess("Added " + addQty + " units to " + medName);
        }
    }
    
    private void showAdjustDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a medicine first.");
            return;
        }
        
        int medId = (int) tableModel.getValueAt(row, 0);
        String medName = (String) tableModel.getValueAt(row, 1);
        int currentStock = (int) tableModel.getValueAt(row, 2);
        
        JPanel panel = createInputPanel(
            "Adjust stock for: " + medName,
            "Current stock: " + currentStock,
            "New quantity:"
        );
        
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentStock, 0, 10000, 1));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JPanel) panel.getComponent(panel.getComponentCount() - 1)).add(spinner);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Adjust Stock", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int newQty = (int) spinner.getValue();
            controller.updateStock(medId, newQty, "Stock adjusted to: " + newQty);
            refreshTable();
            showSuccess("Stock adjusted to " + newQty + " for " + medName);
        }
    }
    
    private void showThresholdDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a medicine first.");
            return;
        }
        
        int medId = (int) tableModel.getValueAt(row, 0);
        String medName = (String) tableModel.getValueAt(row, 1);
        int currentThreshold = (int) tableModel.getValueAt(row, 3);
        
        JPanel panel = createInputPanel(
            "Set threshold for: " + medName,
            "Current threshold: " + currentThreshold,
            "New threshold:"
        );
        
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentThreshold, 0, 100, 1));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JPanel) panel.getComponent(panel.getComponentCount() - 1)).add(spinner);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Set Low Stock Threshold", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int newThreshold = (int) spinner.getValue();
            Medicine m = controller.getMedicineById(medId);
            if (m != null) {
                m.setLowStockThreshold(newThreshold);
                controller.updateMedicine(m);
                refreshTable();
                showSuccess("Threshold set to " + newThreshold + " for " + medName);
            }
        }
    }
    
    private JPanel createInputPanel(String title, String subtitle, String inputLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLbl);
        
        panel.add(Box.createVerticalStrut(5));
        
        JLabel subtitleLbl = new JLabel(subtitle);
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLbl.setForeground(ModernUIUtils.TEXT_SECONDARY);
        subtitleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitleLbl);
        
        panel.add(Box.createVerticalStrut(15));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(new JLabel(inputLabel));
        panel.add(inputPanel);
        
        return panel;
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Custom renderer for status column
    private class StockStatusRenderer extends DefaultTableCellRenderer {
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
                switch (status) {
                    case "OUT OF STOCK":
                        label.setBackground(new Color(255, 235, 238));
                        label.setForeground(ModernUIUtils.DANGER);
                        break;
                    case "LOW STOCK":
                        label.setBackground(new Color(255, 243, 224));
                        label.setForeground(ModernUIUtils.WARNING);
                        break;
                    default:
                        label.setBackground(new Color(232, 245, 233));
                        label.setForeground(ModernUIUtils.SUCCESS);
                }
            }
            
            return label;
        }
    }
    
    // Custom renderer for stock quantity
    private class StockQtyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            
            if (!isSelected) {
                int qty = value != null ? (int) value : 0;
                int threshold = (int) table.getValueAt(row, 3);
                
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                
                if (qty == 0) {
                    label.setForeground(ModernUIUtils.DANGER);
                } else if (qty <= threshold) {
                    label.setForeground(ModernUIUtils.WARNING);
                } else {
                    label.setForeground(ModernUIUtils.SUCCESS);
                }
            }
            
            return label;
        }
    }
    
    private void showAddMedicineDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Medicine", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Medicine Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Unit
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1;
        JTextField unitField = new JTextField("Tablet", 20);
        formPanel.add(unitField, gbc);
        
        // Initial Stock
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Initial Stock:"), gbc);
        gbc.gridx = 1;
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        formPanel.add(stockSpinner, gbc);
        
        // Low Stock Threshold
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Low Stock Alert:"), gbc);
        gbc.gridx = 1;
        JSpinner thresholdSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        formPanel.add(thresholdSpinner, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton saveBtn = ModernUIUtils.createButton("💾 Save", ModernUIUtils.SUCCESS);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showWarning("Medicine name is required!");
                return;
            }
            
            Medicine medicine = new Medicine();
            medicine.setName(name);
            medicine.setDoseUnit(unitField.getText().trim());
            medicine.setStockQuantity((Integer) stockSpinner.getValue());
            medicine.setLowStockThreshold((Integer) thresholdSpinner.getValue());
            
            controller.addMedicine(medicine);
            refreshTable();
            dialog.dispose();
            showSuccess("Medicine '" + name + "' added successfully!");
        });
        
        JButton cancelBtn = ModernUIUtils.createButton("✖ Cancel", ModernUIUtils.TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteMedicine() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showWarning("Please select a medicine to delete.");
            return;
        }
        
        int medId = (int) tableModel.getValueAt(row, 0);
        String medName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete '" + medName + "'?\nThis will remove all schedules and history associated with this medicine.",
            "Delete Medicine", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteMedicine(medId);
            refreshTable();
            showSuccess("Medicine '" + medName + "' deleted successfully!");
        }
    }
}
