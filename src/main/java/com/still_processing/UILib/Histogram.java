package com.still_processing.UILib;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * Create the histogram (subclass of JPanel)
 * 
 * @author Zhou Sun
 * @author Jessica Chen
 */
public class Histogram extends JPanel implements Runnable, Graph {
    // Variable parameters to parse data
    private float[] data = null;
    private int[] barValues = null;
    private int xStep = 5;
    private int yStep = 5;
    private int barMaxValue = 0;

    // Variables to animate the graph and hover effect
    Thread graphThread;
    private final int FPS = 60;
    private int toolTipIndex = 0;
    private boolean showToolTip = false;
    private float renderPercentage = 0;

    // Determines the look and feel
    private Color barColor = HIGHLIGHT;
    private Color barOutline = TEXT_COLOR;
    private Color fontColor = TEXT_COLOR;
    private Font legendFont = REGULAR_FONT;
    private Font labelFont = REGULAR_FONT;
    private float legendFontSize = 12;
    private float labelFontSize = 12;
    private int fontWeight = 5;
    private int barOutlineWidth = 2;
    private int padding = 100;
    private String xLengendText = "X axis";
    private String yLengendText = "Y axis";
    private int toolTipPadding = 10;
    private float toolTipFontSize = 12;

    /**
     * Create the histogram (subclass of JPanel)
     *
     * @param data  array of float with the data to graph
     * @param xStep sets the increment in x axis
     * @param yStep sets the increment in y axis
     *
     * @author Zhou Sun
     */
    public Histogram(float[] data, int xStep, int yStep) {
        if (data != null) {
            if (data.length != 0) {
                this.data = data;
                this.xStep = xStep;
                this.yStep = yStep;

                loadBarValues();

                // Add hover event the histogram
                this.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent event) {
                        hover(event);
                    }
                });
            }
        }

        this.setPreferredSize(new Dimension(0, 1440));
        this.setOpaque(false);
        this.setDoubleBuffered(true);
    }

    private void loadBarValues() {
        // Getting the max and min value from the dataset
        float max = data[0];
        float min = data[0];
        for (float dataPoint : data) {
            max = (max > dataPoint) ? max : dataPoint;
            min = (min < dataPoint) ? min : dataPoint;
        }
        min = (min > 0) ? 0 : min;

        // Loading data to each bars
        barValues = new int[(int) (max - min) / xStep + 1];
        for (float dataPoint : data) {
            int interval = (int) dataPoint / xStep;
            int index = (int) (interval - min / xStep);
            barValues[index]++;
            barMaxValue = (barMaxValue > barValues[index]) ? barMaxValue : barValues[index];
        }
    }

    /**
     * runs ensure the update and repaint cycle occurs at 60 fps
     * cycle ends when the update is done i.e. renderPencentage hits 100%
     *
     * @author Zhou Sun
     */
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (graphThread != null && renderPercentage < 1) {
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

    /**
     * Spawn a new graphThread and animates the graph
     *
     * @author Zhou Sun
     */
    @Override
    public void animate() {
        renderPercentage = 0;
        graphThread = new Thread(this);
        graphThread.start();
    }

    @Override
    public void draw() {
    }

    /**
     * increments the renderPercentage (asynchronously)
     *
     * @author Zhou Sun
     */
    private void update() {
        renderPercentage += (renderPercentage < 1) ? 0.02 : 0;
        renderPercentage = (renderPercentage > 1) ? 1 : renderPercentage;
    }

    /**
     * Draws the graph with respect to the member variables
     *
     * @author Zhou Sun
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (barValues != null && barValues.length != 0) {
            int height = getHeight();

            // Note floor div, don't simplify the equation naiively
            int maxValue = (barMaxValue / yStep + 1) * yStep;
            int maxHeight = -2 * padding + height;
            int barWidth = (getWidth() - 2 * padding) / barValues.length;

            for (int dataIndex = 0; dataIndex < barValues.length; dataIndex++) {
                int xPos = barWidth * dataIndex + padding;
                int yValue = maxHeight * barValues[dataIndex] / maxValue;
                int barHeight = (int) (yValue * renderPercentage);
                int barTop = height - padding - barHeight;

                g2d.setColor(barColor);
                g2d.fillRect(xPos, barTop, barWidth, barHeight);

                g2d.setStroke(new BasicStroke(barOutlineWidth));
                g2d.setColor(barOutline);
                g2d.drawRect(xPos, barTop, barWidth, barHeight);
            }
            drawLegend(g2d);

            if (showToolTip) {
                drawHoverTooltip(g2d);
                showToolTip = false;
            }
        }
        if (barValues == null || barValues.length == 0) {
            try {
                BufferedImage image = ImageIO.read(getClass().getResource("/Images/error-message.png"));
                g2d.drawImage(image, getWidth() / 2 - 300, getHeight() / 2 - 300, 600, 600, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        g2d.dispose();
    }

    /**
     * Draws the legends with respect to the member variables
     *
     * @author Zhou Sun
     */
    private void drawLegend(Graphics2D g2d) {
        if (barValues != null) {
            int height = getHeight();
            int barWidth = (getWidth() - 2 * padding) / barValues.length;
            g2d.setStroke(new BasicStroke(fontWeight));
            g2d.setColor(fontColor);

            FontMetrics metrics = getFontMetrics(labelFont.deriveFont(labelFontSize));
            g2d.setFont(labelFont.deriveFont(labelFontSize));

            // X Legend Line
            g2d.setFont(legendFont.deriveFont(legendFontSize));
            g2d.drawString(xLengendText, getWidth() / 2, height - padding / 2);
            g2d.drawLine(padding, height - padding, barWidth * barValues.length + padding, height - padding);

            for (int dataIndex = 0; dataIndex <= barValues.length; dataIndex++) {
                int xPos = barWidth * dataIndex + padding;
                g2d.drawLine(xPos, height - padding, xPos, height - padding + 10);
                String label = String.format("%d", dataIndex * xStep);
                g2d.drawString(label, xPos, height - padding + metrics.getHeight() + 10);
            }

            // Y Legend Line
            AffineTransform original = g2d.getTransform();
            g2d.rotate(Math.toRadians(-90));
            g2d.setFont(legendFont.deriveFont(legendFontSize));
            g2d.drawString(yLengendText, -getHeight() / 2, padding / 4);
            g2d.setTransform(original);

            int maxYIndex = barMaxValue / yStep + 1;
            int yIntervalHeight = (height - 2 * padding) / maxYIndex;
            g2d.drawLine(padding, height - padding, padding, height - padding - yIntervalHeight * maxYIndex);
            for (int yLegendIndex = 0; yLegendIndex <= maxYIndex; yLegendIndex++) {
                int yPos = height - padding - yIntervalHeight * yLegendIndex;
                String label = String.format("%d", yStep * yLegendIndex);
                g2d.drawString(label, padding - metrics.stringWidth(label) - 20, yPos);
                g2d.setStroke(new BasicStroke(fontWeight));
                g2d.drawLine(padding, yPos, padding - 10, yPos);
            }

        }
    }

    /**
     * Mouse hover eventlisterner, calles the drawHoverTooltip
     *
     * @author Zhou Sun
     */
    private void hover(MouseEvent event) {
        int height = getHeight();
        int mouseX = event.getX();
        int mouseY = event.getY();
        int barWidth = (int) (getWidth() - 2 * padding) / barValues.length;

        for (int barIndex = 0; barIndex < barValues.length; barIndex++) {
            int maxValue = barMaxValue + 1;
            int maxHeight = -2 * padding + height;
            int yValue = maxHeight * barValues[barIndex] / maxValue;
            int barHeight = (int) (yValue * renderPercentage);
            int barTop = height - padding - barHeight;
            int xPos = barWidth * barIndex + padding;

            if (mouseX > xPos && mouseX < xPos + barWidth && mouseY < height - padding && mouseY > barTop) {
                showToolTip = true;
                toolTipIndex = barIndex;
            }
        }
        repaint();
    }

    /**
     * Draw the hover tooltip
     *
     * @author Zhou Sun
     */
    private void drawHoverTooltip(Graphics2D g2d) {
        if (toolTipIndex < barValues.length) {
            int height = getHeight();
            int barWidth = (getWidth() - 2 * padding) / barValues.length;
            int maxValue = barMaxValue + 1;
            int maxHeight = -2 * padding + height;
            int yValue = maxHeight * barValues[toolTipIndex] / maxValue;
            int barHeight = (int) (yValue * renderPercentage);
            int barTop = height - padding - barHeight;
            int toolTipx = barWidth * toolTipIndex + padding + barWidth / 2;
            int toolTipY = barTop + barHeight / 2;
            int showValue = barValues[toolTipIndex];
            String output = String.format("%d", showValue);
            FontMetrics metrics = getFontMetrics(labelFont.deriveFont(labelFontSize));
            int toolTipWidth = metrics.stringWidth(output) + 2 * toolTipPadding;
            int toolTipHeight = metrics.getHeight() + 2 * toolTipPadding;

            g2d.setFont(labelFont.deriveFont(toolTipFontSize));
            g2d.setColor(new Color(0, 0, 0, 99));
            g2d.fillRoundRect(toolTipx - toolTipPadding, toolTipY - toolTipPadding - metrics.getHeight(),
                    toolTipWidth, toolTipHeight, 20, 20);

            g2d.setColor(BACKGROUND);
            g2d.setStroke(new BasicStroke(5));
            g2d.drawString(output, toolTipx, toolTipY);
        }
    }

    public void setXStep(int xStep) {
        if (xStep > 0 && data != null) {
            this.xStep = xStep;
            loadBarValues();
        }
    }

    public void setYStep(int yStep) {
        if (yStep > 0)
            this.yStep = yStep;
    }

    public void setBarColor(Color barColor) {
        if (barColor != null)
            this.barColor = barColor;
    }

    public void setBarOutline(Color barOutline) {
        if (barOutline != null)
            this.barOutline = barOutline;
    }

    public void setFontColor(Color fontColor) {
        if (fontColor != null)
            this.fontColor = fontColor;
    }

    public void setLegendFont(Font legendFont) {
        if (legendFont != null)
            this.legendFont = legendFont;
    }

    public void setLabelFont(Font labelFont) {
        if (labelFont != null)
            this.labelFont = labelFont;
    }

    public void setLegendFontSize(float legendFontSize) {
        if (legendFontSize > 0)
            this.legendFontSize = legendFontSize;
    }

    public void setLabelFontSize(float labelFontSize) {
        if (labelFontSize > 0)
            this.labelFontSize = labelFontSize;
    }

    public void setFontWeight(int fontWeight) {
        if (fontWeight > 0)
            this.fontWeight = fontWeight;
    }

    public void setBarOutlineWidth(int barOutlineWidth) {
        if (barOutlineWidth > 0)
            this.barOutlineWidth = barOutlineWidth;
    }

    public void setPadding(int padding) {
        if (padding > 0)
            this.padding = padding;
    }

    public void setXLengendText(String xLengendText) {
        if (xLengendText != null)
            this.xLengendText = xLengendText;
    }

    public void setYLengendText(String yLengendText) {
        if (yLengendText != null)
            this.yLengendText = yLengendText;
    }

    public void setToolTipPadding(int toolTipPadding) {
        if (toolTipPadding > 0)
            this.toolTipPadding = toolTipPadding;
    }

    public void setToolTipFontSize(float toolTipFontSize) {
        if (toolTipFontSize > 0)
            this.toolTipFontSize = toolTipFontSize;
    }
}
