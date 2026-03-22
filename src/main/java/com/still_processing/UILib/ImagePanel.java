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

    public ImagePanel(String path, int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.path = path;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(path));
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(image, 0, 0, width, height, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
