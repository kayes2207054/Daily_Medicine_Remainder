package com.example.view;

import com.example.controller.*;
import com.example.service.MedicineReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private MedicineController medicineController;
    private ReminderController reminderController;
    private HistoryController historyController;
    private AuthController authController;
    private MedicineReminderService reminderService;
    private LiveClockPanel clockPanel;
    
    public MainFrame() {
        setTitle("DailyDose - Medicine Tracker");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Could not set system look and feel", e);
        }
        
        authController = new AuthController();
        initControllers();
        initComponents();
        startReminderService();
    }
    
    private void initControllers() {
        medicineController = new MedicineController();
        historyController = new HistoryController();
        reminderController = new ReminderController();
        
        // Link controllers
        reminderController.setMedicineController(medicineController);
        reminderController.setHistoryController(historyController);
    }
    
    private void startReminderService() {
        reminderService = new MedicineReminderService(reminderController);
        reminderService.start();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernUIUtils.BACKGROUND);
        
        // Header with clock
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ModernUIUtils.PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("💊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        titlePanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("DailyDose");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Medicine Tracker");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 210, 255));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(8, 5, 0, 0));
        titlePanel.add(subtitleLabel);
        
        topPanel.add(titlePanel, BorderLayout.WEST);
        
        // Right panel with clock and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        
        // Live clock
        clockPanel = new LiveClockPanel();
        rightPanel.add(clockPanel);
        
        JButton logoutButton = ModernUIUtils.createButton("🚪 Logout", ModernUIUtils.DANGER);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);
        
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Create styled tabs
        JTabbedPane tabbedPane = createStyledTabbedPane();
        
        tabbedPane.addTab("🏠 Dashboard", new DashboardPanel(medicineController, reminderController, historyController));
        tabbedPane.addTab("💊 Medicines", new EnhancedMedicinePanel(medicineController));
        tabbedPane.addTab("⏰ Reminders", new ReminderPanel(reminderController));
        tabbedPane.addTab("📦 Inventory", new InventoryPanel(medicineController));
        tabbedPane.addTab("📋 History", new HistoryPanel(historyController));
        tabbedPane.addTab("⚙️ Settings", new SettingsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(ModernUIUtils.BACKGROUND);
        tabbedPane.setForeground(ModernUIUtils.TEXT_PRIMARY);
        
        // Custom UI for better looking tabs
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets = new Insets(5, 15, 0, 15);
                contentBorderInsets = new Insets(0, 0, 0, 0);
            }
            
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(new Color(240, 242, 247));
                }
                
                g2d.fillRoundRect(x, y, w, h + 10, 8, 8);
                g2d.dispose();
            }
            
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(ModernUIUtils.PRIMARY);
                    g2d.fillRect(x + 10, y, w - 20, 3);
                    g2d.dispose();
                }
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // No content border
            }
            
            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                    int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
                // No focus indicator
            }
            
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 45;
            }
            
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 30;
            }
        });
        
        return tabbedPane;
    }
    
    private void logout() {
        // Stop reminder service
        if (reminderService != null) {
            reminderService.stop();
        }
        authController.logout();;
        dispose();
        new LoginFrame().setVisible(true);
    }
    
    @Override
    public void dispose() {
        if (reminderService != null) {
            reminderService.stop();
        }
        super.dispose();
    }
}
