package com.still_processing.UILib;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import static com.still_processing.DefaultSettings.Settings.*;

public class TextAreaBuilder {
    String text = "";
    int fontSize = 12;
    Font font = REGULAR_FONT;
    Color foreground = TEXT_COLOR;
    Color background = BACKGROUND;

    public JTextArea build() {
        JTextArea textArea = new JTextArea();

        // Disable Edits
        textArea.setEditable(false);
        textArea.getCaret().setVisible(false);
        textArea.setCaret(new DefaultCaret() {
            @Override
            public void paint(Graphics g) {
            }
        });

        // Textarea settings
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText(text);
        textArea.setForeground(foreground);
        textArea.setBackground(background);
        textArea.setSelectionColor(HIGHLIGHT);
        textArea.setFont(font.deriveFont((float) fontSize));
        textArea.setOpaque(false);
        textArea.setSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        return textArea;
    }

    public TextAreaBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public TextAreaBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public TextAreaBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public TextAreaBuilder setForeground(Color foreground) {
        this.foreground = foreground;
        return this;
    }

    public TextAreaBuilder setBackground(Color background) {
        this.background = background;
        return this;
    }
}
