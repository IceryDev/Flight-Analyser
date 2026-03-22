package com.still_processing.Application.AnalysisPage;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Scrollable;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.FlightData.Database;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.DropdownBuilder;
import com.still_processing.UILib.Histogram;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.TextPaneBuilder;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia
 */

public class AnalysisPanel extends JPanel implements Scrollable, ActionListener {
    JPanel graphDisplay;
    Histogram histogram;
    Histogram latenessHistogram;
    Histogram distanceHistogram;
    CardLayout cardLayout;

    public AnalysisPanel(ActionListener a) {
        System.out.println("=== Analysis Panel ===");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel titlePanel = new JPanel();
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

        String[] graphOptions = { "--Select Option--", "poisson", "lateness", "distance" };
        JComboBox<String> dropDown = new DropdownBuilder(graphOptions)
                .setFontSize(18)
                .build();
        dropDown.setMaximumSize(new Dimension(70, 200));
        dropDown.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        dropDown.addActionListener(this);

        JButton button2 = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Home Page")
                .setFontSize(18)
                .build();
        button2.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        button2.addActionListener(a);

        titlePanel.setOpaque(false);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(dropDown);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(button2);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(titlePanel);

        final int graphHeight = 900;
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
        histogram.setPreferredSize(new Dimension(0, graphHeight));

        float[] latenessData = Database.getLateness(Database.offlineFlights);
        latenessHistogram = new Histogram(latenessData, 240, 1000);
        latenessHistogram.setPreferredSize(new Dimension(0, graphHeight));

        float[] distance = Database.getDistance(Database.offlineFlights);
        distanceHistogram = new Histogram(distance, 250, 200);
        distanceHistogram.setPreferredSize(new Dimension(0, graphHeight));

        cardLayout = new CardLayout();
        graphDisplay = new JPanel(cardLayout);
        graphDisplay.add(histogram, "poisson");
        graphDisplay.add(latenessHistogram, "lateness");
        graphDisplay.add(distanceHistogram, "distance");
        this.add(graphDisplay);
    }

    public void startRender() {
        histogram.animate();
        latenessHistogram.animate();
        distanceHistogram.animate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> dropDown = (JComboBox<String>) e.getSource();
        System.out.println(dropDown.getSelectedItem());
        dropDown.getParent().repaint();

        switch ((String) dropDown.getSelectedItem()) {
            case "--Select Option--":
                break;
            case "poisson":
                System.out.println("Poisson");

                cardLayout.show(graphDisplay, "poisson");
                histogram.animate();
                break;
            case "lateness":
                System.out.println("Late");

                cardLayout.show(graphDisplay, "lateness");
                latenessHistogram.animate();
                break;
            case "distance":
                System.out.println("Distance");
                cardLayout.show(graphDisplay, "distance");
                distanceHistogram.animate();
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
