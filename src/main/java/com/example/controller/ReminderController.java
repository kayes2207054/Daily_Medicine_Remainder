package com.example.controller;

import com.example.database.DatabaseManager;
import com.example.model.DoseHistory;
import com.example.model.Inventory;
import com.example.model.Reminder;
import com.example.model.Reminder.Status;
import com.example.controller.InventoryController;
import com.example.controller.HistoryController;
import com.example.utils.DataChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Dimension;

/**
 * ReminderController manages in-memory reminders and exposes operations
 * for the UI and background reminder service.
 */
public class ReminderController {
    private static final Logger logger = LoggerFactory.getLogger(ReminderController.class);
    private final List<Reminder> reminders = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final DatabaseManager dbManager;
    private InventoryController inventoryController;
    private HistoryController historyController;
    private List<DataChangeListener> listeners = new ArrayList<>();
    // Basic alarm timer (student-level): checks every second and beeps until user acknowledges
    private Timer alarmTimer;
    private final Set<Integer> alarmingReminderIds = Collections.synchronizedSet(new HashSet<>());
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReminderController() {
        this.dbManager = DatabaseManager.getInstance();
        loadFromDatabase();
        // Start a simple alarm checker
        startBasicAlarmTimer();
    }

    private void loadFromDatabase() {
        List<Reminder> stored = dbManager.getAllReminders();
        reminders.clear();
        reminders.addAll(stored);
        int maxId = stored.stream().mapToInt(Reminder::getId).max().orElse(0);
        idGenerator.set(maxId + 1);
        logger.info("Loaded {} reminders from database", stored.size());
    }

    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    public void setHistoryController(HistoryController historyController) {
        this.historyController = historyController;
    }

