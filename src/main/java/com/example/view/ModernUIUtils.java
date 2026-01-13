package com.example.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;

/**
 * Modern UI Utilities - Provides consistent styling across the application
 */
public class ModernUIUtils {
    
    // Color Palette
    public static final Color PRIMARY = new Color(58, 56, 144);
    public static final Color PRIMARY_DARK = new Color(45, 43, 120);
    public static final Color PRIMARY_LIGHT = new Color(98, 96, 184);
    public static final Color ACCENT = new Color(255, 193, 7);
    public static final Color SUCCESS = new Color(46, 125, 50);
    public static final Color WARNING = new Color(245, 124, 0);
    public static final Color DANGER = new Color(211, 47, 47);
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    public static final Color BORDER = new Color(224, 224, 224);
    
    /**
     * Create a modern styled scroll pane with custom scrollbars
     */
    public static JScrollPane createModernScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollPane.getViewport().setBackground(CARD_BG);
        
        // Style vertical scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getVerticalScrollBar().setBackground(BACKGROUND);
        
        // Style horizontal scrollbar
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.getHorizontalScrollBar().setBackground(BACKGROUND);
        
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        return scrollPane;
    }
    
    /**
     * Style a JTable with modern look
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(232, 234, 246));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_DARK));
        header.setReorderingAllowed(false);
        
        // Custom header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, PRIMARY_DARK),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(232, 234, 246));
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                    c.setForeground(TEXT_PRIMARY);
                }
                
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                return c;
            }
        });
    }
    
    /**
     * Create a modern styled button
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        
        return btn;
    }
    
    /**
     * Create a card panel with shadow effect
     */
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 12, 12);
                
                // Card background
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 10, 10);
                
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        return card;
    }
    
    /**
     * Create a styled text field
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }
    
    /**
     * Create a styled combo box
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        combo.setPreferredSize(new Dimension(150, 35));
        return combo;
    }
    
    /**
     * Modern Scrollbar UI
     */
    static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(180, 180, 190);
            this.trackColor = BACKGROUND;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color color = isDragging ? PRIMARY : (isThumbRollover() ? new Color(150, 150, 160) : thumbColor);
            g2.setColor(color);
            
            int arc = 6;
            int margin = 2;
            g2.fillRoundRect(thumbBounds.x + margin, thumbBounds.y + margin, 
                thumbBounds.width - margin * 2, thumbBounds.height - margin * 2, arc, arc);
            
            g2.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
    
    /**
     * Rounded border for components
     */
    static class RoundedBorder implements Border {
        private int radius;
        private Color color;
        
        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
