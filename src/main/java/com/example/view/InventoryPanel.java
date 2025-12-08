package com.example.view;

import com.example.model.Inventory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class InventoryPanel extends JPanel {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private ArrayList<Inventory> inventoryList;

    public InventoryPanel() {
        inventoryList = new ArrayList<>();
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Inventory Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Medicine Name", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> showAddDialog());
        
        JButton updateButton = new JButton("Update Quantity");
        updateButton.addActionListener(e -> {
            // TODO: Implement update logic later
            JOptionPane.showMessageDialog(this, "Update functionality coming soon!");
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add Inventory Item");
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setLocationRelativeTo(this);
        
        JLabel nameLabel = new JLabel("Medicine Name:");
        JTextField nameField = new JTextField();
        
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String quantityStr = quantityField.getText();
            
            if (!name.isEmpty() && !quantityStr.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    Inventory item = new Inventory(name, quantity);
                    inventoryList.add(item);
                    tableModel.addRow(new Object[]{name, quantity});
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Quantity must be a number!");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(nameLabel);
        dialog.add(nameField);
        dialog.add(quantityLabel);
        dialog.add(quantityField);
        dialog.add(saveButton);
        dialog.add(cancelButton);
        
        dialog.setVisible(true);
    }
}
