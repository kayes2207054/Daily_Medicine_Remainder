package com.example.view;

import com.example.controller.*;
import com.example.model.*;
import com.example.utils.DataChangeListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Enhanced Medicine Panel with Search, Filter, and Better UI
 */
public class EnhancedMedicinePanel extends JPanel implements DataChangeListener {
    private final MedicineController controller;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JLabel totalLabel;

    public EnhancedMedicinePanel(MedicineController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Register as listener for data changes
        controller.addDataChangeListener(this);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadMedicines();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Medicine Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterMedicines();
            }
        });

        filterCombo = new JComboBox<>(new String[]{"All Medicines", "1 time", "2 times", "3 times"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterCombo.addActionListener(e -> filterMedicines());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterCombo);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {"ID", "Name", "Dosage", "Frequency", "Instructions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicineTable = new JTable(tableModel);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        medicineTable.setRowHeight(35);
        medicineTable.setShowVerticalLines(false);
        medicineTable.setGridColor(new Color(236, 240, 241));
        medicineTable.setSelectionBackground(new Color(52, 152, 219));
        medicineTable.setSelectionForeground(Color.WHITE);

        // Hide ID column
        medicineTable.getColumnModel().getColumn(0).setMinWidth(0);
        medicineTable.getColumnModel().getColumn(0).setMaxWidth(0);
        medicineTable.getColumnModel().getColumn(0).setWidth(0);

        // Center align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        medicineTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        medicineTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Total count label
        totalLabel = new JLabel("Total: 0 medicines");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(totalLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);

        JButton addBtn = createStyledButton("Add Medicine", new Color(46, 204, 113));
        JButton editBtn = createStyledButton("Edit Medicine", new Color(52, 152, 219));
        JButton deleteBtn = createStyledButton("Delete Medicine", new Color(231, 76, 60));
        JButton refreshBtn = createStyledButton("Refresh", new Color(149, 165, 166));

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> editMedicine());
        deleteBtn.addActionListener(e -> deleteMedicine());
        refreshBtn.addActionListener(e -> loadMedicines());

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(160, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void loadMedicines() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = controller.getAllMedicines();
        for (Medicine m : medicines) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getDosage(),
                    m.getFrequency(),
                    m.getInstructions()
            });
        }
        totalLabel.setText("Total: " + medicines.size() + " medicines");
    }

    private void filterMedicines() {
        String search = searchField.getText().toLowerCase();
        String filter = (String) filterCombo.getSelectedItem();

        tableModel.setRowCount(0);
        List<Medicine> medicines = controller.getAllMedicines();
        int count = 0;

        for (Medicine m : medicines) {
            boolean matchesSearch = m.getName().toLowerCase().contains(search) ||
                    m.getDosage().toLowerCase().contains(search) ||
                    m.getInstructions().toLowerCase().contains(search);

            boolean matchesFilter = filter.equals("All Medicines") ||
                    m.getFrequency().equalsIgnoreCase(filter);

            if (matchesSearch && matchesFilter) {
                tableModel.addRow(new Object[]{
                        m.getId(),
                        m.getName(),
                        m.getDosage(),
                        m.getFrequency(),
                        m.getInstructions()
                });
                count++;
            }
        }
        totalLabel.setText("Total: " + count + " medicines");
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) null, "Add New Medicine", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        JTextField dosageField = new JTextField();
        JComboBox<String> freqCombo = new JComboBox<>(new String[]{"1 time", "2 times", "3 times", "4 times"});
        JTextArea instructionsArea = new JTextArea(3, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);

        formPanel.add(new JLabel("Medicine Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Dosage:"));
        formPanel.add(dosageField);
        formPanel.add(new JLabel("Frequency:"));
        formPanel.add(freqCombo);
        formPanel.add(new JLabel("Instructions:"));
        formPanel.add(new JScrollPane(instructionsArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createStyledButton("Save", new Color(46, 204, 113));
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166));

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dosage = dosageField.getText().trim();
            String frequency = (String) freqCombo.getSelectedItem();
            String instructions = instructionsArea.getText().trim();

            if (name.isEmpty() || dosage.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Dosage are required!");
                return;
            }

            Medicine medicine = new Medicine(name, dosage, frequency, instructions);
            int id = controller.addMedicine(medicine);
            dialog.dispose();
            if (id > 0) {
                JOptionPane.showMessageDialog(this, "✅ Medicine added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add medicine. It may already exist.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editMedicine() {
        int row = medicineTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to edit!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Medicine medicine = controller.getMedicineById(id);

        if (medicine == null) return;

        JDialog dialog = new JDialog((Frame) null, "Edit Medicine", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(medicine.getName());
        JTextField dosageField = new JTextField(medicine.getDosage());
        JComboBox<String> freqCombo = new JComboBox<>(new String[]{"1 time", "2 times", "3 times", "4 times"});
        freqCombo.setSelectedItem(medicine.getFrequency());
        JTextArea instructionsArea = new JTextArea(medicine.getInstructions(), 3, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);

        formPanel.add(new JLabel("Medicine Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Dosage:"));
        formPanel.add(dosageField);
        formPanel.add(new JLabel("Frequency:"));
        formPanel.add(freqCombo);
        formPanel.add(new JLabel("Instructions:"));
        formPanel.add(new JScrollPane(instructionsArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateBtn = createStyledButton("Update", new Color(52, 152, 219));
        JButton cancelBtn = createStyledButton("Cancel", new Color(149, 165, 166));

        updateBtn.addActionListener(e -> {
            medicine.setName(nameField.getText().trim());
            medicine.setDosage(dosageField.getText().trim());
            medicine.setFrequency((String) freqCombo.getSelectedItem());
            medicine.setInstructions(instructionsArea.getText().trim());

            boolean success = controller.updateMedicine(medicine);
            dialog.dispose();
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Medicine updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update medicine.");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteMedicine() {
        int row = medicineTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete '" + name + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteMedicine(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Medicine deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete medicine.");
            }
        }
    }
    
    // DataChangeListener implementation - auto-refresh UI when data changes
    @Override
    public void onMedicineDataChanged() {
        SwingUtilities.invokeLater(() -> {
            loadMedicines();
        });
    }
    
    @Override
    public void onReminderDataChanged() {
        // Medicine panel doesn't display reminders, no action needed
    }
    
    @Override
    public void onInventoryDataChanged() {
        // Medicine panel doesn't display inventory, no action needed
    }
    
    @Override
    public void onHistoryDataChanged() {
        // Medicine panel doesn't display history, no action needed
    }
}
