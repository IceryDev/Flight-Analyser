package com.still_processing.UILib;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import com.still_processing.DefaultSettings.Settings;

public class Histogram extends JPanel implements Runnable {
    Thread graphThread;
    private final int FPS = 60;
    private float[] data = null;
    private int[] dataHeights;
    private int xStep = 5;
    private int yStep = 5;

    private int padding = 300;
    private int height = 1440;
    private float renderPercentage = 0;

    public Histogram(float[] data) {
        if (data != null) {
            this.data = data;

            float max = data[0];
            float min = data[0];
            for (float dataPoint : data) {
                max = (max > dataPoint) ? max : dataPoint;
                min = (min < dataPoint) ? min : dataPoint;
            }

            min = (min > 0) ? 0 : min;
            dataHeights = new int[(int) (max - min) / xStep + 1];

            for (float dataPoint : data) {
                int interval = (int) dataPoint / xStep;
                int index = (int) (interval - min / xStep);
                dataHeights[index]++;
            }
        }

        this.setPreferredSize(new Dimension(0, height));
        this.setSize(Integer.MAX_VALUE, height);
        this.setOpaque(false);
        this.setDoubleBuffered(true);
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (renderPercentage < 1) {
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

    public void animate() {
        graphThread = new Thread(this);
        graphThread.start();
    }

    private void update() {
        renderPercentage += (renderPercentage < 1) ? 0.02 : 0;
        renderPercentage = (renderPercentage > 1) ? 1 : renderPercentage;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (data != null) {
            int height = getHeight();

            int max = dataHeights[0];
            int min = dataHeights[0];
            for (int dataPoint : dataHeights) {
                max = (max > dataPoint) ? max : dataPoint;
                min = (min < dataPoint) ? min : dataPoint;
            }

            for (int dataIndex = 0; dataIndex < dataHeights.length; dataIndex++) {
                int barWidth = (int) (getWidth() - 2 * padding) / dataHeights.length;
                int xPos = (int) barWidth * (dataIndex);

                int maxValue = (max / yStep + 1) * yStep;
                int maxHeight = -2 * padding + height;
                int yValue = (int) (maxHeight * dataHeights[dataIndex] / maxValue);
                int barHeight = (int) (yValue * renderPercentage);
                int barTop = height - padding - barHeight;

                g2d.setColor(Settings.HIGHLIGHT);
                g2d.fillRect(padding + xPos, barTop, barWidth, barHeight);

                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Settings.TEXT_COLOR);
                g2d.drawRect(padding + xPos, barTop, barWidth, barHeight);
            }

            drawLegend(g2d);

        }

        g2d.dispose();
    }

    private void drawLegend(Graphics2D g2d) {
        if (data != null) {
            int height = getHeight();
            int max = dataHeights[0];
            int min = dataHeights[0];
            for (int dataPoint : dataHeights) {
                max = (max > dataPoint) ? max : dataPoint;
                min = (min < dataPoint) ? min : dataPoint;
            }

            int barWidth = (int) (getWidth() - 2 * padding) / dataHeights.length;
            g2d.setStroke(new BasicStroke(5));
            g2d.setColor(Settings.TEXT_COLOR);

            // X Legend Line
            g2d.setFont(Settings.BOLD_FONT.deriveFont(36f));
            g2d.drawString("X axis", getWidth() / 2, height - padding / 2);

            int xEndPos = barWidth * dataHeights.length;
            g2d.drawLine(padding, height - padding, xEndPos + padding, height - padding);

            for (int dataIndex = 0; dataIndex <= dataHeights.length; dataIndex++) {
                int xPos = (int) barWidth * (dataIndex);

                g2d.setColor(Settings.TEXT_COLOR);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawLine(padding + xPos, height - padding, padding + xPos, height -
                        padding + 10);

                g2d.setFont(Settings.BOLD_FONT.deriveFont(24f));
                String label = String.format("%d", dataIndex * xStep);
                g2d.drawString(label, xPos + padding, height - padding + 36);
            }

            // Y Legend Line
            AffineTransform original = g2d.getTransform();
            g2d.rotate(Math.toRadians(-90));
            g2d.setFont(Settings.BOLD_FONT.deriveFont(36f));
            g2d.drawString("Y axis", -getHeight() / 2, padding / 2);
            g2d.setTransform(original);

            int maxYIndex = (max + yStep) / yStep;
            int yIntervalHeight = (int) (height - 2 * padding) / ((max + yStep) / yStep);
            g2d.drawLine(padding, height - padding - yIntervalHeight * maxYIndex, padding, height - padding);
            for (int yLegendIndex = 0; yLegendIndex <= maxYIndex; yLegendIndex++) {
                int yPos = height - 2 * padding - yIntervalHeight * yLegendIndex;

                g2d.setFont(Settings.BOLD_FONT.deriveFont(24f));
                String label = String.format("%02d", yStep * yLegendIndex);
                g2d.drawString(label, padding - 60, yPos + padding);

                g2d.setColor(Settings.TEXT_COLOR);
                g2d.setStroke(new BasicStroke(5));
                g2d.drawLine(padding, padding + yPos, padding - 10, padding + yPos);
            }

        }
    }
}
