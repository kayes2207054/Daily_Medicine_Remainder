package com.example.service;

import com.example.controller.ReminderController;
import com.example.model.Reminder;
import com.example.model.Reminder.Status;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ReminderService runs a background scheduler to check pending reminders
 * every 30 seconds and triggers an alarm popup when due.
 */
public class ReminderService {
    private final ReminderController controller;
    private ScheduledExecutorService scheduler;
    private int snoozeMinutes = 5;
    private int missAfterMinutes = 10;

    public ReminderService(ReminderController controller) {
        this.controller = controller;
    }

    public void start() {
        stop();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkReminders, 0, 30, TimeUnit.SECONDS);
    }

    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> due = controller.getDueReminders(now);
        for (Reminder r : due) {
            SwingUtilities.invokeLater(() -> showAlarmDialog(r));
        }
    }

    private void showAlarmDialog(Reminder reminder) {
        // Avoid duplicate popups if status already changed
        if (reminder.getStatus() != Status.PENDING) return;

        Toolkit.getDefaultToolkit().beep();
        JDialog dialog = new JDialog((Frame) null, "Reminder Alert", true);
        dialog.setAlwaysOnTop(true);
        dialog.setSize(360, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        info.add(new JLabel("Medicine: " + reminder.getMedicineName()));
        String time = reminder.getReminderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        info.add(new JLabel("Scheduled: " + time));
        dialog.add(info, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton takenBtn = new JButton("Taken");
        JButton snoozeBtn = new JButton("Snooze " + snoozeMinutes + "m");
        JButton closeBtn = new JButton("Close");

        takenBtn.addActionListener(e -> {
            controller.markTaken(reminder.getId());
            dialog.dispose();
        });

        snoozeBtn.addActionListener(e -> {
            controller.snooze(reminder.getId(), snoozeMinutes);
            dialog.dispose();
        });

        closeBtn.addActionListener(e -> {
            // if ignored, we'll mark missed after missAfterMinutes via another check
            dialog.dispose();
        });

        buttons.add(takenBtn);
        buttons.add(snoozeBtn);
        buttons.add(closeBtn);
        dialog.add(buttons, BorderLayout.SOUTH);

        dialog.setVisible(true);

        // If dialog was closed without action and still pending after missAfterMinutes, mark missed
        SwingUtilities.invokeLater(() -> scheduleMissCheck(reminder.getId()));
    }

    private void scheduleMissCheck(int reminderId) {
        if (scheduler == null || scheduler.isShutdown()) return;
        scheduler.schedule(() -> {
            Reminder r = controller.getReminderById(reminderId);
            if (r != null && r.getStatus() == Status.PENDING && !r.getReminderTime().isAfter(LocalDateTime.now())) {
                controller.markMissed(reminderId);
            }
        }, missAfterMinutes, TimeUnit.MINUTES);
    }
}
