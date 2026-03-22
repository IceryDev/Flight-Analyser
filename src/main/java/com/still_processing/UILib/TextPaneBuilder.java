package com.still_processing.UILib;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Zhou Sun
 */
public class TextPaneBuilder {
    String text = "";
    int fontSize = 12;
    Font font = REGULAR_FONT;
    Color foreground = TEXT_COLOR;
    Color background = BACKGROUND;

    public JTextPane build() {
        JTextPane textPane = new JTextPane();

        // Disable Edits
        textPane.setEditable(false);
        textPane.getCaret().setVisible(false);
        textPane.setCaret(new DefaultCaret() {
            @Override
            public void paint(Graphics g) {
            }
        });

        // settings
        textPane.setText(text);
        textPane.setForeground(foreground);
        textPane.setBackground(background);
        textPane.setSelectionColor(HIGHLIGHT);
        textPane.setFont(font.deriveFont((float) fontSize));
        textPane.setOpaque(false);
        textPane.setSize(new Dimension(0, 0));

        return textPane;
    }

    public TextPaneBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public TextPaneBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public TextPaneBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public TextPaneBuilder setForeground(Color foreground) {
        this.foreground = foreground;
        return this;
    }

    public TextPaneBuilder setBackground(Color background) {
        this.background = background;
        return this;
    }
}
