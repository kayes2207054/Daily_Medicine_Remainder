package com.example.view.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int cornerRadius = 20;
    private Color glassColor = new Color(255, 255, 255, 40);
    private boolean drawShadow = true;

    public RoundedPanel() {
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bg) {
        this();
        this.cornerRadius = radius;
        this.glassColor = bg;
    }

    public void setCornerRadius(int cornerRadius) { this.cornerRadius = cornerRadius; }
    public void setGlassColor(Color glassColor) { this.glassColor = glassColor; }
    public void setDrawShadow(boolean drawShadow) { this.drawShadow = drawShadow; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (drawShadow) {
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(6, 10, w - 12, h - 12, cornerRadius + 4, cornerRadius + 4);
        }

        g2.setColor(glassColor);
        g2.fillRoundRect(0, 0, w - 6, h - 6, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
