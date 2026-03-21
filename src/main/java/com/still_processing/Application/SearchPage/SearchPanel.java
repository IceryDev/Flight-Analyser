package com.still_processing.Application.SearchPage;

import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.TextPaneBuilder;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ActionListener;

import static com.still_processing.DefaultSettings.Settings.BOLD_FONT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT;

/**
 * @author Deea Zaharia
 */

public class SearchPanel extends JPanel implements Scrollable {
    public SearchPanel(ActionListener a) {

        System.out.println("=== Search Panel ===");

        String title = "Search";
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

        JButton button = new ButtonBuilder()
                .setSize(25, 25)
                .setBackground(HIGHLIGHT)
                .setText("Analyse")
                .setFontSize(35)
                .build();
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
