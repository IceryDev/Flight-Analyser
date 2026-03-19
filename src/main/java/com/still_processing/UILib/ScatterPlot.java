package com.still_processing.UILib;

import javax.swing.*;
import java.awt.*;

public class ScatterPlot extends JPanel implements Runnable {

    private final static int FPS = 60;
    private final static int POINT_SIZE = 8;
    private final static int STD_MARGIN = 0;
    private final static int STD_TICK_SEPARATOR = 5;
    private final static int MINIMUM_PIXEL_SPACING = 20;

    Thread graphThread;
    private float minimumValue;
    private float maximumValue;
    private final float[][] data;

    public ScatterPlot(float[][] data) {
        this.data = data;
        this.minimumValue = Float.MAX_VALUE;
        this.maximumValue = Float.MIN_VALUE;
        this.setPreferredSize(new Dimension(0, getHeight()));
        this.setSize(Integer.MAX_VALUE, getHeight());
        this.setDoubleBuffered(true);
        // Finding the minimum and maximum values
        // from the data to determine the axis range
        for (float[] point : data) {
            for (float value : point) {
                minimumValue = Math.min(value, minimumValue);
                maximumValue = Math.max(value, maximumValue);
            }
        }
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (graphThread != null) {
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

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int plotWidth = getWidth() - (STD_MARGIN * 2);
        int plotHeight = getHeight() - (STD_MARGIN * 2);
        int centeredX = STD_MARGIN + (plotWidth / 2);
        int centeredY = STD_MARGIN + (plotHeight / 2);
        // Making all four quadrants visible
        float maximumAbsoluteValue = Math.max(Math.abs(minimumValue), Math.abs(maximumValue));
        float axisRadius = maximumAbsoluteValue * 1.05f;
        if (axisRadius == 0f) {
            axisRadius = 1f;
        }
        float minimumAxis = -axisRadius;
        float maximumAxis = axisRadius;
        float range = maximumAxis - minimumAxis;
        float scaleX = (float) plotWidth / range;
        float scaleY = (float) plotHeight / range;
        g2d.setColor(Color.RED);
        g2d.drawLine(0, centeredY, getWidth(), centeredY);     // X-axis
        g2d.drawLine(centeredX, 0, centeredX, getHeight());    // Y-axis
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.setColor(Color.GRAY);
        float tickStep = 1f;
        while (tickStep * scaleX < MINIMUM_PIXEL_SPACING) {
            tickStep *= 2f;
        }
        // Drawing ticks and labels (for axes X and Y)
        float firstTick = (float) ((Math.ceil(minimumAxis / tickStep) * tickStep) + tickStep);
        for (float value = firstTick; value < maximumAxis; value += tickStep) {
            String label = String.valueOf(Math.round(value));
            int labelWidth = metrics.stringWidth(label);
            int x = centeredX + Math.round(value * scaleX);
            int horizontalLabelX = x - (labelWidth / 2);
            int horizontalLabelY = centeredY + STD_TICK_SEPARATOR + metrics.getAscent() + 2;
            g2d.drawLine(x, centeredY - STD_TICK_SEPARATOR, x, centeredY + STD_TICK_SEPARATOR);
            g2d.drawString(label, horizontalLabelX, horizontalLabelY);
            int y = centeredY - Math.round(value * scaleY);
            int verticalLabelX = centeredX - STD_TICK_SEPARATOR - 4 - labelWidth;
            int verticalLabelY = y + (metrics.getAscent() / 2) - 2;
            g2d.drawLine(centeredX - STD_TICK_SEPARATOR, y, centeredX + STD_TICK_SEPARATOR, y);
            g2d.drawString(label, verticalLabelX, verticalLabelY);
        }
        // Drawing points on the panel using the same scales
        g2d.setColor(Color.BLUE);
        for (float[] point : data) {
            int x = centeredX + Math.round(point[0] * scaleX);
            int y = centeredY - Math.round(point[1] * scaleY);
            int parsedX = x - (POINT_SIZE / 2);
            int parsedY = y - (POINT_SIZE / 2);
            g2d.fillOval(parsedX, parsedY, POINT_SIZE, POINT_SIZE);
        }
        g2d.dispose();
    }
}