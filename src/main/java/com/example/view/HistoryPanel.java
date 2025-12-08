package com.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HistoryPanel extends JPanel {
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Dose History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Date", "Medicine", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Info label
        JLabel infoLabel = new JLabel("History tracking coming soon...", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(infoLabel, BorderLayout.SOUTH);
    }
}
