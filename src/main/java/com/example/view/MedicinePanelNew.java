package com.example.view;

import com.example.controller.MedicineController;
import com.example.model.Medicine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * MedicinePanel Class
 * Provides UI for managing medicines with CRUD operations.
 * Includes search, sort, add, edit, and delete functionality.
 */
public class MedicinePanelNew extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(MedicinePanelNew.class);
    private MedicineController controller;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortCombo;

    public MedicinePanelNew(MedicineController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));
        
        initComponents();
        loadMedicines();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Top panel with title and search
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Main table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Create top panel with title and search
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Medicine Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchField = new JTextField(20);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterMedicines();
            }
        });
        
        JLabel sortLabel = new JLabel("Sort By:");
        sortLabel.setForeground(Color.WHITE);
        sortCombo = new JComboBox<>(new String[]{"Name", "Frequency"});
        sortCombo.addActionListener(e -> sortMedicines());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(sortLabel);
        searchPanel.add(sortCombo);
        
        panel.add(searchPanel, BorderLayout.EAST);
        return panel;
    }

    /**
     * Create table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"ID", "Name", "Dosage", "Frequency", "Instructions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        medicineTable = new JTable(tableModel);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        medicineTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        medicineTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        medicineTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        medicineTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(medicineTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Create button panel with actions
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.setBackground(new Color(236, 240, 241));
        
        JButton addBtn = new JButton("Add Medicine");
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(120, 35));
        addBtn.addActionListener(e -> showAddDialog());
        panel.add(addBtn);
        
        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(52, 152, 219));
        editBtn.setForeground(Color.WHITE);
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        editBtn.setPreferredSize(new Dimension(100, 35));
        editBtn.addActionListener(e -> showEditDialog());
        panel.add(editBtn);
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(100, 35));
        deleteBtn.addActionListener(e -> deleteMedicine());
        panel.add(deleteBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(155, 89, 182));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        refreshBtn.addActionListener(e -> loadMedicines());
        panel.add(refreshBtn);
        
        return panel;
    }

    /**
     * Load medicines into table
     */
    private void loadMedicines() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = controller.getAllMedicines();
        for (Medicine med : medicines) {
            tableModel.addRow(new Object[]{
                    med.getId(),
                    med.getName(),
                    med.getDosage(),
                    med.getFrequency(),
                    med.getInstructions()
            });
        }
        logger.info("Loaded " + medicines.size() + " medicines into table");
    }

    /**
     * Show add medicine dialog
     */
    private void showAddDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Medicine");
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Medicine name
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Medicine Name:"), gbc);
        JTextField nameField = new JTextField(25);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        // Dosage
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Dosage:"), gbc);
        JTextField dosageField = new JTextField(25);
        gbc.gridx = 1;
        dialog.add(dosageField, gbc);
        
        // Frequency
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Frequency (per day):"), gbc);
        JComboBox<String> frequencyCombo = new JComboBox<>(new String[]{"1 time", "2 times", "3 times", "4 times"});
        gbc.gridx = 1;
        dialog.add(frequencyCombo, gbc);
        
        // Instructions
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Instructions:"), gbc);
        JTextArea instructionsArea = new JTextArea(4, 25);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(instructionsArea), gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dosage = dosageField.getText().trim();
            String frequency = (String) frequencyCombo.getSelectedItem();
            String instructions = instructionsArea.getText().trim();
            
            if (name.isEmpty() || dosage.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Medicine medicine = new Medicine(name, dosage, frequency, instructions);
            int id = controller.addMedicine(medicine);
            if (id > 0) {
                JOptionPane.showMessageDialog(dialog, "Medicine added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMedicines();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add medicine or medicine already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, gbc);
        
        dialog.setVisible(true);
    }

    /**
     * Show edit medicine dialog
     */
    private void showEditDialog() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to edit!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Medicine medicine = controller.getMedicineById(id);
        
        if (medicine == null) {
            JOptionPane.showMessageDialog(this, "Could not load medicine details!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Medicine");
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Medicine name
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Medicine Name:"), gbc);
        JTextField nameField = new JTextField(medicine.getName(), 25);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        // Dosage
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Dosage:"), gbc);
        JTextField dosageField = new JTextField(medicine.getDosage(), 25);
        gbc.gridx = 1;
        dialog.add(dosageField, gbc);
        
        // Frequency
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Frequency:"), gbc);
        JComboBox<String> frequencyCombo = new JComboBox<>(new String[]{"1 time", "2 times", "3 times", "4 times"});
        frequencyCombo.setSelectedItem(medicine.getFrequency());
        gbc.gridx = 1;
        dialog.add(frequencyCombo, gbc);
        
        // Instructions
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Instructions:"), gbc);
        JTextArea instructionsArea = new JTextArea(medicine.getInstructions(), 4, 25);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(instructionsArea), gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> {
            medicine.setName(nameField.getText().trim());
            medicine.setDosage(dosageField.getText().trim());
            medicine.setFrequency((String) frequencyCombo.getSelectedItem());
            medicine.setInstructions(instructionsArea.getText().trim());
            
            if (controller.updateMedicine(medicine)) {
                JOptionPane.showMessageDialog(dialog, "Medicine updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMedicines();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update medicine!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, gbc);
        
        dialog.setVisible(true);
    }

    /**
     * Delete selected medicine
     */
    private void deleteMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete " + name + "?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteMedicine(id)) {
                JOptionPane.showMessageDialog(this, "Medicine deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMedicines();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete medicine!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Filter medicines based on search query
     */
    private void filterMedicines() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);
        
        List<Medicine> results = query.isEmpty() ? 
                controller.getAllMedicines() : 
                controller.searchMedicines(query);
        
        for (Medicine med : results) {
            tableModel.addRow(new Object[]{
                    med.getId(),
                    med.getName(),
                    med.getDosage(),
                    med.getFrequency(),
                    med.getInstructions()
            });
        }
    }

    /**
     * Sort medicines
     */
    private void sortMedicines() {
        String sortBy = (String) sortCombo.getSelectedItem();
        tableModel.setRowCount(0);
        
        List<Medicine> sorted = sortBy.equals("Name") ?
                controller.sortByName() :
                controller.sortByFrequency();
        
        for (Medicine med : sorted) {
            tableModel.addRow(new Object[]{
                    med.getId(),
                    med.getName(),
                    med.getDosage(),
                    med.getFrequency(),
                    med.getInstructions()
            });
        }
    }
}
