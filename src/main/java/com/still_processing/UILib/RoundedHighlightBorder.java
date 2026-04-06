package com.still_processing.UILib;

import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

/**
 * Utility class for creating a rounded border with a highlight effect.
 *
 * @author Marco Fontana
 */
public final class RoundedHighlightBorder {

    private RoundedHighlightBorder() {}

    public static AbstractBorder getRoundedBorder(Color color) {
        return new AbstractBorder() {
            @Override
            public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) graphics.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(x, y, width - 1, height - 1, 10, 10);
                g2d.setColor(HIGHLIGHT);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(x, y, width - 1, height - 1, 10, 10);
                g2d.dispose();
            }
        };
    }
}
