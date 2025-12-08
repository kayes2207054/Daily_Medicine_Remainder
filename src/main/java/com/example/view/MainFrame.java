package com.example.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel sideMenuPanel;
    private JPanel contentPanel;
    
    private MedicinePanel medicinePanel;
    private ReminderPanel reminderPanel;
    private InventoryPanel inventoryPanel;
    private HistoryPanel historyPanel;
    private JPanel homePanel;

    public MainFrame() {
        setTitle("DailyDose - Personal Medicine Companion");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create side menu
        createSideMenu();
        
        // Create content area
        contentPanel = new JPanel(new CardLayout());
        
        // Initialize panels
        homePanel = createHomePanel();
        medicinePanel = new MedicinePanel();
        reminderPanel = new ReminderPanel();
        inventoryPanel = new InventoryPanel();
        historyPanel = new HistoryPanel();
        
        // Add panels to content area
        contentPanel.add(homePanel, "Home");
        contentPanel.add(medicinePanel, "Medicines");
        contentPanel.add(reminderPanel, "Reminders");
        contentPanel.add(inventoryPanel, "Inventory");
        contentPanel.add(historyPanel, "History");
        
        add(sideMenuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }

    private void createSideMenu() {
        sideMenuPanel = new JPanel();
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));
        sideMenuPanel.setPreferredSize(new Dimension(200, 600));
        sideMenuPanel.setBackground(new Color(52, 73, 94));
        
        // Add title
        JLabel titleLabel = new JLabel("DailyDose");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sideMenuPanel.add(titleLabel);
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Add menu buttons
        addMenuButton("Home");
        addMenuButton("Medicines");
        addMenuButton("Reminders");
        addMenuButton("Inventory");
        addMenuButton("History");
    }

    private void addMenuButton(String name) {
        JButton button = new JButton(name);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        button.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, name);
        });
        
        sideMenuPanel.add(button);
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome to DailyDose", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        
        JLabel subLabel = new JLabel("Your Personal Medicine Companion", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(subLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
}
