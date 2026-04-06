package com.still_processing.UILib;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * Create a Rounded Button Class
 * @author Jessica Chen
 */

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private int radius;
    private Color hoverColor;
    private boolean isHovered = false;
    private boolean showBorder = false;
    private int borderThickness = 1;

    public RoundedButton(String text, int radius, Color backgroundColor, Color hoverColor, int fontSize){
        super(text);
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        this.hoverColor = hoverColor;
        setFont(REGULAR_FONT.deriveFont((float)fontSize));
        setForeground(BACKGROUND);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);

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
    /**
     * Draw
     * @author Jessica Chen
     */
    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(isHovered){
            g2d.setColor(hoverColor);
        }
        else{
            g2d.setColor(backgroundColor);
        }
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

        if(showBorder){
            g2d.setColor(backgroundColor.darker());
            g2d.setStroke(new BasicStroke(1));
            int gap = borderThickness/2;
            g2d.drawRoundRect(gap, gap, getWidth() - borderThickness, getHeight() - borderThickness, radius, radius);
        }
        g2d.dispose();
        super.paintComponent(g);
    }
    /**
     * Ensure button has the right area for user hover and pressing.
     * @author Jessica Chen
     */
    @Override
    public boolean contains(int x, int y) {
        return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius).contains(x, y);
    }

    /**
     * Setters for scaling, drawing a border, and setting the background color and hover color.
     * @author Jessica Chen
     */
    public void setShowBorder(boolean showBorder){
        this.showBorder =showBorder;
        repaint();
    }
    private Icon scaleIcon(ImageIcon icon, int size) {
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
    public void setButtonIcon(ImageIcon icon, int iconSize) {
        setIcon(scaleIcon(icon, iconSize));
        setIconTextGap(8);
        repaint();
    }
    public void setBackgroundColor(Color backgroundColor){
        this.backgroundColor = backgroundColor;
        repaint();
    }
    public void setHoverColor(Color hoverColor){
        this.hoverColor = hoverColor;
        repaint();
    }
    public void setBorderThickness(int borderThickness) {
        this.borderThickness = borderThickness;
        repaint();
    }
}
