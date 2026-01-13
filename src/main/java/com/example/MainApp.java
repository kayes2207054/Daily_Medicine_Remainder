package com.example;

import com.example.view.LoginFrame;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set simple look and feel or default
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch(Exception ignored){}
            
            new LoginFrame().setVisible(true);
        });
    }
}
