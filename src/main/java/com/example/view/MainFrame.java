package com.example.view;

import com.example.controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private MedicineController medicineController;
    private ReminderController reminderController;
    private InventoryController inventoryController;
    private HistoryController historyController;
    
    public MainFrame() {
        setTitle("DailyDose - Medicine Tracker");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initControllers();
        initComponents();
    }
    
    private void initControllers() {
        medicineController = new MedicineController();
        reminderController = new ReminderController();
        inventoryController = new InventoryController();
        historyController = new HistoryController();
        
        // Link controllers
        reminderController.setInventoryController(inventoryController);
        reminderController.setHistoryController(historyController);
        
        // Show alarm service started confirmation
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Alarm monitoring service started!\nYou will be notified when reminders are due.",
                "DailyDose Ready", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Main gradient background panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(58, 56, 144),
                    0, getHeight(), new Color(35, 35, 48)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(0, 100));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("💊 DailyDose - Medicine Tracker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Right panel with clock and logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        // Add live clock
        LiveClockPanel clockPanel = new LiveClockPanel();
        rightPanel.add(clockPanel);
        
        JButton logoutButton = createModernButton("Logout", new Color(231, 76, 60), new Color(192, 57, 43));
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);
        
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(35, 35, 48));
        tabbedPane.setForeground(new Color(200, 200, 220));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add tabs
        tabbedPane.addTab("🏠 Dashboard", new DashboardPanel(medicineController, reminderController, inventoryController, historyController));
        tabbedPane.addTab("💊 Medicines", new EnhancedMedicinePanel(medicineController));
        tabbedPane.addTab("⏰ Reminders", new ReminderPanel(reminderController));
        tabbedPane.addTab("📦 Inventory", new InventoryPanel(inventoryController));
        tabbedPane.addTab("📊 History", new HistoryPanel(historyController));
        tabbedPane.addTab("⚙️ Settings", new SettingsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
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
        button.setPreferredSize(new Dimension(100, 35));
        return button;
    }

    @Override
    public void dispose() {
        // Cleanup all resources before closing
        try {
            if (reminderController != null) {
                reminderController.cleanup();
            }
            com.example.database.DatabaseManager.getInstance().closeConnection();
            logger.info("Application resources cleaned up successfully");
        } catch (Exception e) {
            logger.error("Error during cleanup", e);
        }
        super.dispose();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
