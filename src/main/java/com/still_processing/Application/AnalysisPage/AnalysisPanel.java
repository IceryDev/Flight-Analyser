package com.still_processing.Application.AnalysisPage;

import com.still_processing.FlightData.CSVHandler;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.Histogram;
import com.still_processing.UILib.TextPaneBuilder;
import com.still_processing.FlightData.Database;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia
 */

public class AnalysisPanel extends JPanel implements Scrollable {
    Histogram histogram;
    Histogram latenessHistogram;
    public AnalysisPanel(ActionListener a){

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

        JButton button2 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Home Page").setFontSize(35).build();
        this.add(button2);
        button2.addActionListener(a);


        float[] data = {
                27.0f, 22.0f, 27.0f, 30.0f, 31.0f, 28.0f, 26.0f, 25.0f, 18.0f, 43.0f,
                31.0f, 30.0f, 33.0f, 25.0f, 19.0f, 24.0f, 27.0f, 25.0f, 23.0f, 18.0f,
                23.0f, 27.0f, 30.0f, 39.0f, 21.0f, 25.0f, 34.0f, 24.0f, 25.0f, 26.0f,
                32.0f, 35.0f, 27.0f, 21.0f, 22.0f, 32.0f, 21.0f, 25.0f, 22.0f, 20.0f,
                32.0f, 24.0f, 31.0f, 28.0f, 22.0f, 24.0f, 31.0f, 28.0f, 33.0f, 41.0f,
                25.0f, 36.0f, 32.0f, 32.0f, 37.0f, 26.0f, 30.0f, 25.0f, 21.0f, 23.0f,
                18.0f, 28.0f, 28.0f, 29.0f, 37.0f, 30.0f, 26.0f, 18.0f, 22.0f, 31.0f,
                25.0f, 30.0f, 37.0f, 24.0f, 28.0f, 29.0f, 27.0f, 26.0f, 16.0f, 31.0f,
                24.0f, 23.0f, 39.0f, 30.0f, 28.0f, 28.0f, 20.0f, 24.0f, 30.0f, 27.0f,
                24.0f, 24.0f, 33.0f, 32.0f, 32.0f, 25.0f, 36.0f, 28.0f, 32.0f, 32.0f
        };
        histogram = new Histogram(data, 5, 3);
        this.add(histogram);

        CSVHandler.loadOfflineFlightCSV();

        float[] latenessData = Database.getLateness(Database.offlineFlights);
        System.out.println(latenessData.length);
        latenessHistogram = new Histogram(latenessData, 240, 1000);
        this.add(latenessHistogram);
    }

    public void startRender(){
        histogram.animate();
        latenessHistogram.animate();
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


