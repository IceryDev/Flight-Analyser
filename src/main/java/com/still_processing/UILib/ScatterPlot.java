package com.still_processing.UILib;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Graphs.ScatterPlotData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Generate a Scatter plot graph based on a 2D array of float values,
 * where each inner array represents a point (x, y).
 *
 * @author Marco Fontana
 */
public class ScatterPlot extends JPanel implements Runnable {

    private final static int FPS = 60;
    private final static int POINT_SIZE = 10;
    private final static int STD_MARGIN = 45;
    private final static int STD_PADDING = 50;
    private final static int STD_TICK_SEPARATOR = 5;
    private final static int MINIMUM_PIXEL_SPACING = 28;
    private final static int Y_AXIS_LEFT_OFFSET = 280;
    private final static int X_AXIS_VERTICAL_OFFSET = 140;
    private final static int AXIS_STROKE_WIDTH = 5;
    private final static int MIN_AXIS_SIDE_SPACE = 60;
    private final static int HOVER_OFFSET = 10;

    Thread graphThread;
    private float minimumValue;
    private float maximumValue;
    private final ScatterPlotData plotData;
    private final String title;
    private int toolTipIndex = -1;
    private boolean showToolTip = false;

    public ScatterPlot(ScatterPlotData plotData, String title) {
        this.plotData = plotData;
        this.title = title;
        this.minimumValue = Float.MAX_VALUE;
        this.maximumValue = Float.MIN_VALUE;
        this.setPreferredSize(new Dimension(0, getHeight()));
        this.setSize(Integer.MAX_VALUE, getHeight());
        this.setDoubleBuffered(true);
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                hover(event);
            }
        });
        // Finding the minimum and maximum values
        // from the data to determine the axis range
        for (float[] point : plotData.data) {
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
                remainingTime /= 1_000_000.0;
                remainingTime = (remainingTime < 0) ? 0 : remainingTime;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException error) {
                error.printStackTrace();
            }
        }
    }

    public void setTitle(Graphics2D g2d) {
        g2d.setFont(Settings.BOLD_FONT);
        g2d.setColor(Settings.TEXT_COLOR);
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.drawString(title, (getWidth() - metrics.stringWidth(title)) / 2, STD_MARGIN / 2);
    }

    private float getLowerStepScale(float currentStep) {
        if (currentStep <= 0f) {
            return 0f;
        }
        float decade = (float) Math.pow(10, Math.floor(Math.log10(currentStep)));
        float normalizedStep = currentStep / decade;
        if (normalizedStep >= 5f) {
            return 2f * decade;
        }
        if (normalizedStep >= 2f) {
            return decade;
        }
        return 5f * (decade / 10f);
    }

    private float getUpperStepScale(float rawStep) {
        if (rawStep <= 0f) {
            return 1f;
        }
        float decade = (float) Math.pow(10, Math.floor(Math.log10(rawStep)));
        float normalizedStep = rawStep / decade;
        if (normalizedStep <= 1f) {
            return decade;
        }
        if (normalizedStep <= 2f) {
            return 2f * decade;
        }
        if (normalizedStep <= 5f) {
            return 5f * decade;
        }
        return 10f * decade;
    }

    private float getAxisRadius() {
        float maximumAbsoluteValue = Math.max(Math.abs(minimumValue), Math.abs(maximumValue));
        float axisRadius = maximumAbsoluteValue * 1.05f;
        if (axisRadius == 0f) {
            axisRadius = 1f;
        }
        return axisRadius;
    }

    private float getTickStep(float visibleMinimumAxis, float visibleMaximumAxis, int axisPixels, int minimumLabelPixels, float scale) {
        int desiredTickCount = axisPixels / minimumLabelPixels;
        if (desiredTickCount < 10) { desiredTickCount = 10; }
        float rawTickStep = (visibleMaximumAxis - visibleMinimumAxis) / desiredTickCount;
        float tickStep = getUpperStepScale(rawTickStep);
        float previousTickStep = getLowerStepScale(tickStep);
        while (previousTickStep > 0f && previousTickStep * scale >= MINIMUM_PIXEL_SPACING) {
            tickStep = previousTickStep;
            previousTickStep = getLowerStepScale(tickStep);
        }
        return tickStep;
    }

    private float[] getGraphGeometry() {
        int plotWidth = getWidth() - (STD_MARGIN * 2);
        int plotHeight = getHeight() - (STD_MARGIN * 2);
        if (plotWidth <= 0 || plotHeight <= 0) {
            return null;
        }
        int centeredX = STD_MARGIN + (plotWidth / 2) - Y_AXIS_LEFT_OFFSET;
        int centeredY = STD_MARGIN + (plotHeight / 2) + X_AXIS_VERTICAL_OFFSET;
        int minCenterX = STD_PADDING + MIN_AXIS_SIDE_SPACE;
        int maxCenterX = getWidth() - STD_PADDING - MIN_AXIS_SIDE_SPACE;
        int minCenterY = STD_PADDING + MIN_AXIS_SIDE_SPACE;
        int maxCenterY = getHeight() - STD_PADDING - MIN_AXIS_SIDE_SPACE;
        if (minCenterX <= maxCenterX) {
            centeredX = Math.max(minCenterX, Math.min(maxCenterX, centeredX));
        } else {
            centeredX = getWidth() / 2;
        }
        if (minCenterY <= maxCenterY) {
            centeredY = Math.min(maxCenterY, centeredY);
        } else {
            centeredY = getHeight() / 2;
        }
        float availableHalfWidth = Math.max(1f, Math.min(centeredX - STD_PADDING, getWidth() - STD_PADDING - centeredX));
        float availableHalfHeight = Math.max(1f, Math.min(centeredY - STD_PADDING, getHeight() - STD_PADDING - centeredY));
        float scale = Math.min(availableHalfWidth, availableHalfHeight) / getAxisRadius();
        float visibleMinimumAxisX = (STD_PADDING - centeredX) / scale;
        float visibleMaximumAxisX = (getWidth() - STD_PADDING - centeredX) / scale;
        float visibleMinimumAxisY = (centeredY - (getHeight() - STD_PADDING)) / scale;
        float visibleMaximumAxisY = (centeredY - STD_PADDING) / scale;
        return new float[] {
                centeredX, centeredY, scale,
                visibleMinimumAxisX, visibleMaximumAxisX,
                visibleMinimumAxisY, visibleMaximumAxisY
        };
    }

    private void drawAxisTicks(Graphics2D g2d, FontMetrics metrics, int centeredX, int centeredY, float scale, float minAxis, float maxAxis, float tickStep, boolean horizontal) {
        float firstTick = (float) Math.ceil(minAxis / tickStep) * tickStep;
        float lastTick = (float) Math.floor(maxAxis / tickStep) * tickStep;
        for (float value = firstTick; value <= lastTick; value += tickStep) {
            String label = String.valueOf(Math.round(value));
            int labelWidth = metrics.stringWidth(label);
            if (horizontal) {
                int x = centeredX + Math.round(value * scale);
                int labelX = x - (labelWidth / 2);
                int labelY = centeredY + STD_TICK_SEPARATOR + metrics.getAscent() + 2;
                boolean tickVisible = x >= STD_PADDING && x <= getWidth() - STD_PADDING;
                boolean labelVisible = labelX >= 0 && labelX + labelWidth <= getWidth() && labelY <= getHeight() - metrics.getDescent();
                if (tickVisible) {
                    g2d.drawLine(x, centeredY - STD_TICK_SEPARATOR, x, centeredY + STD_TICK_SEPARATOR);
                }
                if (tickVisible && labelVisible) {
                    g2d.drawString(label, labelX, labelY);
                }
            } else {
                int y = centeredY - Math.round(value * scale);
                int labelX = centeredX - STD_TICK_SEPARATOR - 4 - labelWidth;
                int labelY = y + (metrics.getAscent() / 2) - 2;
                boolean tickVisible = y >= STD_PADDING && y <= getHeight() - STD_PADDING;
                boolean labelVisible = labelX >= 0 && labelX + labelWidth <= getWidth()
                        && labelY >= metrics.getAscent() && labelY <= getHeight() - metrics.getDescent();
                if (tickVisible) {
                    g2d.drawLine(centeredX - STD_TICK_SEPARATOR, y, centeredX + STD_TICK_SEPARATOR, y);
                }
                if (tickVisible && labelVisible) {
                    g2d.drawString(label, labelX, labelY);
                }
            }
        }
    }

    private void drawAxisLabels(Graphics2D g2d, FontMetrics metrics, int centeredX, int centeredY) {
        g2d.setColor(Settings.TEXT_COLOR);
        int xLabelWidth = metrics.stringWidth(plotData.axisX);
        int yLabelWidth = metrics.stringWidth(plotData.axisY);
        int xLabelX = getWidth() - STD_PADDING + (STD_TICK_SEPARATOR * 2);
        int xLabelY = centeredY + metrics.getAscent() + STD_TICK_SEPARATOR;
        int yLabelX = centeredX + STD_TICK_SEPARATOR + 6;
        int yLabelY = STD_PADDING - STD_TICK_SEPARATOR;
        xLabelX = Math.max(0, Math.min(getWidth() - xLabelWidth, xLabelX));
        xLabelY = Math.max(metrics.getAscent(), Math.min(getHeight() - metrics.getDescent(), xLabelY));
        yLabelX = Math.max(0, Math.min(getWidth() - yLabelWidth, yLabelX));
        yLabelY = Math.max(metrics.getAscent(), Math.min(getHeight() - metrics.getDescent(), yLabelY));
        g2d.drawString(plotData.axisX, xLabelX, xLabelY);
        g2d.drawString(plotData.axisY, yLabelX, yLabelY);
    }

    private void hover(MouseEvent event) {
        float[] geometry = getGraphGeometry();
        if (geometry == null) {
            showToolTip = false;
            return;
        }
        int centeredX = (int) geometry[0];
        int centeredY = (int) geometry[1];
        float scale = geometry[2];
        int mouseX = event.getX();
        int mouseY = event.getY();
        int hoverRadiusSquared = (POINT_SIZE + 2) * (POINT_SIZE + 2);
        int hoveredIndex = -1;
        for (int pointIndex = 0; pointIndex < plotData.data.length; pointIndex++) {
            int x = centeredX + Math.round(plotData.data[pointIndex][0] * scale);
            int y = centeredY - Math.round(plotData.data[pointIndex][1] * scale);
            int deltaX = mouseX - x;
            int deltaY = mouseY - y;
            if ((deltaX * deltaX) + (deltaY * deltaY) <= hoverRadiusSquared) {
                hoveredIndex = pointIndex;
                break;
            }
        }
        toolTipIndex = hoveredIndex;
        showToolTip = hoveredIndex >= 0;
        repaint();
    }

    private void drawHoverTooltip(Graphics2D g2d, int centeredX, int centeredY, float scale) {
        if (!showToolTip || toolTipIndex < 0 || toolTipIndex >= plotData.data.length) {
            return;
        }
        int x = centeredX + Math.round(plotData.data[toolTipIndex][0] * scale);
        int y = centeredY - Math.round(plotData.data[toolTipIndex][1] * scale);
        String output = String.format("(%.1f, %.1f)", plotData.data[toolTipIndex][0], plotData.data[toolTipIndex][1]);
        FontMetrics metrics = g2d.getFontMetrics();
        int boxWidth = metrics.stringWidth(output) + (POINT_SIZE * 2);
        int boxHeight = metrics.getAscent() + metrics.getDescent() + (POINT_SIZE * 2);
        int boxX = x + HOVER_OFFSET;
        int boxY = y - boxHeight - HOVER_OFFSET;
        if (boxX + boxWidth > getWidth()) {
            boxX = x - boxWidth - HOVER_OFFSET;
        }
        if (boxX < 0) {
            boxX = 0;
        }
        if (boxY < 0) {
            boxY = y + HOVER_OFFSET;
        }
        if (boxY + boxHeight > getHeight()) {
            boxY = Math.max(0, getHeight() - boxHeight);
        }
        g2d.setColor(new Color(0, 0, 0, 99));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);
        g2d.setColor(Settings.BACKGROUND);
        g2d.drawString(output, boxX + POINT_SIZE, boxY + POINT_SIZE + metrics.getAscent());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(Settings.REGULAR_FONT);
        // Calculate centered origin, visible ranges and current scaling
        float[] geometry = getGraphGeometry();
        if (geometry == null) {
            g2d.dispose();
            return;
        }
        int centeredX = (int) geometry[0];
        int centeredY = (int) geometry[1];
        float scale = geometry[2];
        float visibleMinimumAxisX = geometry[3];
        float visibleMaximumAxisX = geometry[4];
        float visibleMinimumAxisY = geometry[5];
        float visibleMaximumAxisY = geometry[6];
        g2d.setStroke(new BasicStroke(AXIS_STROKE_WIDTH));
        g2d.setColor(Settings.TEXT_COLOR);
        // Draw X & Y Axes
        g2d.drawLine(STD_PADDING, centeredY, getWidth() - STD_PADDING, centeredY);
        g2d.drawLine(centeredX, STD_PADDING, centeredX, getHeight() - STD_PADDING);
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.setColor(Settings.TEXT_COLOR);
        // Compute dynamic tick density from available pixels and label sizes
        int axisPixelsX = Math.max(1, getWidth() - (STD_PADDING * 2));
        int axisPixelsY = Math.max(1, getHeight() - (STD_PADDING * 2));
        int maxLabelWidth = Math.max(
                metrics.stringWidth(String.valueOf(Math.round(visibleMinimumAxisX))),
                metrics.stringWidth(String.valueOf(Math.round(visibleMaximumAxisX)))
        );
        int maxLabelHeight = metrics.getAscent() + metrics.getDescent();
        float tickStepX = getTickStep(visibleMinimumAxisX, visibleMaximumAxisX, axisPixelsX, Math.max(18, maxLabelWidth + 6), scale);
        float tickStepY = getTickStep(visibleMinimumAxisY, visibleMaximumAxisY, axisPixelsY, Math.max(16, maxLabelHeight + 4), scale);
        drawAxisTicks(g2d, metrics, centeredX, centeredY, scale, visibleMinimumAxisX, visibleMaximumAxisX, tickStepX, true);
        drawAxisTicks(g2d, metrics, centeredX, centeredY, scale, visibleMinimumAxisY, visibleMaximumAxisY, tickStepY, false);
        // Draw all scatter points using the same coordinate transformation
        g2d.setColor(Settings.HIGHLIGHT);
        for (float[] point : plotData.data) {
            int x = centeredX + Math.round(point[0] * scale);
            int y = centeredY - Math.round(point[1] * scale);
            g2d.fillOval(x - (POINT_SIZE / 2), y - (POINT_SIZE / 2), POINT_SIZE, POINT_SIZE);
        }
        // Draw hover tooltip and overlay texts last for readability
        drawHoverTooltip(g2d, centeredX, centeredY, scale);
        drawAxisLabels(g2d, metrics, centeredX, centeredY);
        setTitle(g2d);
        g2d.dispose();
    }
}
