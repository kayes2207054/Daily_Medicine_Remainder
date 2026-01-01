package com.example.view;

import com.example.controller.MedicineController;
import com.example.model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicinePanel extends JPanel {
    private final MedicineController controller = new MedicineController();
    private JTable table;
    private DefaultTableModel tableModel;

    public MedicinePanel() {
        setLayout(new BorderLayout());
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setOpaque(false);
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setOpaque(false);
        
        JButton addButton = createModernButton("➕ Add Medicine", new Color(88, 86, 214), new Color(108, 106, 234));
        addButton.addActionListener(e -> addMedicine());
        topPanel.add(addButton);
        
        JButton refreshButton = createModernButton("🔄 Refresh", new Color(52, 152, 219), new Color(41, 128, 185));
        refreshButton.addActionListener(e -> refreshTable());
        topPanel.add(refreshButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Table with modern styling
        String[] columns = {"ID", "Name", "Dosage", "Frequency", "Instructions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setBackground(new Color(50, 50, 70));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(88, 86, 214));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 90));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(58, 56, 144));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        scrollPane.getViewport().setBackground(new Color(35, 35, 48));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JButton createModernButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 38));
        return button;
    }

    private void refreshTable() {
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
    }

    private void addMedicine() {
        JTextField nameField = new JTextField();
        JTextField dosageField = new JTextField();
        JTextField frequencyField = new JTextField();
        JTextArea instructionsArea = new JTextArea(3, 20);

        Object[] message = {
            "Name:", nameField,
            "Dosage:", dosageField,
            "Frequency (times/day):", frequencyField,
            "Instructions:", new JScrollPane(instructionsArea)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String dosage = dosageField.getText().trim();
                int frequencyPerDay = Integer.parseInt(frequencyField.getText().trim());
                String frequencyText = frequencyPerDay + " times";
                String instructions = instructionsArea.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Medicine name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Medicine medicine = new Medicine();
                medicine.setName(name);
                medicine.setDosage(dosage);
                medicine.setFrequency(frequencyText);
                medicine.setInstructions(instructions);

                controller.addMedicine(medicine);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Medicine added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Frequency must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
