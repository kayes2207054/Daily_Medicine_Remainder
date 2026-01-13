package com.example.view;

import com.example.controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private final AuthController authController = new AuthController();
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public SignupFrame() {
        setTitle("DailyDose - Sign Up");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(45, 52, 54));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        
        // Name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(nameField, gbc);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        centerPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(usernameField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        centerPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        centerPanel.add(passwordField, gbc);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 4;
        centerPanel.add(confirmLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        centerPanel.add(confirmPasswordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(46, 204, 113));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.addActionListener(e -> handleSignup());
        buttonPanel.add(signupButton);

        JButton backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        centerPanel.add(buttonPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void handleSignup() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (name.trim().isEmpty() || username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authController.register(username, password, confirm, name)) {
            JOptionPane.showMessageDialog(this, "Account created successfully! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            goBack();
        } else {
            JOptionPane.showMessageDialog(this, authController.getLastError(), "Signup Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
