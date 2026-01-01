package com.example.view;

import com.example.controller.ReminderController;
import com.example.model.Reminder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReminderPanel extends JPanel {
    private final ReminderController controller;
    private JTable table;
    private DefaultTableModel tableModel;

    public ReminderPanel(ReminderController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setOpaque(false);
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setOpaque(false);
        
        JButton refreshButton = createModernButton("🔄 Refresh", new Color(52, 152, 219), new Color(41, 128, 185));
        refreshButton.addActionListener(e -> refreshTable());
        topPanel.add(refreshButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Table with modern styling
        String[] columns = {"ID", "Medicine", "Time", "Status"};
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
        button.setPreferredSize(new Dimension(120, 38));
        return button;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Reminder> reminders = controller.getAllReminders();
        for (Reminder r : reminders) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getMedicineName(),
                r.getReminderTime(),
                r.getStatus()
            });
        }
    }
}
