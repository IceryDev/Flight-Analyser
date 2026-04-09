package com.still_processing.UILib;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JLabel;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * This class creates a pie chart that generates gradually.
 *
 *
 * @author Jessica Chen
 */

public class PieChartGraph extends JPanel implements Runnable {

    Thread graphThread;
    private String chartTitle = "Pie Chart";
    private final int FPS = 60;
    private JPanel TOP_BAR;
    private boolean showTopBar = false;
    private double animationProgress = 0.0;
    private static final double ANIMATION_DURATION = 300.0;
    private HashMap<String, Integer> data;
    private int legendWidth = 180;
    private int padding = 50;
    private int legendFontSize = 18;
    private int percentFontSize = 25;
    private int chartY;

    private JLabel title;
    private Color color1 = LIGHT_HIGHLIGHT;
    private Color color2 = LIGHT_BLUE;
    private Color color3 = LIGHT_GREEN;
    private Color color4 = HIGHLIGHT;
    private Color color5 = CYAN;
    private Color color6 = LIGHT_BURGUNDY;
    private Color color7 = BURGUNDY;

    private Color[] PALETTE = {
            color1,
            color2,
            color3,
            color4,
            color5,
            color6,
            color7
    };

    /**
     *
     * @author Jessica Chen
     */
    public PieChartGraph(HashMap<String, Integer> data, boolean showTopBar) {
        this.showTopBar = showTopBar;
        if (data != null) {
            if (!data.isEmpty()) {
                this.data = data;
                setLayout(new BorderLayout(0, 0));
                if (showTopBar) {
                    TOP_BAR = buildTopBar();
                    add(TOP_BAR, BorderLayout.NORTH);
                }
            }
        }
        this.setPreferredSize(new Dimension(760, 600));
        this.setMinimumSize(new Dimension(300, 400));
        this.setDoubleBuffered(true);
        setVisible(true);
    }

    /**
     * Build top Bar
     * 
     * @author Jessica Chen
     */
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        bar.setBackground(LABEL_COLOR);
        title = new JLabel(chartTitle);
        title.setForeground(BACKGROUND);
        title.setFont(BOLD_FONT);
        bar.add(title);
        return bar;
    }

    /**
     * Animation
     * 
     * @author Zhou Sun
     */
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (graphThread != null && animationProgress != 1) {
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
        animationProgress = 0;
        graphThread = new Thread(this);
        graphThread.start();
    }

    /**
     * Update Variables
     * 
     * @author Jessica Chen
     */
    private void update() {
        if (animationProgress < 1.0) {
            animationProgress = Math.min(animationProgress + 1.0 / ANIMATION_DURATION, 1.0);
        }
    }

    private double sineMotion(double time) {
        return -(Math.cos(Math.PI * time) - 1.0) / 2.0;
    }

    /**
     * Draw method
     * 
     * @author Jessica Chen
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (data != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int chartDiameter = Math.min(panelWidth - legendWidth - padding * 2, panelHeight - padding * 4);
            chartDiameter = Math.max(chartDiameter, 100);
            if (TOP_BAR != null) {
                chartY = (panelHeight - chartDiameter) / 2 + TOP_BAR.getHeight() / 2;
            } else {
                chartY = (panelHeight - chartDiameter) / 2;
            }
            int chartX = (panelWidth - legendWidth - chartDiameter) / 2;
            double animatedSweep = sineMotion(animationProgress) * 360;
            List<String> keys = new ArrayList<>(data.keySet());
            int total = keys.stream().mapToInt(data::get).sum();
            double startAngle = 0;
            double remainingAngle = animatedSweep;
            for (int i = 0; i < keys.size(); i++) {
                if (remainingAngle <= 0) {
                    break;
                }
                String label = keys.get(i);
                int value = data.get(label);
                double sweep = 360.0 * value / total;
                double drawingSweep = Math.min(sweep, remainingAngle);

                g2d.setColor(PALETTE[i % PALETTE.length]);
                g2d.fill(new Arc2D.Double(chartX, chartY, chartDiameter, chartDiameter, startAngle, drawingSweep,
                        Arc2D.PIE));

                // Percentage label inside slice
                // Due to inaccurate rounding in floating point numbers,
                // add 0.01 to fix
                if (sweep > 0.01 && remainingAngle + 0.01 >= sweep) {
                    double midAngle = Math.toRadians(startAngle + sweep / 2);
                    int labelR = chartDiameter / 4;
                    int labelX = chartX + chartDiameter / 2 + (int) (labelR * Math.cos(midAngle));
                    int labelY = chartY + chartDiameter / 2 - (int) (labelR * Math.sin(midAngle));
                    String pct = String.format("%.1f%%", 100.0 * value / total);
                    g2d.setColor(BACKGROUND);
                    g2d.setFont(BOLD_FONT.deriveFont((float) percentFontSize));
                    int textW = g2d.getFontMetrics().stringWidth(pct);
                    g2d.drawString(pct, labelX - textW / 2, labelY + 5);
                }
                startAngle += sweep;
                remainingAngle -= drawingSweep;
            }
            int legendX = chartX + chartDiameter + padding + 80;
            int legendStartY = chartY + 80;
            int swatchSize = 16;
            int rowHeight = 28;
            g2d.setFont(REGULAR_FONT.deriveFont((float) legendFontSize));
            for (int i = 0; i < keys.size(); i++) {
                String label = keys.get(i);
                int value = data.get(label);
                int rowY = legendStartY + i * rowHeight;
                g2d.setColor(PALETTE[i % PALETTE.length]);
                g2d.fillRoundRect(legendX, rowY, swatchSize, swatchSize, 4, 4);
                g2d.setColor(LABEL_COLOR);
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(legendX, rowY, swatchSize, swatchSize, 4, 4);
                g2d.setColor(getForeground());
                g2d.drawString(label + " (" + value + ")", legendX + swatchSize + 8, rowY + swatchSize - 2);
            }
        }
        if (data == null) {
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
     * Create Setters
     * 
     * @author Jessica Chen
     */
    public void setChartTitle(String chartTitle) {
        if (chartTitle != null && data != null) {
            this.chartTitle = chartTitle;
            this.title.setText(this.chartTitle);
        }
    }

    public void setLegendWidth(int legendWidth) {
        if (legendWidth > 0) {
            this.legendWidth = legendWidth;
        }
    }

    public void setPadding(int padding) {
        if (padding > 0) {
            this.padding = padding;
        }
    }

    public void setLegendFontSize(int legendFontSize) {
        if (legendFontSize > 0) {
            this.legendFontSize = legendFontSize;
        }
    }

    public void setPercentFontSize(int percentFontSize) {
        if (percentFontSize > 0) {
            this.percentFontSize = percentFontSize;
        }
    }
}
