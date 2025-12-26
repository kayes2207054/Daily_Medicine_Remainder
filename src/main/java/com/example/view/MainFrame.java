package com.example.view;

import com.example.controller.*;
import com.example.database.DatabaseManager;
import com.example.service.ReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * MainFrame Class
 * Main application window for DailyDose application.
 * Manages navigation between different modules using CardLayout.
 * Integrates all controllers and views.
 */
public class MainFrame extends JFrame {
    // Incremental Progress: 60% → 70%
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private JPanel sideMenuPanel;
    private JPanel contentPanel;
    private JLabel statusLabel;
    
    // View panels
    private JPanel homePanel;
    private MedicinePanel medicinePanel;
    private ReminderPanel reminderPanel;
    private InventoryPanel inventoryPanel;
    private HistoryPanel historyPanel;
    
    // Controllers
    private MedicineController medicineController;
    private ReminderController reminderController;
    private InventoryController inventoryController;
    private HistoryController historyController;
    
    // Services
    private DatabaseManager dbManager;
    private ReminderService reminderService;
    private LiveClockPanel liveClockPanel;

    public MainFrame() {
        setTitle("DailyDose - Your Personal Medicine Companion");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        logger.info("Initializing DailyDose Application");

        // Initialize database and services
        dbManager = DatabaseManager.getInstance();
        // Initialize controllers
        medicineController = new MedicineController();
        reminderController = new ReminderController();
        inventoryController = new InventoryController();
        historyController = new HistoryController();
        reminderController.setInventoryController(inventoryController);
        reminderController.setHistoryController(historyController);
        reminderService = new ReminderService(reminderController);

        // Create UI components
        createSideMenu();
        createContentArea();
        createStatusBar();

        // Start background reminder checks
        reminderService.start();

        // Add window listener for graceful shutdown
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onApplicationClose();
            }
        });

        setVisible(true);
        logger.info("DailyDose Application started successfully");
    }

    /**
     * Create side navigation menu
     */
    private void createSideMenu() {
        sideMenuPanel = new JPanel();
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));
        sideMenuPanel.setPreferredSize(new Dimension(200, 700));
        sideMenuPanel.setBackground(new Color(41, 128, 185));
        
        // Application title
        JLabel titleLabel = new JLabel("DailyDose");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        sideMenuPanel.add(titleLabel);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Medicine Manager");
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideMenuPanel.add(subtitleLabel);
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add live clock
        liveClockPanel = new LiveClockPanel();
        liveClockPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideMenuPanel.add(liveClockPanel);
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Menu buttons
        addMenuButton("Home");
        addMenuButton("Medicines");
        addMenuButton("Reminders");
        addMenuButton("Inventory");
        addMenuButton("History");
        
        sideMenuPanel.add(Box.createVerticalGlue());
        
        // Bottom buttons
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JButton logoutButton = createMenuButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        sideMenuPanel.add(logoutButton);

        JButton settingsButton = createMenuButton("Settings");
        settingsButton.addActionListener(e -> showSettings());
        sideMenuPanel.add(settingsButton);
        
        JButton helpButton = createMenuButton("Help");
        helpButton.addActionListener(e -> showHelp());
        sideMenuPanel.add(helpButton);
        
        add(sideMenuPanel, BorderLayout.WEST);
    }

    /**
     * Create and add menu button
     */
    private void addMenuButton(String name) {
        JButton button = createMenuButton(name);
        button.addActionListener(e -> switchPanel(name));
        sideMenuPanel.add(button);
        sideMenuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    /**
     * Create styled menu button
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 45));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 100, 180));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        
        return button;
    }

    /**
     * Create content area with CardLayout
     */
    private void createContentArea() {
        contentPanel = new JPanel(new CardLayout());
        
        // Initialize all panels
        homePanel = createHomePanel();
        medicinePanel = new MedicinePanel(medicineController, inventoryController);
        reminderPanel = new ReminderPanel(reminderController);
        inventoryPanel = new InventoryPanel(inventoryController, medicineController);
        historyPanel = new HistoryPanel(historyController);
        
        // Add panels to content area
        contentPanel.add(homePanel, "Home");
        contentPanel.add(medicinePanel, "Medicines");
        contentPanel.add(reminderPanel, "Reminders");
        contentPanel.add(inventoryPanel, "Inventory");
        contentPanel.add(historyPanel, "History");
        
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Create enhanced home panel
     */
    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(236, 240, 241));
        
        // Welcome section
        JLabel welcomeLabel = new JLabel("Welcome to DailyDose");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Your Personal Medicine Companion");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Statistics panel
        JPanel statsPanel = createStatsPanel();
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statsPanel);
        
        panel.add(Box.createVerticalGlue());
        
        // Quick actions
        JPanel quickActionsPanel = createQuickActionsPanel();
        quickActionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(quickActionsPanel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        return panel;
    }

    /**
     * Create statistics panel for dashboard
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setPreferredSize(new Dimension(600, 200));
        panel.setMaximumSize(new Dimension(600, 200));
        panel.setOpaque(false);
        
        panel.add(createStatCard("Total Medicines", String.valueOf(medicineController.getTotalMedicines()), new Color(46, 204, 113)));
        panel.add(createStatCard("Pending Reminders", String.valueOf(reminderController.getPendingCount()), new Color(241, 196, 15)));
        panel.add(createStatCard("Low Stock Items", String.valueOf(inventoryController.getLowStockItems().size()), new Color(231, 76, 60)));
        panel.add(createStatCard("Today's Doses", String.valueOf(historyController.getTakenTodayCount()), new Color(52, 152, 219)));
        
        return panel;
    }

    /**
     * Create a statistics card
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setPreferredSize(new Dimension(140, 80));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setForeground(color);
        
        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());
        
        return card;
    }

    /**
     * Create quick actions panel
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(800, 50));
        
        JButton addMedicineBtn = new JButton("Add Medicine");
        addMedicineBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addMedicineBtn.setBackground(new Color(46, 204, 113));
        addMedicineBtn.setForeground(Color.WHITE);
        addMedicineBtn.setBorderPainted(false);
        addMedicineBtn.setFocusPainted(false);
        addMedicineBtn.setPreferredSize(new Dimension(150, 40));
        addMedicineBtn.addActionListener(e -> switchPanel("Medicines"));
        panel.add(addMedicineBtn);
        
        JButton setReminderBtn = new JButton("Set Reminder");
        setReminderBtn.setFont(new Font("Arial", Font.BOLD, 12));
        setReminderBtn.setBackground(new Color(52, 152, 219));
        setReminderBtn.setForeground(Color.WHITE);
        setReminderBtn.setBorderPainted(false);
        setReminderBtn.setFocusPainted(false);
        setReminderBtn.setPreferredSize(new Dimension(150, 40));
        setReminderBtn.addActionListener(e -> switchPanel("Reminders"));
        panel.add(setReminderBtn);
        
        JButton viewHistoryBtn = new JButton("View History");
        viewHistoryBtn.setFont(new Font("Arial", Font.BOLD, 12));
        viewHistoryBtn.setBackground(new Color(155, 89, 182));
        viewHistoryBtn.setForeground(Color.WHITE);
        viewHistoryBtn.setBorderPainted(false);
        viewHistoryBtn.setFocusPainted(false);
        viewHistoryBtn.setPreferredSize(new Dimension(150, 40));
        viewHistoryBtn.addActionListener(e -> switchPanel("History"));
        panel.add(viewHistoryBtn);
        
        return panel;
    }

    /**
     * Create status bar
     */
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Switch to different panel
     */
    private void switchPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
        updateStatusBar("Switched to " + panelName);
        logger.info("Switched to panel: " + panelName);
    }

    /**
     * Update status bar message
     */
    public void updateStatusBar(String message) {
        statusLabel.setText(message);
    }

    /**
     * Show settings dialog
     */
    private void showSettings() {
        // Basic settings placeholder dialog (single option).
        // TODO: Persist preferences and load on startup.
        // TODO: Add more settings (sounds, snooze, missed-after, 12/24h default).
        JDialog dialog = new JDialog(this, "Settings", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(360, 180);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("Basic Settings (Temporary)");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(title, gbc);

        gbc.gridy++;
        JCheckBox chk24 = new JCheckBox("Use 24-hour time");
        // Reflect the current clock state or stored setting
        boolean current24 = (liveClockPanel != null) ? liveClockPanel.isUse24HourFormat() : com.example.utils.AppSettings.getUse24Hour();
        chk24.setSelected(current24);
        dialog.add(chk24, gbc);

        gbc.gridy++;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        buttons.add(save);
        buttons.add(cancel);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(buttons, gbc);

        save.addActionListener(e -> {
            try {
                if (liveClockPanel != null) {
                    liveClockPanel.setUse24HourFormat(chk24.isSelected());
                }
                // Persist the preference for future runs
                com.example.utils.AppSettings.setUse24Hour(chk24.isSelected());
                updateStatusBar("Settings saved");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to apply settings", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    /**
     * Show help dialog
     */
    private void showHelp() {
        String helpText = "DailyDose Help\n\n" +
                "1. Medicines: Add and manage your medications\n" +
                "2. Reminders: Set reminders for medication times\n" +
                "3. Inventory: Track pill quantities and low stock\n" +
                "4. History: View medication adherence and statistics\n\n" +
                "For more help, visit our website.";
        JOptionPane.showMessageDialog(this, helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handle application close
     */
    private void onApplicationClose() {
        logger.info("Closing DailyDose Application");
        if (reminderService != null) {
            reminderService.stop();
        }
        if (liveClockPanel != null) {
            liveClockPanel.stopClock();
        }
        if (dbManager != null) {
            dbManager.disconnect();
        }
        System.exit(0);
    }

    /**
     * Handle logout and return to login screen without exiting the app.
     */
    private void handleLogout() {
        if (reminderService != null) {
            reminderService.stop();
        }
        if (liveClockPanel != null) {
            liveClockPanel.stopClock();
        }
        if (dbManager != null) {
            dbManager.disconnect();
        }
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame(new AuthController()).setVisible(true));
    }

    /**
     * Get database manager
     */
    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }
}
