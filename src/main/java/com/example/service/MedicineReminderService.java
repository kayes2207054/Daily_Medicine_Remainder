package com.example.service;

import com.example.controller.ReminderController;
import com.example.model.Medicine;
import com.example.model.Reminder;
import com.example.model.Schedule;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Medicine Reminder Service
 * Runs in background and shows popup notifications when medicine time matches
 */
public class MedicineReminderService {
    private Timer timer;
    private ReminderController controller;
    private Set<String> notifiedToday = new HashSet<>(); // Track what we've notified
    private boolean enabled = true;
    
    public MedicineReminderService(ReminderController controller) {
        this.controller = controller;
    }
    
    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        
        timer = new Timer("MedicineReminderTimer", true);
        
        // Check every 30 seconds
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (enabled) {
                    checkReminders();
                }
            }
        }, 0, 30 * 1000); // 30 seconds
        
        System.out.println("Medicine Reminder Service started!");
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        System.out.println("Medicine Reminder Service stopped!");
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        String today = now.toLocalDate().toString();
        
        // Clear notifications from previous days
        notifiedToday.removeIf(key -> !key.startsWith(today));
        
        List<Reminder> reminders = controller.getDailyReminders();
        
        for (Reminder r : reminders) {
            if (r.getStatus() == Reminder.Status.PENDING) {
                LocalTime reminderTime = r.getReminderTime().toLocalTime();
                
                // Check if current time is within 2 minutes of reminder time
                long diffMinutes = Math.abs(java.time.Duration.between(currentTime, reminderTime).toMinutes());
                
                if (diffMinutes <= 2) {
                    String notifyKey = today + "_" + r.getMedicineName() + "_" + reminderTime.toString();
                    
                    // Only notify once
                    if (!notifiedToday.contains(notifyKey)) {
                        notifiedToday.add(notifyKey);
                        showReminderNotification(r);
                    }
                }
            }
        }
    }
    
    private void showReminderNotification(Reminder r) {
        SwingUtilities.invokeLater(() -> {
            // Create custom notification dialog
            JDialog dialog = new JDialog();
            dialog.setTitle("💊 Medicine Reminder");
            dialog.setModal(false);
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(null);
            dialog.setAlwaysOnTop(true);
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBackground(new Color(58, 56, 144));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Icon and title
            JLabel iconLabel = new JLabel("⏰", SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            iconLabel.setForeground(Color.WHITE);
            panel.add(iconLabel, BorderLayout.WEST);
            
            // Message
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            messagePanel.setOpaque(false);
            
            JLabel titleLabel = new JLabel("Time to take your medicine!");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            messagePanel.add(titleLabel);
            
            messagePanel.add(Box.createVerticalStrut(10));
            
            JLabel medLabel = new JLabel("💊 " + r.getMedicineName());
            medLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            medLabel.setForeground(new Color(255, 213, 79));
            medLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            messagePanel.add(medLabel);
            
            messagePanel.add(Box.createVerticalStrut(5));
            
            String timeStr = r.getReminderTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
            JLabel timeLabel = new JLabel("Scheduled: " + timeStr);
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            timeLabel.setForeground(new Color(200, 200, 220));
            timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            messagePanel.add(timeLabel);
            
            panel.add(messagePanel, BorderLayout.CENTER);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setOpaque(false);
            
            JButton takenBtn = new JButton("✅ Taken");
            takenBtn.setBackground(new Color(46, 125, 50));
            takenBtn.setForeground(Color.WHITE);
            takenBtn.setFocusPainted(false);
            takenBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(dialog, "Marked as taken!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            });
            buttonPanel.add(takenBtn);
            
            JButton snoozeBtn = new JButton("⏰ Snooze 5 min");
            snoozeBtn.setBackground(new Color(245, 124, 0));
            snoozeBtn.setForeground(Color.WHITE);
            snoozeBtn.setFocusPainted(false);
            snoozeBtn.addActionListener(e -> {
                // Remove from notified so it can trigger again
                String today = LocalDateTime.now().toLocalDate().toString();
                String notifyKey = today + "_" + r.getMedicineName() + "_" + r.getReminderTime().toLocalTime().toString();
                
                // Schedule to remove from notified set after 5 minutes
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        notifiedToday.remove(notifyKey);
                    }
                }, 5 * 60 * 1000);
                
                dialog.dispose();
            });
            buttonPanel.add(snoozeBtn);
            
            JButton dismissBtn = new JButton("❌ Dismiss");
            dismissBtn.setBackground(new Color(120, 120, 120));
            dismissBtn.setForeground(Color.WHITE);
            dismissBtn.setFocusPainted(false);
            dismissBtn.addActionListener(e -> dialog.dispose());
            buttonPanel.add(dismissBtn);
            
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.setVisible(true);
            
            // Play system beep
            Toolkit.getDefaultToolkit().beep();
            
            // Auto-close after 60 seconds
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        if (dialog.isVisible()) {
                            dialog.dispose();
                        }
                    });
                }
            }, 60 * 1000);
        });
    }
    
    // Clear today's notifications (useful for testing)
    public void clearNotifications() {
        notifiedToday.clear();
    }
}
