package com.still_processing.UILib;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.FontMetrics;
import java.awt.Font;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@code BarChartGraph} class is used to build a bar chart that has animation.
 */
public class BarChartGraph extends JPanel implements Runnable{
    Thread graphThread;
    private final int FPS = 60;
    private Map<String, Float> data = null;
    private String[] labels;
    private float[] values;
    private int yStep = 5;
    private int padding = 90;
    private int barGap = 8;
    private double renderPercentage = 0;
    private final String CHART_TITLE = "Bar Chart";

    /**
     * @author Jessica Chen
     */
    public BarChartGraph(Map<String, Float> data) {

        if(data != null && !data.isEmpty()){
            this.data = data;
            labels = new String[data.size()];
            values = new float[data.size()];

            int i = 0;
            for (Map.Entry<String, Float> entry : data.entrySet()) {
                if (entry.getKey().length() > 3){
                    labels[i] = entry.getKey().substring(0,3);
                }
                else {
                    labels[i] = entry.getKey();
                }
                values[i] = entry.getValue();
                i++;
            }
        }
        this.setPreferredSize(new Dimension(700, 450));
        this.setMinimumSize(new Dimension(300, 400));
        this.setDoubleBuffered(true);
        setLayout(new BorderLayout(0, 0));
        add(buildTopBar(), BorderLayout.NORTH);
        setVisible(true);
    }

    /**
     * For the animation effects
     * @author Zhou Sun
     */
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

    private void update() {
        if(renderPercentage < 1){
            renderPercentage += 0.02;
        }
        if (renderPercentage > 1){
            renderPercentage = 1;
        }
    }

    /**
     * The draw method for the graph.
     * @author Jessica Chen
     */
    @Override
    protected void paintComponent(Graphics graphics) {

        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float max = values[0];
        for (float value : values) {
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
        drawAxisTitles(g2d, "Category", "Value");
        g2d.dispose();
    }
    /**
     * To build the top panel
     * @author Jessica Chen
     */
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        bar.setBackground(new Color(40, 44, 60));
        JLabel label = new JLabel(CHART_TITLE);
        label.setForeground(new Color(180, 185, 210));
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        bar.add(label);
        return bar;
    }
    /**
     * To draw the axis
     * @author Jessica Chen
     */
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
        g2d.drawLine(padding, padding, padding, chartHeight + padding);
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
        float scale =  (float)chartHeight / maxYsteps;

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
    /**
     * To draw the axis titles
     * @author Jessica Chen
     */
    private void drawAxisTitles(Graphics2D g2d, String xTitle, String yTitle){

        AffineTransform original = g2d.getTransform();
        g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2d.setColor(new Color(5, 37, 46, 211));
        FontMetrics fontValues = g2d.getFontMetrics();

        int xTitleX = (getWidth()/2) - fontValues.stringWidth(xTitle)/2;
        int xTitleY = getHeight() - padding/3 ;
        g2d.drawString(xTitle, xTitleX, xTitleY);


        AffineTransform originalTransform = g2d.getTransform();
        g2d.rotate(-Math.PI / 2);
        int yTitleX = -(getHeight() / 2) - (fontValues.stringWidth(yTitle) / 2);
        int yTitleY = padding / 3;
        g2d.drawString(yTitle, yTitleX, yTitleY);
        g2d.setTransform(originalTransform);


    }
//    public static void main(String[] args){
//        SwingUtilities.invokeLater(() -> {
//            Map<String, Float> categoricalData = new LinkedHashMap<>();
//            categoricalData.put("Monday", 12.0F);
//            categoricalData.put("Te", 7.5F);
//            categoricalData.put("Wed", 19.0F);
//            categoricalData.put("Thu",  5.0F);
//            categoricalData.put("Fri", 14.5F);
//            categoricalData.put("Sat", 22.0F);
//            categoricalData.put("Sun",  9.0F);
//
//            JFrame catFrame = new JFrame("Bar Chart - Days of Week");
//            catFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            catFrame.setLocationRelativeTo(null);
//
//            BarChartGraph catChart = new BarChartGraph(categoricalData);
//            catFrame.add(catChart);
//            catFrame.pack();
//            catFrame.setVisible(true);
//            catChart.animate();
//        });
//    }
}
