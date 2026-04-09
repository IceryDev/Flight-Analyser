package com.still_processing.Application.AnalysisPage;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Scrollable;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Graphs.PropertyType;
import com.still_processing.FlightData.Graphs.ScatterPlotData;
import com.still_processing.UILib.BarChartGraph;
import com.still_processing.UILib.DropdownBuilder;
import com.still_processing.UILib.Histogram;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.PieChartGraph;
import com.still_processing.UILib.RoundedButton;
import com.still_processing.UILib.ScatterPlot;
import com.still_processing.UILib.TableBuilder;
import com.still_processing.UILib.TextPaneBuilder;

import static com.still_processing.DefaultSettings.Settings.*;
import static com.still_processing.FlightData.Statistics.*;

/**
 * Draws the Analysis Page
 *
 * @author Deea Zaharia
 * @author Jagoda Koczwara-Szuba
 * @author Jessica Chen
 * @author Zhou Sun
 */
public class AnalysisPanel extends JPanel implements Scrollable, ActionListener {
    JPanel graphDisplay;
    Histogram histogram;
    Histogram latenessHistogram;
    Histogram distanceHistogram;
    ScatterPlot latenessVsDistance;
    PieChartGraph departureTimeGraph;
    CardLayout cardLayout;
    BarChartGraph barChart;
    ActionListener sceneSwitch;
    JPanel titlePanel;

    public AnalysisPanel(ActionListener sceneSwitch) {
        System.out.println("=== Analysis Panel ===");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.sceneSwitch = sceneSwitch;

        renderGraph();
        this.add(titlePanel);
        this.add(graphDisplay);
    }

    /**
     * Rerender the graph with updated data
     * remove/delete the old graphes and adds the new graphes
     * 
     * @author Zhou Sun
     */
    public void refreshGraph() {
        graphDisplay.removeAll();
        this.remove(titlePanel);
        this.remove(graphDisplay);
        renderGraph();
        this.add(titlePanel);
        this.add(graphDisplay);
        revalidate();
        repaint();
    }

    /**
     * Render the graphes based on the filtered data
     *
     * @author Zhou Sun
     */
    public void renderGraph() {
        float[] latenessData = null;
        float[] distance = null;
        HashMap<String, Integer> flightOrigins = null;
        ScatterPlotData scatterPlotData = null;
        HashMap<String, Integer> departureTimeOfDay = null;

        if (Database.flightData != null) {
            latenessData = Database.getLateness(Database.flightData);
            distance = Database.getDistance(Database.flightData);
            flightOrigins = Database.getCategoricalFreq(Database.flightData,
                    PropertyType.ORIGIN);
            scatterPlotData = Database.getScatterPlot(Database.flightData, PropertyType.LATENESS,
                    PropertyType.DISTANCE);
            departureTimeOfDay = new HashMap<>();
        }

        ArrayList<String> graphOptionsTemp = new ArrayList<>();
        graphOptionsTemp.add("--Select Option--");
        if (latenessData != null && latenessData.length != 0) {
            graphOptionsTemp.add("Lateness");
        }
        if (distance != null && distance.length != 0) {
            graphOptionsTemp.add("Distance");
        }
        if (flightOrigins != null && !flightOrigins.isEmpty()) {
            graphOptionsTemp.add("Top 10 Airports");
        }
        if (scatterPlotData != null && scatterPlotData.data != null && scatterPlotData.data.length != 0) {
            graphOptionsTemp.add("ScatterPlot");
        }

        if (Database.flightData != null && !Database.flightData.isEmpty()) {
            departureTimeOfDay.put("Early Morning (12am-6am)", 0);
            departureTimeOfDay.put("Morning (6am-12pm)", 0);
            departureTimeOfDay.put("Afternoon (12pm-6pm)", 0);
            departureTimeOfDay.put("Evening (6pm-12am)", 0);

            for (int i = 0; i < Database.flightData.size(); i++) {
                FlightInfo flightInfo = Database.flightData.get(i);
                if (flightInfo.depTime != null && !flightInfo.depTime.isEmpty()) {
                    int hour = Integer.parseInt(flightInfo.depTime.split(":")[0]);
                    if (hour < 6) {
                        departureTimeOfDay.merge("Early Morning (12am-6am)", 1, Integer::sum);
                    } else if (hour < 12) {
                        departureTimeOfDay.merge("Morning (6am-12pm)", 1, Integer::sum);
                    } else if (hour < 18) {
                        departureTimeOfDay.merge("Afternoon (12pm-6pm)", 1, Integer::sum);
                    } else {
                        departureTimeOfDay.merge("Evening (6pm-12am)", 1, Integer::sum);
                    }
                }
            }
        }

        if (departureTimeOfDay != null && !departureTimeOfDay.isEmpty()
                && !departureTimeOfDay.values().stream().allMatch(x -> x == 0)) {
            graphOptionsTemp.add("Departure Times");
        }

        String[] graphOptions = new String[graphOptionsTemp.size()];
        graphOptions = graphOptionsTemp.toArray(graphOptions);

        JComboBox<String> dropDown = new DropdownBuilder(graphOptions)
                .setFontSize(18)
                .build();
        dropDown.setMaximumSize(new Dimension(70, 75));
        dropDown.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        dropDown.addActionListener(this);

        titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        ImagePanel logo = new ImagePanel("/Images/logo.png", 70, 70);
        String title = "Flight Analyser";
        JTextPane textPane = new TextPaneBuilder()
                .setText(title)
                .setFontSize(36)
                .setFont(BOLD_FONT)
                .build();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        FontMetrics metrics = getFontMetrics(BOLD_FONT.deriveFont(36f));
        int textHeight = metrics.getHeight() / 2 + metrics.getMaxAscent();
        int textWidth = metrics.stringWidth(title);
        textPane.setSize(new Dimension(textWidth, textHeight));
        textPane.setMaximumSize(new Dimension(textWidth, textHeight));

        JButton homeButton = new RoundedButton("Return Home", 40, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        homeButton.setMinimumSize(new Dimension(215, 40));
        homeButton.setPreferredSize(new Dimension(215, 40));
        homeButton.setMaximumSize(new Dimension(215, 40));
        homeButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        homeButton.addActionListener(sceneSwitch);

        titlePanel.setOpaque(false);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(dropDown);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(homeButton);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));

