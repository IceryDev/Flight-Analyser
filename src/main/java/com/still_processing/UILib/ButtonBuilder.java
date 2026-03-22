package com.still_processing.UILib;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Font;
import java.awt.Color;
import java.util.Objects;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * The {@code ButtonBuilder} class is used to build JButton objects.
 *
 * @author IceryDev (Ulaş İçer)
 */
public class ButtonBuilder {

    private final int[] size = new int[2];

    private int fontSize = FONT_SIZE;
    private Font font = REGULAR_FONT;
    private String text = "Text";
    private boolean italic = false;
    private boolean bold = false;

    private Color foreground = TEXT_COLOR;
    private Color background = BACKGROUND;

    private Color borderColor = GRAY;
    private int borderWidth = 0;

    private ImageIcon icon;

    private boolean hasBorder = false;

    public JButton build() {
        JButton tmpButton = new JButton();

        tmpButton.setText(this.text);
        tmpButton.setForeground(this.foreground);
        tmpButton.setBackground(this.background);

        int style = Font.PLAIN;
        if (this.bold)
            style |= Font.BOLD;
        if (this.italic)
            style |= Font.ITALIC;
        tmpButton.setFont(this.font.deriveFont(style, this.fontSize));

        tmpButton.setSize(this.size[0], this.size[1]);
        tmpButton.setIcon(this.icon);

        if (hasBorder) {
            Border b = BorderFactory.createLineBorder(this.borderColor, this.borderWidth);
        } else {
            tmpButton.setBorder(null);
        }

        return tmpButton;
    }

    public ButtonBuilder setSize(int sizeX, int sizeY) {
        this.size[0] = sizeX;
        this.size[1] = sizeY;
        return this;
    }

    public ButtonBuilder setIcon(String path) {
        this.icon = new ImageIcon( // Remove the double dot in build
                Objects.requireNonNull(ButtonBuilder.class.getResource(path)));
        return this;
    }

    public ButtonBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public ButtonBuilder setBackground(Color background) {
        this.background = background;
        return this;
    }

    public ButtonBuilder setForeground(Color foreground) {
        this.foreground = foreground;
        return this;
    }

    public ButtonBuilder setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public ButtonBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public ButtonBuilder setBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
        return this;
    }

    public ButtonBuilder setBorderWidth(int width) {
        this.borderWidth = width;
        return this;
    }

    public ButtonBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public ButtonBuilder setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public ButtonBuilder setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

}
