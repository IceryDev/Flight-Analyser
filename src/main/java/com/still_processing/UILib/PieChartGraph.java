package com.still_processing.UILib;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.awt.Color;
import javax.swing.JFrame;

import javax.swing.JPanel;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class PieChartGraph extends JPanel implements Runnable {
    Thread graphThread;
    private final int FPS = 60;
    private int height;

    public HashMap<String, Integer> getData() {
        HashMap<String, Integer> data = new HashMap<>();
        data.put("RyanAir", 3);
        data.put("AerLingus", 5);
        data.put("Scoot", 3);
        data.put("Other Airlines", 3);
        return data;
    }

    public PieChartGraph() {
        this.setPreferredSize(new Dimension(0, height));
        this.setSize(Integer.MAX_VALUE, height);
        this.setDoubleBuffered(true);


        setLayout(new BorderLayout(0, 0));
        setSize(1000, 600);
        setMinimumSize(new Dimension(750, 500));
        setVisible(true);
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