        final int graphHeight = 900;

        String[] columnNames = { "Mean", "Median", "Variance", "SD" };

        JPanel latenessDisplay = new JPanel();
        latenessDisplay.setLayout(new BoxLayout(latenessDisplay, BoxLayout.Y_AXIS));

        latenessDisplay.setSize(new Dimension(700, 900));
        latenessHistogram = new Histogram(latenessData, 100, 1000);
        if (latenessData != null && latenessData.length != 0) {
            float maxLateness = 0;
            for (float dataPoint : latenessData) {
                maxLateness = (maxLateness > dataPoint) ? maxLateness : dataPoint;
            }
            latenessHistogram.setXStep((maxLateness > 500) ? 250 : (maxLateness > 200) ? 50 : 10);
            latenessHistogram.setYStep((latenessData.length <= 10) ? 1 : latenessData.length / 10);
            latenessHistogram.setXLengendText("Lateness (in minutes)");
            latenessHistogram.setYLengendText("Number of Flights");
            latenessHistogram.setPreferredSize(new Dimension(0, graphHeight));

            Object[][] latenessStats = { { arithmeticMean(latenessData), median(latenessData), variance(latenessData),
                    standardDeviation(latenessData) } };

            JScrollPane latenessStatsTable = new TableBuilder(latenessStats, columnNames)
                    .setFontSize(24)
                    .setFont(BOLD_FONT)
                    .setColumnWidth(new int[] { 100, 500, 100, 100 })
                    .buildPane();
            latenessStatsTable.setBorder(BorderFactory.createEmptyBorder(50, 50, 0, 50));
            latenessStatsTable.setMinimumSize(new Dimension(Integer.MAX_VALUE, 200));
            latenessStatsTable.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
            latenessStatsTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            latenessDisplay.add(latenessStatsTable);
        }
        latenessDisplay.add(latenessHistogram);
        latenessDisplay.setOpaque(false);

        JPanel distanceDisplay = new JPanel();
        distanceDisplay.setLayout(new BoxLayout(distanceDisplay, BoxLayout.Y_AXIS));
        distanceDisplay.setSize(new Dimension(700, 900));

