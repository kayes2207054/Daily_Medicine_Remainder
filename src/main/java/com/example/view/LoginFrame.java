package com.example.view;

import com.example.controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private AuthController authController;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    
    public LoginFrame() {
        authController = new AuthController();
        setTitle("DailyDose - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel lblTitle = new JLabel("DailyDose Login");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);
        
        tfUsername = new JTextField(15);
        gbc.gridx = 1;
        add(tfUsername, gbc);
        
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);
        
        pfPassword = new JPasswordField(15);
        gbc.gridx = 1;
        add(pfPassword, gbc);
        
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        
        btnLogin.addActionListener(e -> attemptLogin());
        btnRegister.addActionListener(e -> attemptRegister());
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);
        
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }
    
    private void attemptLogin() {
        String user = tfUsername.getText();
        String pass = new String(pfPassword.getPassword());
        
        if(authController.login(user, pass)) {
            new MainFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login Failed: " + authController.getLastError());
        }
    }
    
    private void attemptRegister() {
        String user = tfUsername.getText();
        String pass = new String(pfPassword.getPassword());
        
        // Auto register for simplicity since we don't have a separate screen
        if(authController.register(user, pass, pass, user)) {
            JOptionPane.showMessageDialog(this, "Registered! Logging in...");
            attemptLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed: " + authController.getLastError());
        }
    }
}
