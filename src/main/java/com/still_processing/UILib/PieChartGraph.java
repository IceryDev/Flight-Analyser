package com.still_processing.UILib;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * This class creates a pie chart that generates gradually.
 *
 *
 * @author Jessica Chen
 */

public class PieChartGraph extends JPanel implements Runnable {
    private final String CHART_TITLE = "Pie Chart";

    Thread graphThread;
    private final int FPS = 60;

    private double animationProgress = 0.0;
    private static final double ANIMATION_DURATION = 300.0;
    private final JPanel TOP_BAR;

    private static final Color[] PALETTE = {
            new Color(0xD31E91B3, true),
            new Color(0x4ECDC4),
            new Color(0x45B7D1),
            new Color(0x96CEB4),
            new Color(0x16599C),
            new Color(0xD4A5A5),
            new Color(0x9B59B6),
            new Color(0x3498DB),
    };

    private final HashMap<String, Integer> data = getData();
    private static HashMap<String, Integer> getData() {
        HashMap<String, Integer> data = new HashMap<>();
        data.put("RyanAir", 3);
        data.put("AerLingus", 5);
        data.put("Scoot", 3);
        data.put("Other Airlines", 3);
        return data;
    }

    public PieChartGraph() {
        this.setPreferredSize(new Dimension(760, 600));
        this.setMinimumSize(new Dimension(300, 400));
        this.setDoubleBuffered(true);
        setLayout(new BorderLayout(0, 0));
        TOP_BAR = buildTopBar();
        add(TOP_BAR, BorderLayout.NORTH);
        setVisible(true);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        bar.setBackground(new Color(40, 44, 60));
        JLabel label = new JLabel(CHART_TITLE);
        label.setForeground(new Color(180, 185, 210));
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        bar.add(label);
        return bar;
    }

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

    public void animate() {
        graphThread = new Thread(this);
        graphThread.start();
    }

    // Update variables
    private void update() {
        if (animationProgress < 1.0) {
            animationProgress = Math.min(animationProgress + 1.0 / ANIMATION_DURATION, 1.0);
        }
    }

    private double sineMotion(double time){
        return -(Math.cos(Math.PI * time) - 1.0) / 2.0;
    }

    // draw method
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int panelWidth  = getWidth();
        int panelHeight = getHeight();
        int legendWidth = 180;
        int padding = 50;
        int chartDiameter = Math.min(panelWidth - legendWidth - padding * 2, panelHeight - padding * 4);
        chartDiameter = Math.max(chartDiameter, 100);
        int chartY = (panelHeight - chartDiameter) / 2 + TOP_BAR.getHeight() / 2;
        int chartX = padding;
        double animatedSweep = sineMotion(animationProgress)*360;
        List<String> keys  = new ArrayList<>(data.keySet());
        int total          = keys.stream().mapToInt(data::get).sum();
        double startAngle  = 0;
        double remainingAngle = animatedSweep;
        for (int i = 0; i < keys.size(); i++) {
            if (remainingAngle <= 0){
                break;
            }
            String label = keys.get(i);
            int value = data.get(label);
            double sweep = 360.0 * value / total;
            double drawingSweep = Math.min(sweep, remainingAngle);

            g2d.setColor(PALETTE[i % PALETTE.length]);
            g2d.fill(new Arc2D.Double(chartX, chartY, chartDiameter, chartDiameter, startAngle, drawingSweep, Arc2D.PIE));
            // Percentage label inside slice
            if (remainingAngle >= sweep){
                double midAngle = Math.toRadians(startAngle + sweep / 2);
                int labelR = chartDiameter / 4;
                int labelX = chartX + chartDiameter / 2 + (int) (labelR * Math.cos(midAngle));
                int labelY = chartY + chartDiameter / 2 - (int) (labelR * Math.sin(midAngle));
                String pct = String.format("%.1f%%", 100.0 * value / total);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
                int textW = g2d.getFontMetrics().stringWidth(pct);
                g2d.drawString(pct, labelX - textW / 2, labelY + 5);
            }
            startAngle += sweep;
            remainingAngle -= drawingSweep;
        }

        int legendX  = chartX + chartDiameter + padding + 80;
        int legendStartY = chartY + 80;
        int swatchSize = 16;
        int rowHeight = 28;
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 13));
        for (int i = 0; i < keys.size(); i++) {
            String label = keys.get(i);
            int value = data.get(label);
            int rowY  = legendStartY + i * rowHeight;
            g2d.setColor(PALETTE[i % PALETTE.length]);
            g2d.fillRoundRect(legendX, rowY, swatchSize, swatchSize, 4, 4);
            g2d.setColor(new Color(60, 60, 60));
            g2d.setStroke(new BasicStroke(1f));
            g2d.drawRoundRect(legendX, rowY, swatchSize, swatchSize, 4, 4);
            g2d.setColor(getForeground());
            g2d.drawString(label + " (" + value + ")", legendX + swatchSize + 8, rowY + swatchSize - 2);
        }
        g2d.dispose();
    }
//Testing:
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Pie Chart");
//        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
//
//        PieChartGraph chart = new PieChartGraph();
//        frame.add(chart);
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//
//        chart.animate();
//    }
}
