package com.example.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Settings Panel - UI Only (Backend not implemented)
 * Provides user preferences and app configuration options
 */
public class SettingsPanel extends JPanel {
    
    // Notification Settings
    private JCheckBox notificationsEnabledCheck;
    private JComboBox<String> reminderSoundCombo;
    private JSpinner reminderAdvanceSpinner;
    private JCheckBox soundEnabledCheck;
    
    // Display Settings
    private JComboBox<String> themeCombo;
    private JComboBox<String> fontSizeCombo;
    private JCheckBox showStockWarningsCheck;
    
    // Reminder Time Settings
    private JSpinner morningTimeSpinner;
    private JSpinner noonTimeSpinner;
    private JSpinner nightTimeSpinner;
    
    // Data Settings
    private JTextField backupPathField;
    
    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(ModernUIUtils.BACKGROUND);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ModernUIUtils.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        mainPanel.add(createNotificationSection());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createReminderTimeSection());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createDisplaySection());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createDataSection());
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(createButtonPanel());
        
        JScrollPane scrollPane = ModernUIUtils.createModernScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernUIUtils.PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("⚙️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        titlePanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        JLabel subtitleLabel = new JLabel("Configure your preferences");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 210, 255));
        panel.add(subtitleLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernUIUtils.BORDER, 1),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(ModernUIUtils.PRIMARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(15));
        
        return section;
    }
    
    private JPanel createNotificationSection() {
        JPanel section = createSection("🔔 Notifications");
        
        // Enable notifications
        notificationsEnabledCheck = new JCheckBox("Enable reminder notifications");
        notificationsEnabledCheck.setSelected(true);
        notificationsEnabledCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationsEnabledCheck.setOpaque(false);
        section.add(notificationsEnabledCheck);
        section.add(Box.createVerticalStrut(10));
        
        // Sound enabled
        soundEnabledCheck = new JCheckBox("Enable notification sound");
        soundEnabledCheck.setSelected(true);
        soundEnabledCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        soundEnabledCheck.setOpaque(false);
        section.add(soundEnabledCheck);
        section.add(Box.createVerticalStrut(10));
        
        // Sound selection
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        soundPanel.setOpaque(false);
        soundPanel.add(new JLabel("Reminder Sound: "));
        reminderSoundCombo = new JComboBox<>(new String[]{"Default", "Chime", "Bell", "Alert", "None"});
        reminderSoundCombo.setPreferredSize(new Dimension(150, 28));
        soundPanel.add(reminderSoundCombo);
        section.add(soundPanel);
        section.add(Box.createVerticalStrut(10));
        
        // Advance reminder
        JPanel advancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        advancePanel.setOpaque(false);
        advancePanel.add(new JLabel("Remind me "));
        reminderAdvanceSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 60, 5));
        reminderAdvanceSpinner.setPreferredSize(new Dimension(60, 28));
        advancePanel.add(reminderAdvanceSpinner);
        advancePanel.add(new JLabel(" minutes before scheduled time"));
        section.add(advancePanel);
        
        return section;
    }
    
    private JPanel createReminderTimeSection() {
        JPanel section = createSection("⏰ Default Reminder Times");
        
        // Morning time
        JPanel morningPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        morningPanel.setOpaque(false);
        morningPanel.add(new JLabel("Morning: "));
        morningTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor morningEditor = new JSpinner.DateEditor(morningTimeSpinner, "HH:mm");
        morningTimeSpinner.setEditor(morningEditor);
        morningTimeSpinner.setPreferredSize(new Dimension(80, 28));
        morningPanel.add(morningTimeSpinner);
        morningPanel.add(new JLabel("  (Default: 08:00)"));
        section.add(morningPanel);
        section.add(Box.createVerticalStrut(10));
        
        // Noon time
        JPanel noonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        noonPanel.setOpaque(false);
        noonPanel.add(new JLabel("Noon:      "));
        noonTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor noonEditor = new JSpinner.DateEditor(noonTimeSpinner, "HH:mm");
        noonTimeSpinner.setEditor(noonEditor);
        noonTimeSpinner.setPreferredSize(new Dimension(80, 28));
        noonPanel.add(noonTimeSpinner);
        noonPanel.add(new JLabel("  (Default: 13:00)"));
        section.add(noonPanel);
        section.add(Box.createVerticalStrut(10));
        
        // Night time
        JPanel nightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nightPanel.setOpaque(false);
        nightPanel.add(new JLabel("Night:      "));
        nightTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor nightEditor = new JSpinner.DateEditor(nightTimeSpinner, "HH:mm");
        nightTimeSpinner.setEditor(nightEditor);
        nightTimeSpinner.setPreferredSize(new Dimension(80, 28));
        nightPanel.add(nightTimeSpinner);
        nightPanel.add(new JLabel("  (Default: 20:00)"));
        section.add(nightPanel);
        
        return section;
    }
    
    private JPanel createDisplaySection() {
        JPanel section = createSection("🎨 Display");
        
        // Theme
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        themePanel.setOpaque(false);
        themePanel.add(new JLabel("Theme: "));
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark", "System Default"});
        themeCombo.setPreferredSize(new Dimension(150, 28));
        themePanel.add(themeCombo);
        section.add(themePanel);
        section.add(Box.createVerticalStrut(10));
        
        // Font size
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fontPanel.setOpaque(false);
        fontPanel.add(new JLabel("Font Size: "));
        fontSizeCombo = new JComboBox<>(new String[]{"Small", "Medium", "Large"});
        fontSizeCombo.setSelectedIndex(1);
        fontSizeCombo.setPreferredSize(new Dimension(150, 28));
        fontPanel.add(fontSizeCombo);
        section.add(fontPanel);
        section.add(Box.createVerticalStrut(10));
        
        // Stock warnings
        showStockWarningsCheck = new JCheckBox("Show low stock warnings on dashboard");
        showStockWarningsCheck.setSelected(true);
        showStockWarningsCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showStockWarningsCheck.setOpaque(false);
        section.add(showStockWarningsCheck);
        
        return section;
    }
    
    private JPanel createDataSection() {
        JPanel section = createSection("💾 Data & Backup");
        
        // Backup path
        JPanel backupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backupPanel.setOpaque(false);
        backupPanel.add(new JLabel("Backup Location: "));
        backupPathField = new JTextField(25);
        backupPathField.setText(System.getProperty("user.home") + "/DailyDose_Backup");
        backupPanel.add(backupPathField);
        JButton browseBtn = new JButton("Browse...");
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                backupPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        backupPanel.add(browseBtn);
        section.add(backupPanel);
        section.add(Box.createVerticalStrut(15));
        
        // Backup buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);
        
        JButton exportBtn = createActionButton("📤 Export Data", new Color(46, 125, 50));
        exportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Export feature - Backend not implemented", "Info", JOptionPane.INFORMATION_MESSAGE));
        actionPanel.add(exportBtn);
        
        JButton importBtn = createActionButton("📥 Import Data", new Color(21, 101, 192));
        importBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Import feature - Backend not implemented", "Info", JOptionPane.INFORMATION_MESSAGE));
        actionPanel.add(importBtn);
        
        JButton clearBtn = createActionButton("🗑️ Clear All Data", new Color(198, 40, 40));
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to clear all data? This cannot be undone!",
                "Confirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Clear data - Backend not implemented", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        actionPanel.add(clearBtn);
        
        section.add(actionPanel);
        
        return section;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = ModernUIUtils.createButton(text, bgColor);
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton saveBtn = ModernUIUtils.createButton("💾 Save Settings", ModernUIUtils.PRIMARY);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(160, 42));
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Settings saved! (UI only - backend not implemented)", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(saveBtn);
        
        JButton resetBtn = ModernUIUtils.createButton("↩️ Reset to Default", ModernUIUtils.TEXT_SECONDARY);
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetBtn.setPreferredSize(new Dimension(170, 42));
        resetBtn.addActionListener(e -> resetToDefaults());
        panel.add(resetBtn);
        
        return panel;
    }
    
    private void resetToDefaults() {
        notificationsEnabledCheck.setSelected(true);
        soundEnabledCheck.setSelected(true);
        reminderSoundCombo.setSelectedIndex(0);
        reminderAdvanceSpinner.setValue(5);
        themeCombo.setSelectedIndex(0);
        fontSizeCombo.setSelectedIndex(1);
        showStockWarningsCheck.setSelected(true);
        backupPathField.setText(System.getProperty("user.home") + "/DailyDose_Backup");
        JOptionPane.showMessageDialog(this, "Settings reset to defaults!", "Reset", JOptionPane.INFORMATION_MESSAGE);
    }
}
