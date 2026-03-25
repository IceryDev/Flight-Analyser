package com.still_processing.Application.SearchPage;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Scrollable;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightInfo;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.ExpandablePanel;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.TextPaneBuilder;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia
 */

public class SearchPanel extends JPanel implements Scrollable, ActionListener {
    public SearchPanel(ActionListener sceneSwitch) {

        System.out.println("=== Search Panel ===");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        ImagePanel logo = new ImagePanel("/Images/logo.png", 70, 70);
        String title = "Flight Analyser | Search";
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

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        this.add(titlePanel);

        this.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton homeButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Return Home")
                .setFontSize(18)
                .build();
        homeButton.addActionListener(sceneSwitch);
        homeButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton graphButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("View Graph")
                .setFontSize(18)
                .build();
        graphButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        graphButton.addActionListener(sceneSwitch);

        JButton sortButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Sort By Lateness")
                .setFontSize(18)
                .build();
        sortButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        sortButton.addActionListener(this);

        JPanel buttonContainer = new JPanel();
        buttonContainer.setOpaque(false);
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(homeButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(graphButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(sortButton);
        buttonContainer.add(Box.createHorizontalGlue());
        this.add(buttonContainer);

        ArrayList<FlightInfo> flightData = Database.offlineFlights;
        for (int i = 0; i < 100; i++) {
            this.add(new ExpandablePanel(flightData.get(i)));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Sort By Lateness":
                System.out.println("Doing some rigorous sorting!!!");
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
