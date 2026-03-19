package com.still_processing.Application.MapPage;

import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.TextPaneBuilder;

import javax.swing.*;
import java.awt.Component;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.still_processing.DefaultSettings.Settings.*;

import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.TextPaneBuilder;

/**
 * @author Deea Zaharia
 */
public class MapPanel extends JPanel implements Scrollable {
    public MapPanel(ActionListener a) {

        System.out.println("=== Map Panel ===");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);

        String title = "Map View";
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

        JPanel panel = new JPanel();
        JButton button1 = new JButton("Search");
        button1.setAlignmentX(Component.LEFT_ALIGNMENT);
        button1.setBackground(GRAY);
        button1.setSize(30, 30);

        JButton button2 = new ButtonBuilder().setSize(25, 25).setFontSize(15).setBackground(HIGHLIGHT).setText("From")
                .setBorderWidth(2).build();
        JButton button3 = new ButtonBuilder().setSize(25, 25).setFontSize(15).setBackground(HIGHLIGHT).setText("To")
                .setBorderWidth(2).build();
        panel.add(button2);
        panel.add(button3);
        panel.add(button1);
        this.add(panel);

        JButton button = new ButtonBuilder().setSize(25, 25).setBackground(HIGHLIGHT).setText("Home Page")
                .setFontSize(35).build();
        this.add(button);
        button.addActionListener(a);
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
