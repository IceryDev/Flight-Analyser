package com.still_processing.Application.HomePage;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import static com.still_processing.DefaultSettings.Settings.*;
import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

import java.awt.event.*;

import com.still_processing.FlightData.Airport;
import com.still_processing.FlightData.CSVHandler;
import com.still_processing.FlightData.Database;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.TableBuilder;
import com.still_processing.UILib.TextPaneBuilder;

/**
 * @author Zhou Sun
 */
public class BodyPanel extends JPanel implements Scrollable {

    public BodyPanel(ActionListener a) {

        System.out.println("=== Body Panel ===");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);

        String title = "Flight Analyser";
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

        CSVHandler.loadAirportCSV();
        Map<String, Airport> airports = Database.getAirports();
        String[] columnNames = {
                "IATA Code", "Name", "Country", "Region"
        };
        Object[][] data = new Object[airports.size()][4];

        int dataIndex = 0;
        for (String iata : airports.keySet()) {
            String iataCode = airports.get(iata).iataCode;
            String name = airports.get(iata).name;
            String country = airports.get(iata).country;
            String region = airports.get(iata).region;
            data[dataIndex++] = new String[]{iataCode, name, country, region};
        }

        JScrollPane table = new TableBuilder(data, columnNames)
                .setFontSize(24)
                .setFont(BOLD_FONT)
                .setColumnWidth(new int[]{100, 500, 100, 100})
                .buildPane();
        table.setPreferredSize(new Dimension(Integer.MAX_VALUE, 800));
        table.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        this.add(table);

        JButton button1 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Analyse").setFontSize(35).build();
        JButton button2 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Map View").setFontSize(35).build();
        JButton button3 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Search").setFontSize(35).build();

        this.add(button1);
        this.add(button2);
        this.add(button3);
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
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
            int direction) {
        return 32;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 32;
    }
}
