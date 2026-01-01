package com.example.view;

import com.example.controller.AuthController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {
    private final AuthController authController = new AuthController();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("DailyDose - Login");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 600, 25, 25));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Main panel with dark background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(35, 35, 48));
        
        // Close button
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setForeground(new Color(255, 100, 100));
        closeBtn.setBackground(new Color(50, 50, 65));
        closeBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topBar.setOpaque(false);
        topBar.add(closeBtn);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // LEFT SIDE - Purple gradient
        JPanel leftPanel = createLeftPanel();
        
        // RIGHT SIDE - Login form
        JPanel rightPanel = createRightPanel();
        
        contentPanel.add(leftPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Make draggable
        makeDraggable(topBar);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(88, 86, 214),
                    0, getHeight(), new Color(58, 56, 144)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        panel.setLayout(new GridBagLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        
        JLabel logo = new JLabel("💊");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(logo);
        content.add(Box.createVerticalStrut(20));
        
        JLabel title = new JLabel("DailyDose");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(10));
        
        JLabel subtitle = new JLabel("Your Medicine Companion");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(subtitle);
        
        panel.add(content);
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(420, 600));
        panel.setBackground(new Color(35, 35, 48));
        panel.setLayout(new GridBagLayout());
        
        // Glass card
        RoundedPanel glassCard = new RoundedPanel(25);
        glassCard.setPreferredSize(new Dimension(360, 480));
        glassCard.setLayout(new BoxLayout(glassCard, BoxLayout.Y_AXIS));
        glassCard.setBorder(BorderFactory.createEmptyBorder(35, 30, 35, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassCard.add(titleLabel);
        glassCard.add(Box.createVerticalStrut(8));
        
        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassCard.add(subtitleLabel);
        glassCard.add(Box.createVerticalStrut(30));
        
        // Username field
        addFormField(glassCard, "Username");
        usernameField = new RoundedTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 42));
        glassCard.add(usernameField);
        glassCard.add(Box.createVerticalStrut(18));
        
        // Password field
        addFormField(glassCard, "Password");
        passwordField = new RoundedPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 42));
        glassCard.add(passwordField);
        glassCard.add(Box.createVerticalStrut(25));
        
        // Login button
        GradientButton loginButton = new GradientButton("Login");
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());
        glassCard.add(loginButton);
        glassCard.add(Box.createVerticalStrut(15));
        
        // Signup link
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setOpaque(false);
        JLabel askLabel = new JLabel("Don't have an account?");
        askLabel.setForeground(new Color(200, 200, 220));
        askLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setForeground(new Color(88, 86, 214));
        signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signupBtn.setBorderPainted(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupBtn.addActionListener(e -> handleSignup());
        
        signupPanel.add(askLabel);
        signupPanel.add(signupBtn);
        signupPanel.setMaximumSize(new Dimension(300, 30));
        glassCard.add(signupPanel);
        
        panel.add(glassCard);
        return panel;
    }
    
    private void addFormField(JPanel parent, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(220, 220, 235));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(label);
        parent.add(Box.createVerticalStrut(6));
    }
    
    private void makeDraggable(Component component) {
        final Point[] mouseDownCompCoords = {null};
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords[0] = e.getPoint();
            }
        });
        component.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords[0].x, 
                           currCoords.y - mouseDownCompCoords[0].y);
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authController.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            });
        } else {
            String error = authController.getLastError();
            JOptionPane.showMessageDialog(this, error != null ? error : "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSignup() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            SignupFrame signupFrame = new SignupFrame();
            signupFrame.setVisible(true);
        });
    }
    
    // Custom rounded panel for glassmorphism effect
    class RoundedPanel extends JPanel {
        private int radius;
        
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(58, 56, 144, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            g2.setColor(new Color(255, 255, 255, 10));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom rounded text field
    class RoundedTextField extends JTextField {
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBackground(new Color(50, 50, 70));
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom rounded password field
    class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField(int columns) {
            super(columns);
            setOpaque(false);
            setBackground(new Color(50, 50, 70));
            setForeground(Color.WHITE);
            setCaretColor(Color.WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Custom gradient button
    class GradientButton extends JButton {
        private Color color1 = new Color(88, 86, 214);
        private Color color2 = new Color(108, 106, 234);
        private boolean isHovered = false;
        
        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isHovered) {
                g2.setPaint(new GradientPaint(0, 0, color2, 0, getHeight(), color1));
            } else {
                g2.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            
            super.paintComponent(g);
        }
    }
}
