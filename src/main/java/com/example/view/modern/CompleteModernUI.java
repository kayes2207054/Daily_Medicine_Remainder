package com.example.view.modern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Signup UI with Glassmorphism Design
 * Complete standalone implementation with all custom components
 */
public class CompleteModernUI extends JFrame {

    // UI Components
    private RoundedTextField firstNameField;
    private RoundedTextField lastNameField;
    private RoundedTextField emailField;
    private RoundedPasswordField passwordField;
    private RoundedPasswordField confirmPasswordField;
    private JCheckBox termsCheckbox;
    private GradientButton signupButton;
    private JButton loginLinkButton;
    private JButton closeButton;

    // Animation
    private Timer fadeInTimer;
    private float opacity = 0.0f;

    public CompleteModernUI() {
        initComponents();
        startFadeInAnimation();
    }

    private void initComponents() {
        setTitle("DailyDose - Create Account");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 700, 30, 30));

        // Main panel with dark background (matching image)
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2.setColor(new Color(35, 35, 48)); // Darker blue-gray like image
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBackground(new Color(35, 35, 48));

        // Close button
        closeButton = createCloseButton();
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topBar.setOpaque(false);
        topBar.add(closeButton);

        // LEFT SIDE - Image panel with overlay
        JPanel leftPanel = createLeftPanel();

        // RIGHT SIDE - Form panel
        JPanel rightPanel = createRightPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Make frame draggable
        makeDraggable(topBar);
    }

    private JButton createCloseButton() {
        JButton btn = new JButton("✕");
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(new Color(255, 100, 100));
        btn.setBackground(new Color(50, 50, 65));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> System.exit(0));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 70, 70));
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(50, 50, 65));
                btn.setForeground(new Color(255, 100, 100));
            }
        });
        
        return btn;
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
                setLocation(currCoords.x - mouseDownCompCoords[0].x, currCoords.y - mouseDownCompCoords[0].y);
            }
        });
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient matching the image theme (purple-blue)
                GradientPaint gp1 = new GradientPaint(
                        0, 0, new Color(88, 86, 214),      // Lighter purple-blue
                        0, getHeight(), new Color(58, 56, 144)  // Darker purple-blue
                );
                g2.setPaint(gp1);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle dark overlay for depth
                g2.setColor(new Color(30, 20, 60, 40));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new GridBagLayout());

        // Logo and title
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Logo text (like AMU in image)
        JLabel logoLabel = new JLabel("DD");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 60));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(logoLabel);
        contentPanel.add(Box.createVerticalStrut(40));

        // Main tagline (like "Capturing Moments..." in image)
        JLabel titleLabel = new JLabel("Managing Medicine");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Creating Wellness");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(50));

        // Tagline
        JLabel taglineLabel = new JLabel("Daily Dose - Stay Healthy");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        taglineLabel.setForeground(new Color(255, 255, 255, 160));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(taglineLabel);

        panel.add(contentPanel);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 700));
        panel.setBackground(new Color(35, 35, 48));
        panel.setLayout(new GridBagLayout());

        // Glass card
        RoundedPanel glassCard = new RoundedPanel(30);
        glassCard.setGlassmorphism(true);
        glassCard.setPreferredSize(new Dimension(420, 620));
        glassCard.setLayout(new BoxLayout(glassCard, BoxLayout.Y_AXIS));
        glassCard.setBorder(BorderFactory.createEmptyBorder(40, 35, 40, 35));

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassCard.add(titleLabel);
        glassCard.add(Box.createVerticalStrut(8));

        JLabel subtitleLabel = new JLabel("Join DailyDose community today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassCard.add(subtitleLabel);
        glassCard.add(Box.createVerticalStrut(35));

        // Form fields
        addFormField(glassCard, "First Name", firstNameField = new RoundedTextField(20));
        addFormField(glassCard, "Last Name", lastNameField = new RoundedTextField(20));
        addFormField(glassCard, "Email Address", emailField = new RoundedTextField(20));
        addFormField(glassCard, "Password", passwordField = new RoundedPasswordField(20));
        addFormField(glassCard, "Confirm Password", confirmPasswordField = new RoundedPasswordField(20));

        // Terms checkbox
        termsCheckbox = new JCheckBox("<html>I agree to <span style='color: #8A2BE2;'>Terms & Conditions</span></html>");
        termsCheckbox.setForeground(new Color(200, 200, 220));
        termsCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        termsCheckbox.setOpaque(false);
        termsCheckbox.setFocusPainted(false);
        termsCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        glassCard.add(termsCheckbox);
        glassCard.add(Box.createVerticalStrut(25));

        // Signup button
        signupButton = new GradientButton("Create Account");
        signupButton.setMaximumSize(new Dimension(350, 48));
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.addActionListener(e -> handleSignup());
        glassCard.add(signupButton);
        glassCard.add(Box.createVerticalStrut(20));

        // Login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setOpaque(false);
        loginPanel.setMaximumSize(new Dimension(350, 30));

        JLabel alreadyLabel = new JLabel("Already have an account?");
        alreadyLabel.setForeground(new Color(200, 200, 220));
        alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        loginLinkButton = new JButton("Login");
        loginLinkButton.setForeground(new Color(138, 43, 226));
        loginLinkButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginLinkButton.setBorderPainted(false);
        loginLinkButton.setContentAreaFilled(false);
        loginLinkButton.setFocusPainted(false);
        loginLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLinkButton.addActionListener(e -> handleLogin());

        loginPanel.add(alreadyLabel);
        loginPanel.add(loginLinkButton);
        glassCard.add(loginPanel);

        panel.add(glassCard);
        return panel;
    }

    private void addFormField(JPanel parent, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(220, 220, 235));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(350, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(6));
        parent.add(field);
        parent.add(Box.createVerticalStrut(18));
    }

    private void handleSignup() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage("Please fill all fields", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match", false);
            return;
        }

        if (!termsCheckbox.isSelected()) {
            showMessage("Please accept terms & conditions", false);
            return;
        }

        showMessage("Account created successfully!", true);
    }

    private void handleLogin() {
        showMessage("Navigate to Login page", true);
    }

    private void showMessage(String message, boolean success) {
        JOptionPane.showMessageDialog(this, message,
                success ? "Success" : "Error",
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void startFadeInAnimation() {
        fadeInTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    fadeInTimer.stop();
                }
                repaint();
            }
        });
        fadeInTimer.start();
    }

    // ==================== CUSTOM COMPONENTS ====================

    /**
     * Rounded Panel with Glassmorphism Effect
     */
    static class RoundedPanel extends JPanel {
        private int cornerRadius = 25;
        private boolean glassmorphism = false;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }

        public void setGlassmorphism(boolean enabled) {
            this.glassmorphism = enabled;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            if (glassmorphism) {
                // Glass background
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

                // Glass border
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, width - 3, height - 3, cornerRadius, cornerRadius);

                // Subtle shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(4, 4, width - 8, height - 8, cornerRadius, cornerRadius);
            }

            g2.dispose();
        }
    }

    /**
     * Modern Rounded Text Field
     */
    static class RoundedTextField extends JTextField {
        private int cornerRadius = 15;
        private Color borderColor = new Color(180, 180, 200);
        private Color focusBorderColor = new Color(138, 43, 226);

        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBackground(new Color(255, 255, 255, 220));
            setForeground(new Color(30, 30, 30));
            setCaretColor(new Color(138, 43, 226));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new EmptyBorder(12, 18, 12, 18));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hasFocus()) {
                g2.setColor(focusBorderColor);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1));
            }

            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }

    /**
     * Modern Rounded Password Field
     */
    static class RoundedPasswordField extends JPasswordField {
        private int cornerRadius = 15;
        private Color borderColor = new Color(180, 180, 200);
        private Color focusBorderColor = new Color(138, 43, 226);

        public RoundedPasswordField(int columns) {
            super(columns);
            setOpaque(false);
            setBackground(new Color(255, 255, 255, 220));
            setForeground(new Color(30, 30, 30));
            setCaretColor(new Color(138, 43, 226));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new EmptyBorder(12, 18, 12, 18));
            setEchoChar('●');

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hasFocus()) {
                g2.setColor(focusBorderColor);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1));
            }

            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }

    /**
     * Gradient Button with Hover Animation
     */
    static class GradientButton extends JButton {
        private Color color1 = new Color(88, 86, 214);  // Matching image purple
        private Color color2 = new Color(121, 119, 255);
        private Color hoverColor1 = new Color(108, 106, 234);
        private Color hoverColor2 = new Color(141, 139, 255);
        private boolean isHovered = false;
        private int cornerRadius = 18;

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

            int width = getWidth();
            int height = getHeight();

            GradientPaint gp;
            if (isHovered) {
                gp = new GradientPaint(0, 0, hoverColor1, width, height, hoverColor2);
            } else {
                gp = new GradientPaint(0, 0, color1, width, height, color2);
            }

            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

            // Subtle shadow when not hovered
            if (!isHovered) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(3, 3, width - 6, height - 6, cornerRadius, cornerRadius);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================== MAIN METHOD ====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CompleteModernUI().setVisible(true);
        });
    }
}
