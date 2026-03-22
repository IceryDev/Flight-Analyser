package com.still_processing.Application.HomePage;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Scrollable;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.FlightData.Airport;
import com.still_processing.FlightData.CSVHandler;
import com.still_processing.FlightData.Database;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.TableBuilder;
import com.still_processing.UILib.TextPaneBuilder;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Zhou Sun, Deea Zaharia
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
            data[dataIndex++] = new String[] { iataCode, name, country, region };
        }

        JScrollPane table = new TableBuilder(data, columnNames)
                .setFontSize(24)
                .setFont(BOLD_FONT)
                .setColumnWidth(new int[] { 100, 500, 100, 100 })
                .buildPane();
        table.setPreferredSize(new Dimension(Integer.MAX_VALUE, 800));
        table.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        this.add(table);

        JButton button1 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Analyse")
                .setFontSize(35).build();
        JPanel button1Container = new JPanel();
        button1Container.setLayout(new BoxLayout(button1Container, BoxLayout.X_AXIS));
        button1Container.add(Box.createHorizontalGlue());
        button1Container.add(button1);
        button1Container.add(Box.createHorizontalGlue());
        button1Container.setOpaque(false);
        this.add(button1Container);

        JButton button2 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Map View")
                .setFontSize(35).build();
        JPanel button2Container = new JPanel();
        button2Container.setLayout(new BoxLayout(button2Container, BoxLayout.X_AXIS));
        button2Container.add(Box.createHorizontalGlue());
        button2Container.add(button2);
        button2Container.add(Box.createHorizontalGlue());
        button2Container.setOpaque(false);
        this.add(button2Container);

        JButton button3 = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Search").setFontSize(35)
                .build();
        JPanel button3Container = new JPanel();
        button3Container.setLayout(new BoxLayout(button3Container, BoxLayout.X_AXIS));
        button3Container.add(Box.createHorizontalGlue());
        button3Container.add(button3);
        button3Container.add(Box.createHorizontalGlue());
        button3Container.setOpaque(false);
        this.add(button3Container);

        button1.addActionListener(a);
        button2.addActionListener(a);
        button3.addActionListener(a);
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
