package com.example.view.modern;

import javax.swing.*;
import java.awt.*;

/**
 * Modern Signup UI with glassmorphism design
 */
public class ModernSignupUI extends JFrame {
    
    private RoundedTextField firstNameField;
    private RoundedTextField lastNameField;
    private RoundedTextField emailField;
    private RoundedPasswordField passwordField;
    private RoundedPasswordField confirmPasswordField;
    private JCheckBox termsCheckbox;
    private GradientButton signupButton;
    private JButton loginLinkButton;

    public ModernSignupUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("DailyDose - Create Account");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        
        // Main panel with dark background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(31, 31, 46)); // #1F1F2E
        
        // LEFT SIDE - Image panel with overlay
        JPanel leftPanel = createLeftPanel();
        
        // RIGHT SIDE - Form panel
        JPanel rightPanel = createRightPanel();
        
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        setContentPane(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient overlay
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(138, 43, 226, 200),
                    0, getHeight(), new Color(75, 0, 130, 220)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        panel.setLayout(new GridBagLayout());
        
        // Title text
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h1 style='font-size: 48px; color: white; font-family: Arial, sans-serif;'>Daily Dose</h1>" +
                "<p style='font-size: 20px; color: rgba(255,255,255,0.9);'>Stay Healthy, Stay Happy</p>" +
                "</div></html>");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(titleLabel);
        
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(450, 650));
        panel.setBackground(new Color(31, 31, 46));
        panel.setLayout(new GridBagLayout());
        
        // Glass card
        RoundedPanel glassCard = new RoundedPanel(25);
        glassCard.setGlassmorphism(true);
        glassCard.setPreferredSize(new Dimension(380, 580));
        glassCard.setLayout(new BoxLayout(glassCard, BoxLayout.Y_AXIS));
        glassCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        glassCard.add(titleLabel);
        glassCard.add(Box.createVerticalStrut(10));
        
        JLabel subtitleLabel = new JLabel("Join DailyDose today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        glassCard.add(subtitleLabel);
        glassCard.add(Box.createVerticalStrut(30));
        
        // Form fields
        firstNameField = new RoundedTextField(20);
        firstNameField.setMaximumSize(new Dimension(320, 45));
        JLabel firstNameLabel = createLabel("First Name");
        glassCard.add(firstNameLabel);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(firstNameField);
        glassCard.add(Box.createVerticalStrut(15));
        
        lastNameField = new RoundedTextField(20);
        lastNameField.setMaximumSize(new Dimension(320, 45));
        JLabel lastNameLabel = createLabel("Last Name");
        glassCard.add(lastNameLabel);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(lastNameField);
        glassCard.add(Box.createVerticalStrut(15));
        
        emailField = new RoundedTextField(20);
        emailField.setMaximumSize(new Dimension(320, 45));
        JLabel emailLabel = createLabel("Email");
        glassCard.add(emailLabel);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(emailField);
        glassCard.add(Box.createVerticalStrut(15));
        
        passwordField = new RoundedPasswordField(20);
        passwordField.setMaximumSize(new Dimension(320, 45));
        JLabel passwordLabel = createLabel("Password");
        glassCard.add(passwordLabel);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(passwordField);
        glassCard.add(Box.createVerticalStrut(15));
        
        confirmPasswordField = new RoundedPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(320, 45));
        JLabel confirmPasswordLabel = createLabel("Confirm Password");
        glassCard.add(confirmPasswordLabel);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(confirmPasswordField);
        glassCard.add(Box.createVerticalStrut(20));
        
        // Terms checkbox
        termsCheckbox = new JCheckBox("I agree to Terms & Conditions");
        termsCheckbox.setForeground(new Color(200, 200, 220));
        termsCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        termsCheckbox.setOpaque(false);
        termsCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassCard.add(termsCheckbox);
        glassCard.add(Box.createVerticalStrut(20));
        
        // Signup button
        signupButton = new GradientButton("Create Account");
        signupButton.setMaximumSize(new Dimension(320, 45));
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassCard.add(signupButton);
        glassCard.add(Box.createVerticalStrut(15));
        
        // Login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setOpaque(false);
        JLabel alreadyLabel = new JLabel("Already have an account?");
        alreadyLabel.setForeground(new Color(200, 200, 220));
        alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        loginLinkButton = new JButton("Login");
        loginLinkButton.setForeground(new Color(138, 43, 226));
        loginLinkButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLinkButton.setBorderPainted(false);
        loginLinkButton.setContentAreaFilled(false);
        loginLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginPanel.add(alreadyLabel);
        loginPanel.add(loginLinkButton);
        loginPanel.setMaximumSize(new Dimension(320, 30));
        glassCard.add(loginPanel);
        
        panel.add(glassCard);
        
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(220, 220, 235));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ModernSignupUI().setVisible(true);
        });
    }
}
