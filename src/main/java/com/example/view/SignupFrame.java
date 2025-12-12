package com.example.view;

import com.example.controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private final AuthController authController;
    private final JFrame loginFrameToReturn;

    public SignupFrame(AuthController authController, JFrame loginFrameToReturn) {
        super("DailyDose - Create Account");
        this.authController = authController;
        this.loginFrameToReturn = loginFrameToReturn;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JLabel confirmLabel = new JLabel("Confirm Password:");
        JPasswordField confirmField = new JPasswordField();

        JButton registerBtn = new JButton("Register");

        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (authController.register(username, password, confirm)) {
                JOptionPane.showMessageDialog(this, "Account created. Please login.");
                if (loginFrameToReturn != null) {
                    loginFrameToReturn.setVisible(true);
                } else {
                    // Fallback to open a new login frame
                    new LoginFrame(authController).setVisible(true);
                }
                dispose();
            }
        });

        panel.add(userLabel); panel.add(userField);
        panel.add(passLabel); panel.add(passField);
        panel.add(confirmLabel); panel.add(confirmField);
        panel.add(new JLabel()); panel.add(registerBtn);

        setContentPane(panel);
    }
}
