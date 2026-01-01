package com.example.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LiveClockPanel - Real-time digital clock component
 * 
 * Features:
 * - Shows current system time (HH:mm:ss)
 * - Updates every 1 second automatically
 * - Modern purple gradient design
 * - Auto-starts when created
 * - Uses javax.swing.Timer for thread-safe updates
 */
public class LiveClockPanel extends JPanel {
    private JLabel timeLabel;
    private JLabel dateLabel;
    private Timer clockTimer;
    
    // Time formatters
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    /**
     * Constructor - Creates and starts the live clock
     */
    public LiveClockPanel() {
        initComponents();
        startClock();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setPreferredSize(new Dimension(200, 80));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Time label (HH:mm:ss)
        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(timeLabel);
        
        add(Box.createVerticalStrut(5));
        
        // Date label (dd-MM-yyyy)
        dateLabel = new JLabel("01-01-2026");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(200, 200, 220));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(dateLabel);
        
        // Update immediately with current time
        updateClock();
    }
    
    /**
     * Start the clock timer
     * Updates every 1 second (1000 ms)
     */
    private void startClock() {
        // Stop any existing timer
        stopClock();
        
        // Create new timer that fires every 1000ms (1 second)
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.setInitialDelay(0); // Start immediately
        clockTimer.start();
    }
    
    /**
     * Stop the clock timer
     * Call this when panel is removed or app closes
     */
    public void stopClock() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
    }
    
    /**
     * Update clock display with current system time
     * Called every second by the timer
     */
    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        
        // Format and update time label
        String timeText = now.format(TIME_FORMAT);
        timeLabel.setText(timeText);
        
        // Format and update date label
        String dateText = now.format(DATE_FORMAT);
        dateLabel.setText(dateText);
    }
    
    /**
     * Custom paint for rounded background
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded background with gradient
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(88, 86, 214, 100),
            0, getHeight(), new Color(108, 106, 234, 100)
        );
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // Draw border
        g2.setColor(new Color(255, 255, 255, 30));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    /**
     * Cleanup when panel is removed
     */
    @Override
    public void removeNotify() {
        stopClock();
        super.removeNotify();
    }
}
