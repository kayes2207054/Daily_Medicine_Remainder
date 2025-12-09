package com.example;

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
            new MainFrame();
        });
    }
}
