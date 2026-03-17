package com.still_processing.UILib;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.still_processing.DefaultSettings.Settings;

public class GraphTemplate extends JPanel implements Runnable {
    Thread graphThread;
    private final int FPS = 60;
    private int height;

    // Define your own types
//    private final float[] data;

    public GraphTemplate() {
        this.setPreferredSize(new Dimension(0, height));
        this.setSize(Integer.MAX_VALUE, height);
        this.setDoubleBuffered(true);
    }

    // Don't change this
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (graphThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1_000_000.0; // Converts to milliseconds
                remainingTime = (remainingTime < 0) ? 0 : remainingTime;

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException error) {
                error.printStackTrace();
            }

        }
    }

    // This initialised the Graph - don't change
    public void animate() {
        graphThread = new Thread(this);
        graphThread.start();
    }

    // Update variables
    private void update() {
    }

    // actually draw method
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int height = getHeight();

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.dispose();
    }
}
