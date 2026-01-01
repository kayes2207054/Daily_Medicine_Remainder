package com.example.view.modern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern gradient button with hover effects
 */
public class GradientButton extends JButton {
    private Color color1 = new Color(138, 43, 226); // Purple
    private Color color2 = new Color(75, 0, 130);   // Violet
    private Color hoverColor1 = new Color(160, 60, 240);
    private Color hoverColor2 = new Color(95, 20, 150);
    private boolean isHovered = false;
    private int cornerRadius = 18;
    private Shape shape;

    public GradientButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
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

    public void setGradientColors(Color c1, Color c2) {
        this.color1 = c1;
        this.color2 = c2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Gradient background
        GradientPaint gp;
        if (isHovered) {
            gp = new GradientPaint(0, 0, hoverColor1, width, height, hoverColor2);
        } else {
            gp = new GradientPaint(0, 0, color1, width, height, color2);
        }

        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Shadow effect
        if (!isHovered) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(2, 2, width - 2, height - 2, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
        return shape.contains(x, y);
    }
}
