package com.still_processing.UILib;

import com.still_processing.DefaultSettings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BarChartGraph extends JPanel implements Runnable{
    Thread graphThread;
    private final int FPS = 60;
    private Map<String, Double> data = null;
    private String[] labels;
    private double[] values;
    private int yStep = 5;
    private int padding = 60;
    private int barGap = 8;
    private double renderPercentage = 0;

    public BarChartGraph(Map<String, Double> data) {
        if(data != null && !data.isEmpty()){
            this.data = data;
            labels = new String[data.size()];
            values = new double[data.size()];

            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                labels[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
        }
        this.setDoubleBuffered(true);
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

    // This initialises the Graph - don't change
    public void animate() {
        graphThread = new Thread(this);
        graphThread.start();
    }

    // Update variables
    private void update() {
        if(renderPercentage < 1){
            renderPercentage += 0.02;
        }
        if (renderPercentage > 1){
            renderPercentage = 1;
        }
    }

    // actually draw method
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double max = values[0];
        for (double value : values) {
            max = Math.max(max, value);
        }
        int chartWidth = getWidth() - 2*padding;
        int chartHeight = getHeight() - 2*padding;

        int totalGaps = barGap * (values.length - 1);
        int barWidth = (chartWidth - totalGaps)/values.length;
        double maxValue = (Math.floor(max / yStep) + 1) * yStep;

        for (int i = 0; i < values.length; i++){
            int xPos = padding + i*(barWidth + barGap);
            int yPos = (int) (chartHeight * values[i] / maxValue);
            int barProgress = (int)(yPos * renderPercentage);
            int barTop = (int)(getHeight() - padding - barProgress);

            g2d.setColor(new Color(0x01796F));
            g2d.fillRect(xPos, barTop, barWidth, barProgress);

            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(0x001917));
            g2d.drawRect(xPos, barTop, barWidth, barProgress);
        }
        drawAxis(g2d, max);
        g2d.dispose();
    }
    private void drawAxis(Graphics2D g2d, double max){
        if(data == null){
            return;
        }
        int chartWidth = getWidth() - 2*padding;
        int chartHeight = getHeight() - 2*padding;

        int totalGaps = barGap * (values.length - 1);
        int barWidth = (chartWidth - totalGaps)/values.length;

        g2d.setColor(new Color(0x001917));

        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();

        for (int i = 0; i < values.length; i++) {
            int xPos  = padding + i * (barWidth + barGap);
            int tickX = xPos + barWidth / 2;

            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(tickX, getHeight() - padding, tickX, getHeight() - padding + 4);

            int labelWidth = fm.stringWidth(labels[i]);
            g2d.drawString(labels[i], tickX - labelWidth / 2, getHeight() - padding + 16);

        }
        int maxYsteps = (int) Math.ceil(max / yStep);
        double scale = (double) chartHeight / maxYsteps;

        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics font = g2d.getFontMetrics();

        for (int i = 0; i <= maxYsteps; i++) {
            int y = (int) (getHeight() - padding - i * scale);

            g2d.drawLine(padding, y, padding - 4, y);

            String label = String.format("%.1f", i * (double) yStep);
            int labelWidth = font.stringWidth(label);

            g2d.drawString(label, padding - labelWidth - 6, y + 4);
        }
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            Map<String, Double> categoricalData = new LinkedHashMap<>();
            categoricalData.put("Mon", 12.0);
            categoricalData.put("Tue",  7.5);
            categoricalData.put("Wed", 19.0);
            categoricalData.put("Thu",  5.0);
            categoricalData.put("Fri", 14.5);
            categoricalData.put("Sat", 22.0);
            categoricalData.put("Sun",  9.0);

            JFrame catFrame = new JFrame("Bar Chart - Days of Week");
            catFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            catFrame.setSize(700, 450);
            catFrame.setLocationRelativeTo(null);

            BarChartGraph catChart = new BarChartGraph(categoricalData);
            catFrame.add(catChart);
            catFrame.setVisible(true);
            catChart.animate();
        });
    }
}