        distanceHistogram = new Histogram(distance, 250, 200);
        if (distance != null && distance.length != 0) {
            float maxDistance = 0;
            for (float dataPoint : distance) {
                maxDistance = (maxDistance > dataPoint) ? maxDistance : dataPoint;
            }
            int xStep = 50;
            if (maxDistance > 5_000_000)
                xStep = 2_000_000;
            if (maxDistance > 1_000_000)
                xStep = 500_000;
            else if (maxDistance > 10000)
                xStep = 2000;
            else if (maxDistance > 5000)
                xStep = 1000;
            else if (maxDistance > 500)
                xStep = 250;

            distanceHistogram.setXStep(xStep);
            distanceHistogram.setYStep((distance.length <= 10) ? 1 : distance.length / 10);
            distanceHistogram.setXLengendText("Distance (in kilometers)");
            distanceHistogram.setYLengendText("Number of Flights");

            Object[][] distanceStats = {
                    { arithmeticMean(distance), median(distance), variance(distance), standardDeviation(distance) } };

            JScrollPane distanceStatsTable = new TableBuilder(distanceStats, columnNames)
                    .setFontSize(24)
                    .setFont(BOLD_FONT)
                    .setColumnWidth(new int[] { 100, 500, 100, 100 })
                    .buildPane();
            distanceStatsTable.setBorder(BorderFactory.createEmptyBorder(50, 50, 0, 50));
            distanceStatsTable.setMinimumSize(new Dimension(Integer.MAX_VALUE, 150));
            distanceStatsTable.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
            distanceStatsTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            distanceDisplay.add(distanceStatsTable);
        }
        distanceDisplay.add(distanceHistogram);
        distanceDisplay.setOpaque(false);

        Map<String, Float> sortedTop10Airports = new LinkedHashMap<>();
        if (flightOrigins != null && !flightOrigins.isEmpty()) {
            Map<String, Float> floatOriginMap = flightOrigins.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().floatValue()));
            List<Map.Entry<String, Float>> top10Entries = floatOriginMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Sort by value descending
                    .limit(10)
                    .collect(Collectors.toList());

            for (Map.Entry<String, Float> entry : top10Entries) {
                sortedTop10Airports.put(entry.getKey(), entry.getValue());
            }
        }
        barChart = new BarChartGraph(sortedTop10Airports, false);
        barChart.setOpaque(false);
        barChart.setPreferredSize(new Dimension(0, graphHeight));
        barChart.setXLegend("Top 10 Origins");
        barChart.setXLegend("Flight Counts");

        float maxCount = 0;
        for (String key : sortedTop10Airports.keySet()) {
            float dataPoint = sortedTop10Airports.get(key);
            maxCount = (maxCount > dataPoint) ? maxCount : dataPoint;
        }
        int yStep = 5;
        if (maxCount > 200)
            yStep = 50;
        else if (maxCount > 50)
            yStep = 10;
        barChart.setYStep(yStep);

        departureTimeGraph = new PieChartGraph(departureTimeOfDay, false);
        departureTimeGraph.setOpaque(false);

        if (scatterPlotData != null && scatterPlotData.data != null && scatterPlotData.data.length != 0) {
            latenessVsDistance = new ScatterPlot(scatterPlotData, "Lateness Against Distance");
        }
        latenessVsDistance.setOpaque(false);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(BACKGROUND);
        if (graphOptionsTemp.size() == 1) {
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            ImagePanel errorImage = new ImagePanel("/Images/error-message.png", 500, 500);
            emptyPanel.add(Box.createVerticalGlue());
            emptyPanel.add(errorImage);
            emptyPanel.add(Box.createVerticalGlue());
        }
        cardLayout = new CardLayout();
        graphDisplay = new JPanel(cardLayout);
        graphDisplay.setOpaque(false);
        graphDisplay.add(emptyPanel, "empty");
        graphDisplay.add(latenessDisplay, "lateness");
        graphDisplay.add(distanceDisplay, "distance");
        graphDisplay.add(barChart, "Top 10 Airports");
        graphDisplay.add(latenessVsDistance, "ScatterPlot");
        graphDisplay.add(departureTimeGraph, "Departure Times");
        this.add(graphDisplay);
    }

    /**
     * Triggers the graph animations
     *
     * @author Deea Zaharia
     */
    public void startRender() {
        latenessHistogram.animate();
        distanceHistogram.animate();
        barChart.animate();
    }

    /**
     * Graph selection dropdown
     *
     * @author Jagoda Koczwara-Szuba
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> dropDown = (JComboBox<String>) e.getSource();
        dropDown.getParent().repaint();
        switch ((String) dropDown.getSelectedItem()) {
            case "--Select Option--":
                break;
            case "Lateness":
                cardLayout.show(graphDisplay, "lateness");
                latenessHistogram.animate();
                break;
            case "Distance":
                cardLayout.show(graphDisplay, "distance");
                distanceHistogram.animate();
                break;
            case "Top 10 Airports":
                cardLayout.show(graphDisplay, "Top 10 Airports");
                barChart.animate();
                break;
            case "ScatterPlot":
                cardLayout.show(graphDisplay, "ScatterPlot");
                break;
            case "Departure Times":
                cardLayout.show(graphDisplay, "Departure Times");
                departureTimeGraph.animate();
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {

        return null;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {

        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {

        return true;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 32;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 32;
    }
}
