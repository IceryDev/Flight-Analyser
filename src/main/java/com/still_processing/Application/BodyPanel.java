package com.still_processing.Application;

import java.awt.Rectangle;

import static com.still_processing.DefaultSettings.Settings.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Scrollable;

import com.still_processing.UILib.TextAreaBuilder;

class BodyPanel extends JPanel implements Scrollable {

    public BodyPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);

        JTextArea title = new TextAreaBuilder()
                .setText("Flight Analyser")
                .setFont(BOLD_FONT)
                .setFontSize(48)
                .build();
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createLineBorder(HIGHLIGHT, 5));

        this.add(title);

        JTextArea subtitle = new TextAreaBuilder()
                .setText("Flight Analyser")
                .setFont(BOLD_FONT)
                .setFontSize(48)
                .build();
        // title.setAlignmentX(CENTER_ALIGNMENT);
        // title.setBorder(BorderFactory.createLineBorder(HIGHLIGHT, 5));

        this.add(subtitle);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        int width = getParent() != null ? getParent().getWidth() : super.getPreferredSize().width;
        return new Dimension(width, getHeightForWidth(width));
    }

    private int getHeightForWidth(int width) {
        int totalHeight = 0;
        for (Component component : getComponents()) {
            // component.setSize(width, Integer.MAX_VALUE);
            component.setSize(width, Integer.MAX_VALUE);
            totalHeight += component.getPreferredSize().height;
        }
        return totalHeight;
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
        return 100;
    }
}
