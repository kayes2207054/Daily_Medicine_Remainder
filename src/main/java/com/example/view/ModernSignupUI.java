package com.example.view;

import com.example.view.ui.GradientButton;
import com.example.view.ui.RoundedPanel;
import com.example.view.ui.RoundedPasswordField;
import com.example.view.ui.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStream;

public class ModernSignupUI extends JFrame {
    public ModernSignupUI() {
        super("DailyDose - Create an Account");
        setUndecorated(true);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color bg = new Color(31, 31, 46); // #1F1F2E
        getContentPane().setBackground(bg);

        // Left Panel with background image and overlay
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Try to draw a background image if available
                Image img = loadImage("/login_bg.jpg");
                if (img != null) {
                    g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback gradient as background
                    GradientPaint gp = new GradientPaint(0, 0, new Color(44, 44, 66), 0, getHeight(), new Color(60, 60, 90));
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // Fade overlay
                g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0,150), 0, getHeight(), new Color(0,0,0,0)));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Title centered
                g2.setFont(getModernFont(36f, Font.BOLD));
                g2.setColor(Color.WHITE);
                String title = "Daily Dose – Stay Healthy";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                int y = getHeight() / 2;
                g2.drawString(title, x, y);
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(460, 600));

        // Right Panel with glassmorphic card and form (with fade-in)
        FadePanel right = new FadePanel(new GridBagLayout());
        right.setOpaque(false);

        RoundedPanel card = new RoundedPanel(24, new Color(255, 255, 255, 40));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(8,8,8,8); c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0;

        JLabel title = new JLabel("Create an Account");
        title.setFont(getModernFont(28f, Font.BOLD));
        title.setForeground(Color.WHITE);
        card.add(title, c);

        c.gridy++;
        RoundedTextField firstName = new RoundedTextField(20); firstName.setToolTipText("First name"); firstName.setForeground(Color.WHITE);
        card.add(wrapLabeled("First name", firstName), c);

        c.gridy++;
        RoundedTextField lastName = new RoundedTextField(20); lastName.setToolTipText("Last name"); lastName.setForeground(Color.WHITE);
        card.add(wrapLabeled("Last name", lastName), c);

        c.gridy++;
        RoundedTextField email = new RoundedTextField(20); email.setToolTipText("Email"); email.setForeground(Color.WHITE);
        card.add(wrapLabeled("Email", email), c);

        c.gridy++;
        RoundedPasswordField pass = new RoundedPasswordField(20); pass.setToolTipText("Password");
        card.add(wrapLabeled("Password", pass), c);

        c.gridy++;
        RoundedPasswordField confirm = new RoundedPasswordField(20); confirm.setToolTipText("Confirm password");
        card.add(wrapLabeled("Confirm password", confirm), c);

        c.gridy++;
        JCheckBox terms = new JCheckBox("I agree to Terms");
        terms.setOpaque(false); terms.setForeground(Color.WHITE);
        card.add(terms, c);

        c.gridy++;
        GradientButton signup = new GradientButton("Register");
        signup.setPreferredSize(new Dimension(160, 40));
        card.add(signup, c);

        c.gridy++;
        JButton loginLink = new JButton("<html><u>Already have an account? Login</u></html>");
        loginLink.setForeground(new Color(180, 180, 220));
        loginLink.setOpaque(false); loginLink.setContentAreaFilled(false); loginLink.setBorderPainted(false);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(loginLink, c);

        GridBagConstraints r = new GridBagConstraints();
        r.gridx = 0; r.gridy = 0; r.insets = new Insets(0, 40, 0, 40); r.fill = GridBagConstraints.BOTH; r.weightx = 1.0; r.weighty = 1.0;
        right.add(card, r);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        // Rounded window shape
        applyRoundedWindow(24);
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { applyRoundedWindow(24); }
        });

        // Start fade-in animation for right panel
        right.startFadeIn();
    }

    private JPanel wrapLabeled(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(label);
        l.setForeground(new Color(220, 220, 240));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(6)); p.add(field);
        return p;
    }

    private Font getModernFont(float size, int style) {
        // Try to load a bundled TTF (optional). Fallback to system fonts.
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf");
            if (is != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                return f.deriveFont(style, size);
            }
        } catch (Exception ignored) {}
        // Try common modern fonts by name
        String[] names = {"Poppins", "Montserrat", "Roboto", "Segoe UI", "SansSerif"};
        for (String n : names) {
            Font f = new Font(n, style, (int) size);
            if (f != null) return f;
        }
        return new Font("SansSerif", style, (int) size);
    }

    private Image loadImage(String resourcePath) {
        try {
            java.net.URL url = getClass().getResource(resourcePath);
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void applyRoundedWindow(int radius) {
        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        } catch (Throwable ignored) {
            // setShape may not be supported on some platforms/JREs; ignore gracefully
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernSignupUI().setVisible(true));
    }

    // Fade-in wrapper panel for subtle appearance animation
    static class FadePanel extends JPanel {
        private float alpha = 0f; // 0..1
        private final Timer timer;

        FadePanel(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            timer = new Timer(16, e -> {
                alpha += 0.05f;
                if (alpha >= 1f) { alpha = 1f; ((Timer) e.getSource()).stop(); }
                repaint();
            });
        }

        void startFadeIn() { alpha = 0f; timer.start(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