    /**
     * Start a very basic alarm timer.
     * Checks every second; when current time matches reminder (hour+minute), plays beeps and shows an OK dialog.
     * TODO: Improve alarm handling
     * TODO: Add snooze feature
     */
    public void startBasicAlarmTimer() {
        stopBasicAlarmTimer();
        alarmTimer = new Timer("DailyDoseAlarmTimer", true);
        alarmTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkAndTriggerAlarms();
                } catch (Exception ignored) {}
            }
        }, 0, 1000);
        logger.info("Basic alarm timer started");
    }

    /** Stop the basic alarm timer if running. */
    public void stopBasicAlarmTimer() {
        if (alarmTimer != null) {
            alarmTimer.cancel();
            alarmTimer = null;
            logger.info("Basic alarm timer stopped");
        }
    }

    /** Shutdown all resources when app closes (stops alarm timer and any pending beeps). */
    public void shutdown() {
        logger.info("ReminderController shutting down...");
        stopBasicAlarmTimer();
        alarmingReminderIds.clear();
    }

    private void checkAndTriggerAlarms() {
        LocalDateTime now = LocalDateTime.now();
        // Cleanup alarms for reminders no longer matching this minute or no longer pending
        alarmingReminderIds.removeIf(id -> {
            Reminder r = getReminderById(id);
            return r == null || r.getStatus() != Status.PENDING || !sameMinute(r.getReminderTime(), now);
        });

        List<Reminder> due;
        synchronized (reminders) {
            due = reminders.stream()
                    .filter(r -> r.getStatus() == Status.PENDING && r.getReminderTime() != null && sameMinute(r.getReminderTime(), now))
                    .collect(Collectors.toList());
        }

        for (Reminder r : due) {
            if (!alarmingReminderIds.contains(r.getId())) {
                alarmingReminderIds.add(r.getId());
                triggerAlarm(r);
            }
        }
    }

    private boolean sameMinute(LocalDateTime a, LocalDateTime b) {
        return a.getYear() == b.getYear() && a.getDayOfYear() == b.getDayOfYear()
                && a.getHour() == b.getHour() && a.getMinute() == b.getMinute();
    }

    private void triggerAlarm(Reminder reminder) {
        // Repeat beep until user clicks a button (TAKEN, MISS, or STOP)
        Timer beepTimer = new Timer("DailyDoseAlarmBeep", true);
        beepTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Toolkit.getDefaultToolkit().beep();
                } catch (Exception ignored) {}
            }
        }, 0, 1500);

        SwingUtilities.invokeLater(() -> {
            try {
                // Create modern alarm dialog
                JDialog alarmDialog = new JDialog((Frame) null, "⏰ MEDICINE REMINDER", true);
                alarmDialog.setAlwaysOnTop(true);
                alarmDialog.setSize(450, 250);
                alarmDialog.setLocationRelativeTo(null);
                alarmDialog.setLayout(new BorderLayout(15, 15));
                alarmDialog.getContentPane().setBackground(new Color(58, 56, 144));
                
                // Info panel
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new javax.swing.BoxLayout(infoPanel, javax.swing.BoxLayout.Y_AXIS));
                infoPanel.setOpaque(false);
                infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
                
                JLabel titleLabel = new JLabel("⏰ TIME TO TAKE YOUR MEDICINE!");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                infoPanel.add(titleLabel);
                
                infoPanel.add(javax.swing.Box.createVerticalStrut(15));
                
                JLabel medLabel = new JLabel("Medicine: " + reminder.getMedicineName());
                medLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                medLabel.setForeground(new Color(220, 220, 235));
                medLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                infoPanel.add(medLabel);
                
                String timeStr = reminder.getReminderTime() != null ? 
                    reminder.getReminderTime().format(DISPLAY_FMT) : "now";
                JLabel timeLabel = new JLabel("Time: " + timeStr);
                timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                timeLabel.setForeground(new Color(200, 200, 220));
                timeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                infoPanel.add(timeLabel);
                
                alarmDialog.add(infoPanel, BorderLayout.CENTER);
                
                // Button panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                buttonPanel.setOpaque(false);
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));
                
                JButton takenBtn = createAlarmButton("✓ TAKEN", new Color(46, 204, 113), new Color(39, 174, 96));
                JButton missBtn = createAlarmButton("✗ MISS", new Color(231, 76, 60), new Color(192, 57, 43));
                JButton snoozeBtn = createAlarmButton("⏰ SNOOZE 5m", new Color(241, 196, 15), new Color(243, 156, 18));
                
                takenBtn.addActionListener(e -> {
                    handleAlarmAction(reminder, "TAKEN", beepTimer);
                    alarmDialog.dispose();
                });
                
                missBtn.addActionListener(e -> {
                    handleAlarmAction(reminder, "MISS", beepTimer);
                    alarmDialog.dispose();
                });
                
                snoozeBtn.addActionListener(e -> {
                    handleAlarmAction(reminder, "SNOOZE", beepTimer);
                    alarmDialog.dispose();
                });
                
                buttonPanel.add(takenBtn);
                buttonPanel.add(snoozeBtn);
                buttonPanel.add(missBtn);
                
                alarmDialog.add(buttonPanel, BorderLayout.SOUTH);
                
                // Handle window close (X button) - stop beep
                alarmDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        beepTimer.cancel();
                        alarmingReminderIds.remove(reminder.getId());
                    }
                });
                
                alarmDialog.setVisible(true);
                
            } catch (Exception ex) {
                // Fallback to simple dialog
                beepTimer.cancel();
                int result = JOptionPane.showConfirmDialog(null,
                    "TIME TO TAKE: " + reminder.getMedicineName() + "\nTime: " + 
                    (reminder.getReminderTime() != null ? reminder.getReminderTime().format(DISPLAY_FMT) : "now"),
                    "⏰ MEDICINE REMINDER",
                    JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    markTaken(reminder.getId());
                } else {
                    markMissed(reminder.getId());
                }
                alarmingReminderIds.remove(reminder.getId());
            }
        });
    }
    
    private JButton createAlarmButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    /**
     * Handle alarm user action: TAKEN, MISS, SNOOZE, or STOP.
     * Stops beeping immediately and updates reminder status if needed.
     */
    private void handleAlarmAction(Reminder reminder, String action, Timer beepTimer) {
        // Stop beeping immediately
        beepTimer.cancel();
        alarmingReminderIds.remove(reminder.getId());

        // Update reminder status based on action
        if ("TAKEN".equals(action)) {
            markTaken(reminder.getId());
            logger.info("Reminder marked TAKEN: {}", reminder.getMedicineName());
        } else if ("MISS".equals(action)) {
            markMissed(reminder.getId());
            logger.info("Reminder marked MISSED: {}", reminder.getMedicineName());
        } else if ("SNOOZE".equals(action)) {
            // Snooze for 5 minutes - re-trigger alarm after delay
            Timer snoozeTimer = new Timer("SnoozeTimer", true);
            snoozeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    triggerAlarm(reminder);
                }
            }, 5 * 60 * 1000); // 5 minutes
            logger.info("Reminder snoozed for 5 minutes: {}", reminder.getMedicineName());
        }
        // If STOP, just close without changing status (leaves it as PENDING for later)
    }

    public Reminder addReminder(Reminder reminder) {
        if (reminder == null || reminder.getMedicineName() == null || reminder.getReminderTime() == null) {
            logger.warn("Cannot add invalid reminder");
            return null;
        }
        reminder.setStatus(Status.PENDING);
        int newId = dbManager.addReminder(reminder);
        if (newId > 0) {
            reminder.setId(newId);
        } else {
            reminder.setId(idGenerator.getAndIncrement());
        }
        reminders.add(reminder);
        logger.info("Reminder added: {} at {}", reminder.getMedicineName(), reminder.getReminderTime());
        notifyReminderDataChanged();
        return reminder;
    }

    public boolean deleteReminder(int reminderId) {
        boolean removed = dbManager.deleteReminder(reminderId);
        if (removed) {
            reminders.removeIf(r -> r.getId() == reminderId);
            logger.info("Reminder deleted: {}", reminderId);
            notifyReminderDataChanged();
        }
        return removed;
    }

    public boolean updateReminder(Reminder reminder) {
        if (reminder == null || reminder.getId() <= 0) return false;
        boolean success = dbManager.updateReminder(reminder);
        if (!success) {
            return false;
        }
        synchronized (reminders) {
            for (int i = 0; i < reminders.size(); i++) {
                if (reminders.get(i).getId() == reminder.getId()) {
                    reminders.set(i, reminder);
                    notifyReminderDataChanged();
                    return true;
                }
            }
        }
        return true;
    }

    public List<Reminder> getAllReminders() {
        synchronized (reminders) {
            return new ArrayList<>(reminders);
        }
    }

    public Reminder getReminderById(int reminderId) {
        synchronized (reminders) {
            return reminders.stream().filter(r -> r.getId() == reminderId).findFirst().orElse(null);
        }
    }

    public List<Reminder> getPendingReminders() {
        synchronized (reminders) {
            return reminders.stream()
                    .filter(r -> r.getStatus() == Status.PENDING)
                    .collect(Collectors.toList());
        }
    }

    public List<Reminder> getDueReminders(LocalDateTime now) {
        synchronized (reminders) {
            return reminders.stream()
                    .filter(r -> r.getStatus() == Status.PENDING && !r.getReminderTime().isAfter(now))
                    .collect(Collectors.toList());
        }
    }

    public void markTaken(int reminderId) {
        Reminder r = getReminderById(reminderId);
        if (r != null) {
            r.setStatus(Status.TAKEN);
            updateReminder(r);
            recordHistory(r, DoseHistory.STATUS_TAKEN);
            decreaseInventory(r);
        }
    }

    public void markMissed(int reminderId) {
        Reminder r = getReminderById(reminderId);
        if (r != null) {
            r.setStatus(Status.MISSED);
            updateReminder(r);
            recordHistory(r, DoseHistory.STATUS_MISSED);
        }
    }

    public void snooze(int reminderId, int minutes) {
        Reminder r = getReminderById(reminderId);
        if (r != null) {
            r.setReminderTime(r.getReminderTime().plusMinutes(minutes));
            r.setStatus(Status.PENDING);
            updateReminder(r);
        }
    }

    /**
     * Get count of pending reminders
     */
    public int getPendingCount() {
        synchronized (reminders) {
            return (int) reminders.stream()
                    .filter(r -> r.getStatus() == Status.PENDING)
                    .count();
        }
    }

    public List<Reminder> getRemindersSortedByTime() {
        return getAllReminders().stream()
                .sorted(Comparator.comparing(Reminder::getReminderTime))
                .collect(Collectors.toList());
    }

    private void recordHistory(Reminder reminder, String statusLabel) {
        if (historyController == null || reminder.getReminderTime() == null) {
            return;
        }
        DoseHistory history = new DoseHistory();
        history.setMedicineId(0);
        history.setReminderId(reminder.getId());
        history.setMedicineName(reminder.getMedicineName());
        history.setDate(reminder.getReminderTime().toLocalDate());
        history.setTime(reminder.getReminderTime().toLocalTime());
        history.setStatus(statusLabel);
        historyController.addHistory(history);
    }

    private void decreaseInventory(Reminder reminder) {
        if (inventoryController == null) {
            return;
        }
        Inventory inv = inventoryController.getInventoryByMedicineName(reminder.getMedicineName());
        if (inv != null) {
            inventoryController.decreaseQuantity(inv.getMedicineId(), 1);
        }
    }

    /**
     * Cleanup method to stop all background services and timers
     */
    public void cleanup() {
        logger.info("ReminderController cleanup started...");
        stopBasicAlarmTimer();
        alarmingReminderIds.clear();
        logger.info("ReminderController cleanup completed");
    }
    
    /**
     * Add data change listener
     */
    public void addDataChangeListener(DataChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove data change listener
     */
    public void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that reminder data has changed
     */
    private void notifyReminderDataChanged() {
        for (DataChangeListener listener : listeners) {
            try {
                listener.onReminderDataChanged();
            } catch (Exception e) {
                logger.error("Error notifying listener", e);
            }
        }
    }
}
