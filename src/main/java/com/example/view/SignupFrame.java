package com.example.view;

import com.example.controller.AuthController;
import com.example.view.ui.RoundedPasswordField;
import com.example.view.ui.RoundedTextField;
import com.example.view.ui.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SignupFrame extends JFrame {
    private final AuthController authController;
    private final JFrame loginFrameToReturn;

    public SignupFrame(AuthController authController, JFrame loginFrameToReturn) {
        super("DailyDose - Create Account");
        this.authController = authController;
        this.loginFrameToReturn = loginFrameToReturn;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 500, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(31, 31, 46), 0, getHeight(), new Color(50, 50, 70));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setForeground(new Color(200, 150, 255));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        RoundedTextField userField = new RoundedTextField(20);
        userField.setBackground(new Color(60, 60, 80));
        gbc.gridy = 1;
        formPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);

        RoundedPasswordField passField = new RoundedPasswordField(20);
        passField.setBackground(new Color(60, 60, 80));
        gbc.gridy = 3;
        formPanel.add(passField, gbc);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 4;
        formPanel.add(confirmLabel, gbc);

        RoundedPasswordField confirmField = new RoundedPasswordField(20);
        confirmField.setBackground(new Color(60, 60, 80));
        gbc.gridy = 5;
        formPanel.add(confirmField, gbc);

        GradientButton registerBtn = new GradientButton("Register");
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (authController.register(username, password, confirm)) {
                JOptionPane.showMessageDialog(this, "Account created. Please login.");
                if (loginFrameToReturn != null) {
                    loginFrameToReturn.setVisible(true);
                } else {
                    new LoginFrame(authController).setVisible(true);
                }
                dispose();
            }
        });

        JLabel backLabel = new JLabel("<html><u>Back to Login</u></html>");
        backLabel.setForeground(new Color(150, 200, 255));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(backLabel, gbc);

        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (loginFrameToReturn != null) {
                    loginFrameToReturn.setVisible(true);
                } else {
                    new LoginFrame(authController).setVisible(true);
                }
                dispose();
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }
}
