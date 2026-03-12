package com.still_processing.Application;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.*;
import java.awt.Component;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import static com.still_processing.DefaultSettings.Settings.*;

import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.TextPaneBuilder;

/**
 * @author Zhou Sun
 */
class BodyPanel extends JPanel implements Scrollable {

    public BodyPanel() {
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

        JPanel panel = new JPanel();
        JButton button1 = new JButton("Search");
        button1.setAlignmentX(Component.LEFT_ALIGNMENT);
        button1.setBackground(GRAY);
        button1.setSize(30, 30);

        JButton button2 = new ButtonBuilder().setSize(25,25).setFontSize(15).setBackground(HIGHLIGHT).setText("From").setBorderWidth(2).build();
        JButton button3 = new ButtonBuilder().setSize(25,25).setFontSize(15).setBackground(HIGHLIGHT).setText("To").setBorderWidth(2).build();
        panel.add(button2);
        panel.add(button3);
        panel.add(button1);
        this.add(textPane);
        this.add(panel);
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
