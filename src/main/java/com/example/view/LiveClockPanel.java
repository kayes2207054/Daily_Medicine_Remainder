package com.example.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.example.utils.AppSettings;

/**
 * LiveClockPanel - Real-time digital clock component
 * Displays current system time updated every second.
 */
public class LiveClockPanel extends JPanel {
    // Incremental Progress: 60% → 70%
    private JLabel timeLabel;
    private JLabel dateLabel;
    private Timer timer;
    private DateTimeFormatter timeFormatter;
    private DateTimeFormatter dateFormatter;
    private boolean use24HourFormat = true; // default to 24h

    public LiveClockPanel() {
        // Initialize format from persisted setting
        use24HourFormat = AppSettings.getUse24Hour();
        timeFormatter = DateTimeFormatter.ofPattern(use24HourFormat ? "HH:mm:ss" : "hh:mm:ss a");
        dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(45, 45, 60));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Time label
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timeLabel.setForeground(new Color(200, 150, 255));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setToolTipText("Current time");
        
        // Date label
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(180, 180, 200));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateLabel.setToolTipText("Current date");
        
        add(timeLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(dateLabel);
        
        // Update clock immediately
        updateClock();
        
        // Start timer to update every second
        timer = new Timer(1000, e -> updateClock());
        timer.start();
    }

    /**
     * Update clock labels with current time
     */
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(timeFormatter));
        dateLabel.setText(now.format(dateFormatter));
    }

    /**
     * Stop the clock timer (call when panel is no longer needed)
     */
    public void stopClock() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    /**
     * Toggle between 12-hour and 24-hour display.
     * Basic, non-persistent preference. Call from settings later.
     * TODO: Bind to a Settings UI and persist user preference.
     */
    public void setUse24HourFormat(boolean use24) {
        this.use24HourFormat = use24;
        this.timeFormatter = DateTimeFormatter.ofPattern(use24 ? "HH:mm:ss" : "hh:mm:ss a");
        // TODO: Consider adding timezone display and colon blink effect.
        updateClock();
    }

    /**
     * Current clock format preference (12/24h).
     * TODO: Consolidate via a shared settings model.
     */
    public boolean isUse24HourFormat() {
        return use24HourFormat;
    }

    /**
     * Get preferred size for layout managers
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 80);
    }
}
