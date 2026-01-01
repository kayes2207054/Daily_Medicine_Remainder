package com.example.view.modern;

import javax.swing.*;
import java.awt.*;

/**
 * Custom rounded panel with glassmorphism effect
 */
public class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 25;
    private boolean glassmorphism = false;

    public RoundedPanel(int radius) {
        super();
        this.cornerRadius = radius;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
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

        // Glass effect background
        if (glassmorphism) {
            g2.setColor(new Color(255, 255, 255, 40)); // Semi-transparent white
        } else if (backgroundColor != null) {
            g2.setColor(backgroundColor);
        } else {
            g2.setColor(getBackground());
        }

        // Draw rounded rectangle
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Glass border
        if (glassmorphism) {
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, width - 3, height - 3, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
