package com.still_processing.Application.AnalysisPage;

import com.still_processing.FlightData.CSVHandler;
import com.still_processing.FlightData.Database;
import com.still_processing.UILib.BarChartGraph;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.TextPaneBuilder;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import com.still_processing.UILib.Histogram;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia
 */

public class AnalysisPanel extends JPanel implements Scrollable {
    public AnalysisPanel(ActionListener a) {

        System.out.println("=== Analysis Panel ===");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);

        String title = "Flight Analysis";
        JTextPane textPane = new TextPaneBuilder()
                .setText(title)
                .setFontSize(48)
                .setFont(BOLD_FONT)
                .build();

        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        this.add(textPane);

        JButton button1 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Analyse")
                .setFontSize(35).build();
        this.add(button1);
        button1.addActionListener(a);

        JButton button2 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Home Page")
                .setFontSize(35).build();
        this.add(button2);
        button2.addActionListener(a);

        HashMap<String, Float> chartData = new HashMap<>();
        chartData.put("Monday", 12.0F);
        chartData.put("Te", 7.5F);
        chartData.put("Wed", 19.0F);
        chartData.put("Thu", 5.0F);
        chartData.put("Fri", 14.5F);
        chartData.put("Sat", 101.0F);
        chartData.put("Sun", 9.0F);
        chartData.put("Testing", 9.0F);
        chartData.put("nothing here", 9.0F);
        chartData.put("\0", 9.0F);
        BarChartGraph chartGraph = new BarChartGraph(chartData);
        chartGraph.setPreferredSize(new Dimension(0, 1440));
        chartGraph.animate();
        chartGraph.setBorder(BorderFactory.createLineBorder(HIGHLIGHT, 5));
        this.add(chartGraph);

        CSVHandler.loadOfflineFlightCSV();
        float[] histData = Database.getDistance(Database.offlineFlights);
        Histogram histogram = new Histogram(histData, 500, 250);
        histogram.animate();
        histogram.setPreferredSize(new Dimension(0, 1440));
        this.add(histogram);
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

        return false;
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
