package com.example;

import com.example.controller.AuthController;
import com.example.view.LoginFrame;
import com.example.view.SignupFrame;
import com.example.view.MainFrame;
import com.example.utils.DataSeeder;
import javax.swing.SwingUtilities;

/**
 * Main Class
 * Entry point for the DailyDose application.
 * Initializes the database and launches the GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Seed sample data if first run
        DataSeeder.seedSampleData();
        
        // Launch GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            AuthController auth = new AuthController();
            if (auth.isFirstTimeUser()) {
                // No users yet: show signup directly
                SignupFrame sf = new SignupFrame(auth, null);
                sf.setVisible(true);
            } else {
                // Show login frame first
                LoginFrame lf = new LoginFrame(auth);
                lf.setVisible(true);
            }
        });
    }
}
