package com.still_processing.Application.MapPage;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Utils.LiveDataHandler;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.RoundedButton;
import com.still_processing.UILib.TextPaneBuilder;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * Draws the map page
 *
 * @author Deea Zaharia
 * @author Ulaş İçer
 */
public class MapPanel extends JPanel {

    private static final int MARGIN = 10;

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

        JButton homeButton = new RoundedButton("Return Home", 40, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        homeButton.setMinimumSize(new Dimension(215, 40));
        homeButton.setPreferredSize(new Dimension(215, 40));
        homeButton.setMaximumSize(new Dimension(215, 40));
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
        LiveDataHandler.resetRefresh();

        mapBorderLayout.add(LiveDataHandler.mvf);

        MapSideOverlay mso = new MapSideOverlay();
        JPanel glassPane = Settings.getGlassPane();
        glassPane.setLayout(null);
        glassPane.setOpaque(false);

        glassPane.add(mso);
        LiveDataHandler.sidebarOverlay = glassPane;
        LiveDataHandler.sidebar = mso;

        ComponentAdapter ca = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionSidebar();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                repositionSidebar(); // fires if layout shifts too
            }

            private void repositionSidebar() {
                Point p = SwingUtilities.convertPoint(mapBorderLayout, 0, 0, glassPane);
                int w = mapBorderLayout.getWidth() - 2 * MARGIN;
                int h = mapBorderLayout.getHeight() - 2 * MARGIN;
                mso.maxWidth = w / 4;
                mso.setBounds(p.x + MARGIN, p.y + MARGIN, mso.renderWidth, h);
                glassPane.repaint();
            }
        };
        LiveDataHandler.ca = ca;

        mapBorderLayout.addComponentListener(ca);

        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(titlePanel);
        this.add(mapBorderLayout);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

}
