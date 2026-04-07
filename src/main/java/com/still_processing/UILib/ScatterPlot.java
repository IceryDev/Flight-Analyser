package com.still_processing.UILib;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Graphs.ScatterPlotData;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;

/**
 * Generate a Scatter plot graph based on a 2D array of float values,
 * where each inner array represents a point (x, y).
 *
 * @author Marco Fontana
 */
public class ScatterPlot extends JPanel implements Runnable {

    private final static int FPS = 60;
    private final static int POINT_SIZE = 10;
    private final static int STD_MARGIN = 65;
    private final static int STD_PADDING = 100;
    private final static int STD_TICK_SEPARATOR = 5;
    private final static int HOVER_OFFSET = 10;
    private final static double XY_PADDING_FRACTION = 0.05;
    private final static int MAX_RENDER_POINTS = 100;
    private final static float LERP_PERCENTAGE = 0.35f;

    private final ScatterPlotData plotData;
    private final float[][] sampledData;
    private final String title;

    Thread graphThread;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private int toolTipIndex = -1;
    private boolean showToolTip = false;

    public ScatterPlot(ScatterPlotData plotData, String title) {
        this.plotData = plotData;
        this.sampledData = sampleData(plotData.data);
        this.title = title;
        this.minX = Double.POSITIVE_INFINITY;
        this.maxX = Double.NEGATIVE_INFINITY;
        this.minY = Double.POSITIVE_INFINITY;
        this.maxY = Double.NEGATIVE_INFINITY;
        this.setPreferredSize(new Dimension(0, getHeight()));
        this.setSize(Integer.MAX_VALUE, getHeight());
        this.setDoubleBuffered(true);
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                hover(event);
            }
        });
        // Find per-axis bounds (double precision) to avoid float scale goes
        // wrong when values are large but the range is small
        for (float[] point : sampledData) {
            if (point == null || point.length < 2) {
                continue;
            }
            double x = point[0];
            double y = point[1];
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
    }

    /**
     * Computes a clamped value staying within the specified range [lo, hi]
     *
     * @param v          The value to clamp
     * @param lowerBound The lower bound of the range
     * @param upperBound The upper bound of the range
     * @return The clamped value, included to be between lowerBound and upperBound
     */
    private static double clamp(double v, double lowerBound, double upperBound) {
        return Math.max(lowerBound, Math.min(upperBound, v));
    }

    /**
     * Moves the distance from {@code a} toward {@code b} by {@code LERP_PERCENTAGE} of the way.
     *
     * @param a The starting value
     * @param b The target value
     * @return The interpolated value between {@code a} and {@code b}
     */
    private static double lerp(double a, double b) {
        return a + ((b - a) * LERP_PERCENTAGE);
    }

    private static String formatTickLabel(double value, double step) {
        double absStep = Math.abs(step);
        double absValue = Math.abs(value);
        if (absValue >= 1e6 || absStep >= 1e5) {
            return String.format("%.3g", value);
        }
        if (absStep >= 1) {
            return String.format("%.0f", value);
        }
        int decimals = (int) Math.ceil(-Math.log10(absStep));
        decimals = (int) clamp(decimals, 0, 6);
        return String.format("%." + decimals + "f", value);
    }

    public static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text) {
        g2d.translate((float) x, (float) y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text, 0, 0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float) x, -(float) y);
    }

    private float[][] sampleData(float[][] data) {
        if (data.length <= MAX_RENDER_POINTS) {
            return data;
        }
        // Getting a random sample of points to render by
        // shuffling a copy of the original array and taking the first N points.
        List<float[]> points = new ArrayList<>(data.length);
        Collections.addAll(points, data);
        Collections.shuffle(points);
        float[][] sampled = new float[MAX_RENDER_POINTS][];
        for (int index = 0; index < MAX_RENDER_POINTS; index++) {
            sampled[index] = points.get(index);
        }
        return sampled;
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

    /**
     * Rounds a current step down to a "nice" tick step (It will be scaled by [1/2/5])
     *
     * @param currentStep The current step to round down
     * @return The next lower "nice" tick step
     */
    private double getLowerStepScale(double currentStep) {
        if (currentStep <= 0) {
            return 0;
        }
        // Finding the closest lower step by normalizing the current step to a value between 1 and 10
        double decade = Math.pow(10, Math.floor(Math.log10(currentStep)));
        double normalizedStep = currentStep / decade;
        if (normalizedStep >= 5) {
            return 2 * decade;
        }
        if (normalizedStep >= 2) {
            return decade;
        }
        return 5 * (decade / 10);
    }

    /**
     * Rounds a raw step up to a "nice" tick step (It will be scaled by [1/2/5])
     *
     * @param rawStep The raw step calculated from the visible range and desired tick count
     * @return The next higher "nice" tick step
     */
    private double getUpperStepScale(double rawStep) {
        if (rawStep <= 0) {
            return 1;
        }
        // Finding the closest higher step by normalizing the raw step to a value between 1 and 10
        double decade = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double normalizedStep = rawStep / decade;
        if (normalizedStep <= 1) {
            return decade;
        }
        if (normalizedStep <= 2) {
            return 2 * decade;
        }
        if (normalizedStep <= 5) {
            return 5 * decade;
        }
        return 10 * decade;
    }

    private double computeNegativeWidthFraction(double dataMinX, double dataMaxX) {
        if (!(dataMinX < 0 && dataMaxX > 0)) {
            return 0;
        }
        int pos = 0;
        int neg = 0;
        for (float[] point : sampledData) {
            if (point == null || point.length < 2) {
                continue;
            }
            if (point[0] >= 0) {
                pos++;
            } else {
                neg++;
            }
        }
        int total = pos + neg;
        if (total <= 0) {
            return 0.25;
        }
        double posShare = (double) pos / (double) total;
        // Smooth it a bit so it doesn't swing too hard.
        double smoothed = lerp((1 - posShare), (Math.abs(dataMinX) / (Math.abs(dataMinX) + dataMaxX)));
        return clamp(smoothed, 0.15, 0.45);
    }

    /**
     * Picks a readable tick gap for the current axis size.
     *
     * @param visibleMinimumAxis The current visible minimum value on the axis
     * @param visibleMaximumAxis The current visible maximum value on the axis
     * @param axisPixels         The number of pixels available for the axis
     * @param minimumLabelPixels The minimum number of pixels needed to fit a label without overlap
     * @param scale              The current scale of the axis, used to adjust tick steps for better readability
     * @return A scaled "nice" tick step
     */
    private double getTickStep(double visibleMinimumAxis, double visibleMaximumAxis, int axisPixels,
                               int minimumLabelPixels, double scale) {
        int desiredTickCount = axisPixels / minimumLabelPixels;
        if (desiredTickCount < 10) {
            desiredTickCount = 10;
        }
        double rawTickStep = (visibleMaximumAxis - visibleMinimumAxis) / desiredTickCount;
        double tickStep = getUpperStepScale(rawTickStep);
        double previousTickStep = getLowerStepScale(tickStep);
        while (previousTickStep > 0 && previousTickStep * scale >= 28) {
            tickStep = previousTickStep;
            previousTickStep = getLowerStepScale(tickStep);
        }
        return tickStep;
    }

    /**
     * Calculates graph bounds, scales, and axis positions used for drawing.
     *
     * @return An array containing all the necessary geometry values for drawing the graph,
     *         or null if the plot area is too small to draw.
     */
    private double[] getGraphGeometry() {
        int plotWidth = getWidth() - (STD_MARGIN * 2);
        int plotHeight = getHeight() - (STD_MARGIN * 2);
        if (plotWidth <= 0 || plotHeight <= 0) {
            return null;
        }
        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        if (rangeX <= 0) {
            rangeX = 2;
        }
        if (rangeY <= 0) {
            rangeY = 2;
        }
        double padX = rangeX * XY_PADDING_FRACTION;
        double padY = rangeY * XY_PADDING_FRACTION;
        double dataMinX = minX - padX;
        double dataMaxX = maxX + padX;
        double dataMinY = minY - padY;
        double dataMaxY = maxY + padY;
        int innerW = Math.max(1, getWidth() - (STD_PADDING * 2));
        int innerH = Math.max(1, getHeight() - (STD_PADDING * 2));
        double yScale = innerH / (dataMaxY - dataMinY);
        if (!Double.isFinite(yScale) || yScale <= 0) {
            yScale = 1;
        }
        double offsetY = STD_PADDING;
        double negWidthFrac = computeNegativeWidthFraction(dataMinX, dataMaxX);
        double negW = (dataMinX < 0 && dataMaxX > 0) ? (innerW * negWidthFrac) : 0;
        double posW = innerW - negW;
        // Keeping some space for positive values
        if (posW < innerW * 0.35) {
            posW = innerW * 0.35;
            negW = innerW - posW;
        }
        double offsetX = STD_PADDING;
        double xScaleNeg;
        double xScalePos;
        // If the data includes both negative and positive values, we need to split the X axis in two parts
        // with different scales. Otherwise, a single scale will be used for the whole axis.
        if (dataMinX < 0 && dataMaxX > 0) {
            xScaleNeg = negW / (0 - dataMinX);
            xScalePos = posW / (dataMaxX - 0);
        } else if (dataMaxX <= 0) {
            xScaleNeg = innerW / (dataMaxX - dataMinX);
            xScalePos = xScaleNeg;
            negW = innerW;
        } else {
            xScalePos = innerW / (dataMaxX - dataMinX);
            xScaleNeg = xScalePos;
            negW = 0;
        }
        if (!Double.isFinite(xScaleNeg) || xScaleNeg <= 0) {
            xScaleNeg = 1;
        }
        if (!Double.isFinite(xScalePos) || xScalePos <= 0) {
            xScalePos = 1;
        }
        // Axis lines: draw through 0 when visible, otherwise pin to plot edge.
        double axisXValue = (0 >= dataMinX && 0 <= dataMaxX) ? 0 : dataMinX;
        double axisYValue = (0 >= dataMinY && 0 <= dataMaxY) ? 0 : dataMinY;
        double zeroXPx;
        if (dataMinX < 0 && dataMaxX > 0) {
            zeroXPx = offsetX + negW;
        } else if (dataMaxX <= 0) {
            zeroXPx = offsetX + (0 - dataMinX) * xScaleNeg;
        } else {
            zeroXPx = offsetX + (0 - dataMinX) * xScalePos;
        }
        double axisXPx = (axisXValue == 0) ? zeroXPx : offsetX;
        axisXPx = clamp(axisXPx, STD_PADDING, getWidth() - STD_PADDING);
        double axisYPx = (getHeight() - offsetY) - (axisYValue - dataMinY) * yScale;
        axisYPx = clamp(axisYPx, STD_PADDING, getHeight() - STD_PADDING);
        return new double[]{dataMinX, dataMaxX, dataMinY, dataMaxY, yScale, offsetX, offsetY, axisXPx, axisYPx, negW,
                xScaleNeg, xScalePos, zeroXPx};
    }

    /**
     * Converts one X data value to a position matching screen coordinates.
     *
     * @param x         The X data value to convert
     * @param dataMinX  The minimum X data value in the current view,
     * @param negW      The width in pixels of the negative X area (for split scaling)
     * @param xScaleNeg The scale factor for negative X values
     * @param xScalePos The scale factor for positive X values
     * @param zeroXPx   The pixel position of the zero X value
     * @param offsetX   The pixel offset for the left edge of the plot area, used to shift all points
     * @return The pixel X coordinate corresponding to the given data X value, accounting for split scaling if needed.
     *
     */
    private double mapX(double x, double dataMinX, double negW, double xScaleNeg, double xScalePos, double zeroXPx,
                        double offsetX) {
        if (dataMinX < 0 && negW > 0) {
            if (x < 0) {
                return offsetX + (x - dataMinX) * xScaleNeg;
            }
            return zeroXPx + x * xScalePos;
        }
        return offsetX + (x - dataMinX) * xScaleNeg;
    }

    private void drawXTicks(Graphics2D g2d, FontMetrics metrics, double dataMinX, double dataMaxX, double offsetX,
                            double axisYPx, double negW, double xScaleNeg, double xScalePos, double zeroXPx) {
        int axisY = (int) Math.round(axisYPx);
        int axisPixelsX = Math.max(1, getWidth() - (STD_PADDING * 2));
        int maxLabelWidth = Math.max(metrics.stringWidth(formatTickLabel(dataMinX, 1)),
                metrics.stringWidth(formatTickLabel(dataMaxX, 1)));
        int minLabelPixels = Math.max(18, maxLabelWidth + 6);
        boolean split = (dataMinX < 0 && dataMaxX > 0 && negW > 0);
        if (!split) {
            double scale = (xScalePos > 0) ? xScalePos : xScaleNeg;
            double tickStep = getTickStep(dataMinX, dataMaxX, axisPixelsX, minLabelPixels, scale);
            double firstTick = Math.ceil(dataMinX / tickStep) * tickStep;
            double lastTick = Math.floor(dataMaxX / tickStep) * tickStep;
            for (double value = firstTick; value <= lastTick + (tickStep * 0.5); value += tickStep) {
                int x = (int) Math.round(mapX(value, dataMinX, 0, xScaleNeg, xScalePos, zeroXPx, offsetX));
                if (x < STD_PADDING || x > getWidth() - STD_PADDING) {
                    continue;
                }
                String label = formatTickLabel(value, tickStep);
                int labelWidth = metrics.stringWidth(label);
                int labelX = x - (labelWidth / 2);
                int labelY = axisY + STD_TICK_SEPARATOR + metrics.getAscent() + 2;
                boolean labelVisible = labelX >= 0 && labelX + labelWidth <= getWidth()
                        && labelY <= getHeight() - metrics.getDescent();
                g2d.drawLine(x, axisY - STD_TICK_SEPARATOR, x, axisY + STD_TICK_SEPARATOR);
                if (labelVisible) {
                    drawRotate(g2d, labelX, labelY, 45, label);
                }
            }
            return;
        }
        int negPixels = (int) Math.round(negW);
        int posPixels = Math.max(1, axisPixelsX - negPixels);
        double tickStepNeg = getTickStep(dataMinX, 0, Math.max(1, negPixels), minLabelPixels, xScaleNeg);
        double tickStepPos = getTickStep(0, dataMaxX, posPixels, minLabelPixels, xScalePos);
        double firstNeg = Math.ceil(dataMinX / tickStepNeg) * tickStepNeg;
        double lastNeg = Math.floor(0 / tickStepNeg) * tickStepNeg;
        for (double value = firstNeg; value <= lastNeg - (tickStepNeg * 0.5); value += tickStepNeg) {
            int x = (int) Math.round(mapX(value, dataMinX, negW, xScaleNeg, xScalePos, zeroXPx, offsetX));
            if (x < STD_PADDING || x > getWidth() - STD_PADDING) {
                continue;
            }
            String label = formatTickLabel(value, tickStepNeg);
            int labelWidth = metrics.stringWidth(label);
            int labelX = x - (labelWidth / 2);
            int labelY = axisY + STD_TICK_SEPARATOR + metrics.getAscent() + 2;
            boolean labelVisible = labelX >= 0 && labelX + labelWidth <= getWidth()
                    && labelY <= getHeight() - metrics.getDescent();
            g2d.drawLine(x, axisY - STD_TICK_SEPARATOR, x, axisY + STD_TICK_SEPARATOR);
            if (labelVisible) {
                drawRotate(g2d, labelX, labelY, 45, label);
            }
        }
        // Positive ticks
        double firstPos = Math.ceil(0 / tickStepPos) * tickStepPos;
        double lastPos = Math.floor(dataMaxX / tickStepPos) * tickStepPos;
        for (double value = firstPos; value <= lastPos + (tickStepPos * 0.5); value += tickStepPos) {
            int x = (int) Math.round(mapX(value, dataMinX, negW, xScaleNeg, xScalePos, zeroXPx, offsetX));
            if (x < STD_PADDING || x > getWidth() - STD_PADDING) {
                continue;
            }
            String label = formatTickLabel(value, tickStepPos);
            int labelWidth = metrics.stringWidth(label);
            int labelX = x - (labelWidth / 2);
            int labelY = axisY + STD_TICK_SEPARATOR + metrics.getAscent() + 2;
            boolean labelVisible = labelX >= 0 && labelX + labelWidth <= getWidth()
                    && labelY <= getHeight() - metrics.getDescent();
            g2d.drawLine(x, axisY - STD_TICK_SEPARATOR, x, axisY + STD_TICK_SEPARATOR);
            if (labelVisible) {
                drawRotate(g2d, labelX, labelY, 45, label);
            }
        }
    }

    private void drawYTicks(Graphics2D g2d, FontMetrics metrics, double dataMinY, double dataMaxY, double scale,
                            double offsetY, double axisXPx, double tickStep) {
        double firstTick = Math.ceil(dataMinY / tickStep) * tickStep;
        double lastTick = Math.floor(dataMaxY / tickStep) * tickStep;
        int axisX = (int) Math.round(axisXPx);
        for (double value = firstTick; value <= lastTick + (tickStep * 0.5); value += tickStep) {
            int y = (int) Math.round((getHeight() - offsetY) - (value - dataMinY) * scale);
            if (y < STD_PADDING || y > getHeight() - STD_PADDING) {
                continue;
            }
            String label = formatTickLabel(value, tickStep);
            int labelWidth = metrics.stringWidth(label);
            int labelX = axisX - STD_TICK_SEPARATOR - 4 - labelWidth;
            int labelY = y + (metrics.getAscent() / 2) - 2;
            boolean labelVisible = labelX >= 0 && labelX + labelWidth <= getWidth() && labelY >= metrics.getAscent()
                    && labelY <= getHeight() - metrics.getDescent();
            g2d.drawLine(axisX - STD_TICK_SEPARATOR, y, axisX + STD_TICK_SEPARATOR, y);
            if (labelVisible && !label.equals("-0")) {
                // Skipping duplicated "0" label
                g2d.drawString(label, labelX, labelY);
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
        g2d.drawString(plotData.axisX, xLabelX - 5, xLabelY - 30);
        g2d.drawString(plotData.axisY, yLabelX, yLabelY);
    }

    private void hover(MouseEvent event) {
        double[] geometry = getGraphGeometry();
        if (geometry == null) {
            showToolTip = false;
            return;
        }
        double dataMinX = geometry[0];
        double dataMinY = geometry[2];
        double yScale = geometry[4];
        double offsetX = geometry[5];
        double offsetY = geometry[6];
        double negW = geometry[9];
        double xScaleNeg = geometry[10];
        double xScalePos = geometry[11];
        double zeroXPx = geometry[12];
        int mouseX = event.getX();
        int mouseY = event.getY();
        int hoverRadiusSquared = (POINT_SIZE + 2) * (POINT_SIZE + 2);
        int hoveredIndex = -1;
        for (int pointIndex = 0; pointIndex < sampledData.length; pointIndex++) {
            int x = (int) Math
                    .round(mapX(sampledData[pointIndex][0], dataMinX, negW, xScaleNeg, xScalePos, zeroXPx, offsetX));
            int y = (int) Math.round((getHeight() - offsetY) - (sampledData[pointIndex][1] - dataMinY) * yScale);
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

    private void drawHoverTooltip(Graphics2D g2d, double dataMinX, double dataMinY, double yScale, double offsetX,
                                  double offsetY, double negW, double xScaleNeg, double xScalePos, double zeroXPx) {
        if (!showToolTip || toolTipIndex < 0 || toolTipIndex >= sampledData.length) {
            return;
        }
        int x = (int) Math
                .round(mapX(sampledData[toolTipIndex][0], dataMinX, negW, xScaleNeg, xScalePos, zeroXPx, offsetX));
        int y = (int) Math.round((getHeight() - offsetY) - (sampledData[toolTipIndex][1] - dataMinY) * yScale);
        String output = String.format("(%.3g, %.3g)", (double) sampledData[toolTipIndex][0],
                (double) sampledData[toolTipIndex][1]);
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
        double[] geometry = getGraphGeometry();
        if (geometry == null) {
            g2d.dispose();
            return;
        }
        double dataMinX = geometry[0];
        double dataMaxX = geometry[1];
        double dataMinY = geometry[2];
        double dataMaxY = geometry[3];
        double yScale = geometry[4];
        double offsetX = geometry[5];
        double offsetY = geometry[6];
        int axisX = (int) Math.round(geometry[7]);
        int axisY = (int) Math.round(geometry[8]);
        double negW = geometry[9];
        double xScaleNeg = geometry[10];
        double xScalePos = geometry[11];
        double zeroXPx = geometry[12];
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(Settings.TEXT_COLOR);
        // Draw X & Y Axes
        g2d.drawLine(STD_PADDING, axisY, getWidth() - STD_PADDING, axisY);
        g2d.drawLine(axisX, STD_PADDING, axisX, getHeight() - STD_PADDING);
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.setColor(Settings.TEXT_COLOR);
        // Compute dynamic tick density from available pixels and label sizes
        int axisPixelsY = Math.max(1, getHeight() - (STD_PADDING * 2));
        int maxLabelHeight = metrics.getAscent() + metrics.getDescent();
        double tickStepY = getTickStep(dataMinY, dataMaxY, axisPixelsY, Math.max(16, maxLabelHeight + 4), yScale);
        drawXTicks(g2d, metrics, dataMinX, dataMaxX, offsetX, axisY, negW, xScaleNeg, xScalePos, zeroXPx);
        drawYTicks(g2d, metrics, dataMinY, dataMaxY, yScale, offsetY, axisX, tickStepY);
        // Draw all scatter points using the same coordinate transformation
        g2d.setColor(Settings.HIGHLIGHT);
        for (float[] point : sampledData) {
            int x = (int) Math.round(mapX(point[0], dataMinX, negW, xScaleNeg, xScalePos, zeroXPx, offsetX));
            int y = (int) Math.round((getHeight() - offsetY) - (point[1] - dataMinY) * yScale);
            g2d.fillOval(x - (POINT_SIZE / 2), y - (POINT_SIZE / 2), POINT_SIZE, POINT_SIZE);
            // Add black outline for each oval
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawOval(x - (POINT_SIZE / 2), y - (POINT_SIZE / 2), POINT_SIZE, POINT_SIZE);
            g2d.setColor(Settings.HIGHLIGHT);
        }
        // Draw hover tooltip and overlay texts last for readability
        drawHoverTooltip(g2d, dataMinX, dataMinY, yScale, offsetX, offsetY, negW, xScaleNeg, xScalePos, zeroXPx);
        drawAxisLabels(g2d, metrics, axisX, axisY);
        setTitle(g2d);
        g2d.dispose();
    }
}
