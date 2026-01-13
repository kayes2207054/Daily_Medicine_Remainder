package com.example.view;

import com.example.controller.MedicineController;
import com.example.model.Medicine;
import com.example.model.Schedule;
import com.example.utils.DataChangeListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EnhancedMedicinePanel extends JPanel implements DataChangeListener {
    private MedicineController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Filters
    private JTextField searchField;
    private JCheckBox cbFilterMorning, cbFilterNoon, cbFilterNight;
    private JRadioButton rbFilterBefore, rbFilterAfter, rbFilterNone;

    public EnhancedMedicinePanel(MedicineController controller) {
        this.controller = controller;
        this.controller.addDataChangeListener(this);
        setLayout(new BorderLayout());
        setBackground(ModernUIUtils.BACKGROUND);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
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
        
        JLabel iconLabel = new JLabel("💊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        titlePanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Medicine Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIUtils.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));
        
        // Filter panel
        panel.add(createFilterPanel(), BorderLayout.NORTH);
        
        // Table panel
        panel.add(createTablePanel(), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIUtils.BORDER, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Search field
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        panel.add(searchLabel);
        
        searchField = new JTextField(12);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIUtils.BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        panel.add(searchField);
        
        // Separator
        panel.add(createSeparator());
        
        // Frequency checkboxes
        JLabel freqLabel = new JLabel("Time:");
        freqLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        freqLabel.setForeground(ModernUIUtils.TEXT_SECONDARY);
        panel.add(freqLabel);
        
        cbFilterMorning = createStyledCheckbox("Morning");
        cbFilterNoon = createStyledCheckbox("Noon");
        cbFilterNight = createStyledCheckbox("Night");
        panel.add(cbFilterMorning);
        panel.add(cbFilterNoon);
        panel.add(cbFilterNight);
        
        // Separator
        panel.add(createSeparator());
        
        // Meal radio buttons
        JLabel mealLabel = new JLabel("Meal:");
        mealLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mealLabel.setForeground(ModernUIUtils.TEXT_SECONDARY);
        panel.add(mealLabel);
        
        ButtonGroup bg = new ButtonGroup();
        rbFilterBefore = createStyledRadio("Before");
        rbFilterAfter = createStyledRadio("After");
        rbFilterNone = createStyledRadio("Any");
        rbFilterNone.setSelected(true);
        bg.add(rbFilterBefore);
        bg.add(rbFilterAfter);
        bg.add(rbFilterNone);
        
        panel.add(rbFilterBefore);
        panel.add(rbFilterAfter);
        panel.add(rbFilterNone);
        
        // Search button
        JButton btnSearch = ModernUIUtils.createButton("Apply", ModernUIUtils.PRIMARY);
        btnSearch.setPreferredSize(new Dimension(80, 30));
        btnSearch.addActionListener(e -> applyFilters());
        panel.add(btnSearch);
        
        JButton btnClear = ModernUIUtils.createButton("Clear", ModernUIUtils.TEXT_SECONDARY);
        btnClear.setPreferredSize(new Dimension(70, 30));
        btnClear.addActionListener(e -> clearFilters());
        panel.add(btnClear);
        
        return panel;
    }
    
    private JLabel createSeparator() {
        JLabel sep = new JLabel("|");
        sep.setForeground(ModernUIUtils.BORDER);
        sep.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        return sep;
    }
    
    private JCheckBox createStyledCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setForeground(ModernUIUtils.TEXT_PRIMARY);
        cb.setOpaque(false);
        return cb;
    }
    
    private JRadioButton createStyledRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rb.setForeground(ModernUIUtils.TEXT_PRIMARY);
        rb.setOpaque(false);
        return rb;
    }
    
    private void clearFilters() {
        searchField.setText("");
        cbFilterMorning.setSelected(false);
        cbFilterNoon.setSelected(false);
        cbFilterNight.setSelected(false);
        rbFilterNone.setSelected(true);
        refreshTable();
    }
    
    private void applyFilters() {
        String query = searchField.getText().trim();
        String freq = "";
        if(cbFilterMorning.isSelected()) freq = "MORNING";
        else if(cbFilterNoon.isSelected()) freq = "NOON";
        else if(cbFilterNight.isSelected()) freq = "NIGHT";
        
        String meal = "";
        if(rbFilterBefore.isSelected()) meal = "BEFORE_MEAL";
        else if(rbFilterAfter.isSelected()) meal = "AFTER_MEAL";
        
        List<Medicine> results = controller.searchMedicines(query, freq, meal);
        updateTableData(results);
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(ModernUIUtils.BORDER, 1));
        
        String[] columns = {"ID", "Name", "Dosage Schedule", "Stock", "Threshold", "Unit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        ModernUIUtils.styleTable(table);
        
        // Custom renderer for stock column
        table.getColumnModel().getColumn(3).setCellRenderer(new StockCellRenderer());
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        
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
        
        // Add Medicine Button - Bright Green
        JButton btnAdd = new JButton("➕ Add Medicine");
        btnAdd.setPreferredSize(new Dimension(180, 45));
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 142, 60), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnAdd.addActionListener(e -> showMedicineDialog(null));
        
        // Edit Button - Bright Blue
        JButton btnEdit = new JButton("✏️ Edit Medicine");
        btnEdit.setPreferredSize(new Dimension(170, 45));
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEdit.setFocusPainted(false);
        btnEdit.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(21, 101, 192), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                int id = (int) table.getValueAt(row, 0);
                Medicine m = controller.getAllMedicines().stream().filter(med -> med.getId() == id).findFirst().orElse(null);
                if(m != null) showMedicineDialog(m);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a medicine to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Delete Button - Bright Red
        JButton btnDelete = new JButton("🗑️ Delete Medicine");
        btnDelete.setPreferredSize(new Dimension(180, 45));
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(198, 40, 40), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row >= 0) {
                int id = (int) table.getValueAt(row, 0);
                String name = (String) table.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + name + "'?",
                    "Delete Medicine", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.deleteMedicine(id);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a medicine to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Update Stock Button - Bright Orange
        JButton btnStock = new JButton("📦 Update Stock");
        btnStock.setPreferredSize(new Dimension(170, 45));
        btnStock.setBackground(new Color(255, 152, 0));
        btnStock.setForeground(Color.WHITE);
        btnStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnStock.setFocusPainted(false);
        btnStock.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 124, 0), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnStock.addActionListener(e -> showStockDialog());
        
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnStock);
        
        return panel;
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void refreshTable() {
        updateTableData(controller.getAllMedicines());
    }
    
    private void updateTableData(List<Medicine> medicines) {
        tableModel.setRowCount(0);
        for(Medicine m : medicines) {
            Vector<Object> row = new Vector<>();
            row.add(m.getId());
            row.add(m.getName());
            row.add(m.getDosageSummary());
            row.add(m.getStockQuantity());
            row.add(m.getLowStockThreshold());
            row.add(m.getDoseUnit());
            tableModel.addRow(row);
        }
    }
    
    // Custom renderer for stock column
    private class StockCellRenderer extends DefaultTableCellRenderer {
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
                int threshold = (int) table.getValueAt(row, 4); // Threshold column
                
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
    
    @Override
    public void onMedicineDataChanged() {
        refreshTable();
    }

    @Override
    public void onInventoryDataChanged() {
        refreshTable();
    }

    @Override
    public void onReminderDataChanged() {
        // No visual impact on medicine table
    }

    @Override
    public void onHistoryDataChanged() {
        // No visual impact on medicine table
    }
    
    // ============ DIALOGS ============
    
    private void showMedicineDialog(Medicine m) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                m == null ? "Add Medicine" : "Edit Medicine", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField tfName = new JTextField(m != null ? m.getName() : "", 20);
        JTextField tfUnit = new JTextField(m != null ? m.getDoseUnit() : "Tablet", 10);
        JSpinner spStock = new JSpinner(new SpinnerNumberModel(m != null ? m.getStockQuantity() : 0, 0, 10000, 1));
        JSpinner spThreshold = new JSpinner(new SpinnerNumberModel(m != null ? m.getLowStockThreshold() : 10, 0, 1000, 1));
        
        gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Name:"), gbc);
        gbc.gridx=1; form.add(tfName, gbc);
        
        gbc.gridx=0; gbc.gridy=1; form.add(new JLabel("Unit:"), gbc);
        gbc.gridx=1; form.add(tfUnit, gbc);
        
        gbc.gridx=0; gbc.gridy=2; form.add(new JLabel("Initial Stock:"), gbc);
        gbc.gridx=1; form.add(spStock, gbc);
        
        gbc.gridx=0; gbc.gridy=3; form.add(new JLabel("Low Warning:"), gbc);
        gbc.gridx=1; form.add(spThreshold, gbc);
        
        // Schedules
        JPanel schedPanel = new JPanel(new GridLayout(4, 1));
        schedPanel.setBorder(BorderFactory.createTitledBorder("Frequency & Timing"));
        
        // Helper to create rows
        class SchedRow {
            JCheckBox cb;
            JRadioButton rbBefore, rbAfter, rbNone;
            JTextField tfCustom;
            String time;
            
            SchedRow(String label, Medicine existing) {
                time = label.toUpperCase();
                cb = new JCheckBox(label);
                ButtonGroup bg = new ButtonGroup();
                rbBefore = new JRadioButton("Before");
                rbAfter = new JRadioButton("After");
                rbNone = new JRadioButton("N/A", true);
                bg.add(rbBefore); bg.add(rbAfter); bg.add(rbNone);
                
                if(existing != null) {
                    for(Schedule s : existing.getSchedules()) {
                        if(s.getTimeOfDay().equals(time)) {
                            cb.setSelected(true);
                            if("BEFORE_MEAL".equals(s.getMealTiming())) rbBefore.setSelected(true);
                            else if("AFTER_MEAL".equals(s.getMealTiming())) rbAfter.setSelected(true);
                        }
                    }
                }
            }
            
            JPanel getPanel() {
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
                p.add(cb);
                p.add(rbBefore); p.add(rbAfter); p.add(rbNone);
                if("CUSTOM".equals(time)) {
                    tfCustom = new JTextField(10);
                    p.add(new JLabel("Note:"));
                    p.add(tfCustom);
                }
                return p;
            }
            
            Schedule getSchedule() {
                if(!cb.isSelected()) return null;
                String meal = "NONE";
                if(rbBefore.isSelected()) meal = "BEFORE_MEAL";
                else if(rbAfter.isSelected()) meal = "AFTER_MEAL";
                
                String note = (tfCustom != null) ? tfCustom.getText() : null;
                return new Schedule(time, meal, 1.0, note);
            }
        }
        
        SchedRow rowM = new SchedRow("MORNING", m);
        SchedRow rowN = new SchedRow("NOON", m);
        SchedRow rowNi = new SchedRow("NIGHT", m);
        SchedRow rowC = new SchedRow("CUSTOM", m);
        
        schedPanel.add(rowM.getPanel());
        schedPanel.add(rowN.getPanel());
        schedPanel.add(rowNi.getPanel());
        schedPanel.add(rowC.getPanel());
        
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2;
        form.add(schedPanel, gbc);
        
        dialog.add(form, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            String name = tfName.getText();
            if(name.isEmpty()) return;
            
            List<Schedule> schedules = new ArrayList<>();
            if(rowM.getSchedule() != null) schedules.add(rowM.getSchedule());
            if(rowN.getSchedule() != null) schedules.add(rowN.getSchedule());
            if(rowNi.getSchedule() != null) schedules.add(rowNi.getSchedule());
            if(rowC.getSchedule() != null) schedules.add(rowC.getSchedule());
            
            Medicine med = (m != null) ? m : new Medicine();
            med.setName(name);
            med.setDoseUnit(tfUnit.getText());
            med.setStockQuantity((Integer)spStock.getValue());
            med.setLowStockThreshold((Integer)spThreshold.getValue());
            med.setSchedules(schedules);
            
            if(m == null) controller.addMedicine(med);
            else controller.updateMedicine(med);
            
            dialog.dispose();
        });
        
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showStockDialog() {
        int row = table.getSelectedRow();
        if(row < 0) {
            JOptionPane.showMessageDialog(this, "Select a medicine");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        String name = (String) table.getValueAt(row, 1);
        int current = (int) table.getValueAt(row, 3);
        
        String input = JOptionPane.showInputDialog(this, "Enter new stock for " + name + ":", current);
        if(input != null) {
            try {
                int newQty = Integer.parseInt(input);
                controller.updateStock(id, newQty, "Manual Update");
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number");
            }
        }
    }
}
