package com.still_processing.UILib;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    private String path = "";
    private int width = 100;
    private int height = 100;
    private int x = 0;
    private int y = 0;

    public ImagePanel(String path, int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public ImagePanel(String path, int width, int height, int x, int y) {
        this(path, width, height);
        this.x = x;
        this.y = y;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(path));
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(image, x, y, width, height, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
