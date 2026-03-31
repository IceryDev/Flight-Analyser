package com.still_processing.Application.MapPage;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.Utils.LiveDataHandler;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.TextPaneBuilder;
import static com.still_processing.DefaultSettings.Settings.BACKGROUND;
import static com.still_processing.DefaultSettings.Settings.BOLD_FONT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT;

/**
 * @author Deea Zaharia, Ulaş İçer
 */
public class MapPanel extends JPanel {

    public MapPanel(ActionListener sceneSwitch) {

        System.out.println("=== Map Panel ===");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        ImagePanel logo = new ImagePanel("/Images/logo.png", 50, 50);

        String title = "Flight Analyser | Map";
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

        JButton homeButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Return Home")
                .setFontSize(18)
                .build();
        homeButton.addActionListener(sceneSwitch);
        homeButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        titlePanel.setOpaque(false);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(homeButton);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel mapBorderLayout = new JPanel();
        mapBorderLayout.setLayout(new BoxLayout(mapBorderLayout, BoxLayout.Y_AXIS));
        mapBorderLayout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mapBorderLayout.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        System.setProperty("http.agent", "FlightAnalyser/1.0");
        LiveDataHandler.mvf = new MapViewFull(true);

        mapBorderLayout.add(LiveDataHandler.mvf);

        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(titlePanel);
        this.add(mapBorderLayout);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

}
