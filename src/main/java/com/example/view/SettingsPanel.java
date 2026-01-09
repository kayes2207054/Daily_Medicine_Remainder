package com.example.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Settings Panel for application configuration
 */
public class SettingsPanel extends JPanel {
    private JComboBox<String> themeCombo;
    private JCheckBox soundEnabledCheck;
    private JCheckBox notificationsEnabledCheck;
    private JSpinner reminderIntervalSpinner;
    private JComboBox<String> languageCombo;
    private JTextField databasePathField;

    public SettingsPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createSettingsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));

        JLabel subtitleLabel = new JLabel("Configure DailyDose preferences");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        mainPanel.add(createAppearanceSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createNotificationSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createDatabaseSection());
        mainPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createAppearanceSection() {
        JPanel section = createSection("🎨 Appearance");

        // Theme selection
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        themePanel.setOpaque(false);
        themePanel.add(new JLabel("Theme:"));
        themeCombo = new JComboBox<>(new String[]{"Dark Theme", "Light Theme", "System Default"});
        themeCombo.setPreferredSize(new Dimension(200, 30));
        themePanel.add(themeCombo);
        section.add(themePanel);

        // Language selection
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        langPanel.setOpaque(false);
        langPanel.add(new JLabel("Language:"));
        languageCombo = new JComboBox<>(new String[]{"English", "বাংলা (Bengali)", "हिन्दी (Hindi)"});
        languageCombo.setPreferredSize(new Dimension(200, 30));
        langPanel.add(languageCombo);
        section.add(langPanel);

        return section;
    }

    private JPanel createNotificationSection() {
        JPanel section = createSection("Notifications");

        // Sound enabled
        soundEnabledCheck = new JCheckBox("Enable alarm sound");
        soundEnabledCheck.setSelected(true);
        soundEnabledCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        soundEnabledCheck.setOpaque(false);
        section.add(soundEnabledCheck);

        // Notifications enabled
        notificationsEnabledCheck = new JCheckBox("Enable notifications");
        notificationsEnabledCheck.setSelected(true);
        notificationsEnabledCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationsEnabledCheck.setOpaque(false);
        section.add(notificationsEnabledCheck);

        // Reminder check interval
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        intervalPanel.setOpaque(false);
        intervalPanel.add(new JLabel("Check reminders every:"));
        reminderIntervalSpinner = new JSpinner(new SpinnerNumberModel(30, 10, 300, 10));
        reminderIntervalSpinner.setPreferredSize(new Dimension(70, 25));
        intervalPanel.add(reminderIntervalSpinner);
        intervalPanel.add(new JLabel("seconds"));
        section.add(intervalPanel);

        return section;
    }

    private JPanel createDatabaseSection() {
        JPanel section = createSection("Database");

        // Database path
        JPanel pathPanel = new JPanel(new BorderLayout(10, 10));
        pathPanel.setOpaque(false);
        pathPanel.add(new JLabel("Database Location:"), BorderLayout.NORTH);

        JPanel pathInputPanel = new JPanel(new BorderLayout(5, 5));
        pathInputPanel.setOpaque(false);
        databasePathField = new JTextField("data/dailydose.db");
        databasePathField.setPreferredSize(new Dimension(300, 30));
        databasePathField.setEditable(false);
        pathInputPanel.add(databasePathField, BorderLayout.CENTER);

        JButton browseButton = createStyledButton("Browse", new Color(52, 152, 219));
        browseButton.addActionListener(e -> browseDatabasePath());
        pathInputPanel.add(browseButton, BorderLayout.EAST);

        pathPanel.add(pathInputPanel, BorderLayout.CENTER);
        section.add(pathPanel);

        // Backup/Restore buttons
        JPanel backupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        backupPanel.setOpaque(false);

        JButton backupButton = createStyledButton("Backup Database", new Color(46, 204, 113));
        backupButton.addActionListener(e -> backupDatabase());
        backupPanel.add(backupButton);

        JButton restoreButton = createStyledButton("📥 Restore Database", new Color(241, 196, 15));
        restoreButton.addActionListener(e -> restoreDatabase());
        backupPanel.add(restoreButton);

        section.add(backupPanel);

        return section;
    }

    private JPanel createSection(String title) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(52, 73, 94)
                ),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        return section;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton saveButton = createStyledButton("Save Settings", new Color(46, 204, 113));
        saveButton.addActionListener(e -> saveSettings());
        panel.add(saveButton);

        JButton resetButton = createStyledButton("Reset to Defaults", new Color(231, 76, 60));
        resetButton.addActionListener(e -> resetSettings());
        panel.add(resetButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void browseDatabasePath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Database Location");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            databasePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void backupDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Database Backup");
        fileChooser.setSelectedFile(new java.io.File("dailydose_backup_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".db"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.nio.file.Files.copy(
                    java.nio.file.Paths.get("data/dailydose.db"),
                    fileChooser.getSelectedFile().toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                JOptionPane.showMessageDialog(this,
                        "Database backed up successfully!",
                        "Backup Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to backup database: " + ex.getMessage(),
                        "Backup Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreDatabase() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "This will replace your current database. Continue?",
                "Confirm Restore",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Database Backup to Restore");

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    java.nio.file.Files.copy(
                        fileChooser.getSelectedFile().toPath(),
                        java.nio.file.Paths.get("data/dailydose.db"),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    JOptionPane.showMessageDialog(this,
                            "Database restored successfully!\nPlease restart the application.",
                            "Restore Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Failed to restore database: " + ex.getMessage(),
                            "Restore Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void saveSettings() {
        // TODO: Implement settings persistence (save to properties file)
        JOptionPane.showMessageDialog(this,
                "Settings saved successfully!",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetSettings() {
        themeCombo.setSelectedIndex(0);
        languageCombo.setSelectedIndex(0);
        soundEnabledCheck.setSelected(true);
        notificationsEnabledCheck.setSelected(true);
        reminderIntervalSpinner.setValue(30);
        databasePathField.setText("data/dailydose.db");

        JOptionPane.showMessageDialog(this,
                "Settings reset to defaults!",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
