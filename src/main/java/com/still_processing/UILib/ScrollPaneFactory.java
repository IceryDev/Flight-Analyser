package com.still_processing.UILib;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Zhou Sun
 */
public class ScrollPaneFactory {
    private static final int BAR_SIZE = 10;
    private static final int THUMB_SIZE = BAR_SIZE - 2;

    /**
     * Create a prestyled JScrollPane,
     * Horizontal scroll if needed
     * Vertical scroll if needed
     * 
     * @author Zhou Sun
     */
    public static JScrollPane createPane() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUI(creatScrollBar(0));
        scrollPane.getHorizontalScrollBar().setUI(creatScrollBar(1));
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(32);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(BAR_SIZE, Integer.MAX_VALUE));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(Integer.MAX_VALUE, BAR_SIZE));
        scrollPane.getViewport().setBackground(HIGHLIGHT);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        return scrollPane;
    }

    /**
     * Create a prestyled JScrollPane,
     * Horizontal scroll hidden
     * Vertical scroll hidden
     * 
     * @author Zhou Sun
     */
    public static JScrollPane createHideScroll() {
        JScrollPane scrollPane = createPane();
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        return scrollPane;
    }

    private static BasicScrollBarUI creatScrollBar(int type) {
        return new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = GRAY;
                this.trackColor = BACKGROUND;
            }

            @Override
            protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
                Graphics2D g2d = (Graphics2D) graphics.create();
                g2d.setClip(null);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(this.thumbColor);

                if (type == 0)
                    g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, THUMB_SIZE, thumbBounds.height, THUMB_SIZE,
                            THUMB_SIZE);
                else
                    g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, THUMB_SIZE, THUMB_SIZE,
                            THUMB_SIZE);

                g2d.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        };
    }
}
