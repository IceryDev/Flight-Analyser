package com.still_processing.UILib;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.still_processing.DefaultSettings.Settings.*;

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private int radius;
    private Color hoverColor;
    private boolean isHovered = false;

    public RoundedButton(String text, int radius, Color backgroundColor, Color hoverColor){
        super(text);
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        this.hoverColor = hoverColor;

        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);

        addMouseListener(new MouseAdapter() {
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
        g2d.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean contains(int x, int y) {
        return new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius).contains(x, y);
    }


}
