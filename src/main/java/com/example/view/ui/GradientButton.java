package com.example.view.ui;

import javax.swing.*;
import java.awt.*;

public class GradientButton extends JButton {
    private final Color start = new Color(123, 31, 162);
    private final Color end = new Color(142, 36, 170);
    private final Color hoverStart = new Color(171, 71, 188);
    private final Color hoverEnd = new Color(186, 104, 200);
    private boolean hovered = false;
    private int radius = 18;
    private float t = 0f; // 0..1 lerp amount
    private final Timer anim;

    public GradientButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        anim = new Timer(16, e -> {
            float target = hovered ? 1f : 0f;
            t += (target - t) * 0.15f; // ease towards target
            if (Math.abs(target - t) < 0.01f) t = target;
            repaint();
        });
        anim.start();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { hovered = false; }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c1 = lerp(start, hoverStart, t);
        Color c2 = lerp(end, hoverEnd, t);
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
        g2.dispose();
    }

    private Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        int al = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(r, g, bl, al);
    }

    @Override
    protected void paintBorder(Graphics g) { /* no border */ }
}
