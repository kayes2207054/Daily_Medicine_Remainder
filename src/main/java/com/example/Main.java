package com.example;

import com.example.view.LoginFrame;
import com.example.utils.DataSeeder;

import javax.swing.*;

/**
 * Main entry point for Swing version of DailyDose
 * Legacy entry point - kept for compatibility
 */
public class Main {
    public static void main(String[] args) {
        // Seed sample data
        DataSeeder.seedSampleData();
        
        // Set Nimbus Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to default
        }
        
        // Launch Swing UI
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
