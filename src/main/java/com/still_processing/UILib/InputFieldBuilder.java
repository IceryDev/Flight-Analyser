package com.still_processing.UILib;

import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Color;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia
 */

public class InputFieldBuilder {

    private final int[] size = {200, 30};

    private int fontSize = FONT_SIZE;
    private Font font = REGULAR_FONT;
    private boolean italic = false;
    private boolean bold = false;

    private Color foreground = TEXT_COLOR;
    private Color background = BACKGROUND;

    private Color borderColor = GRAY;
    private int borderWidth = 0;

    private boolean hasBorder = false;

    public JTextField build() {
        JTextField tmpTextField = new JTextField();

        tmpTextField.setForeground(this.foreground);
        tmpTextField.setBackground(this.background);

        int style = Font.PLAIN;
        if (this.bold)
            style |= Font.BOLD;
        if (this.italic)
            style |= Font.ITALIC;
        tmpTextField.setFont(this.font.deriveFont(style, this.fontSize));

        tmpTextField.setPreferredSize(new Dimension(this.size[0], this.size[1]));

        if (hasBorder && borderWidth > 0) {
            Border b = BorderFactory.createLineBorder(this.borderColor, this.borderWidth);
            tmpTextField.setBorder(b);
        } else {
            tmpTextField.setBorder(null);
        }

        return tmpTextField;
    }

    public InputFieldBuilder setPreferredSize(int sizeX, int sizeY) {
        this.size[0] = sizeX;
        this.size[1] = sizeY;
        return this;
    }

    public InputFieldBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public InputFieldBuilder setBackground(Color background) {
        this.background = background;
        return this;
    }

    public InputFieldBuilder setForeground(Color foreground) {
        this.foreground = foreground;
        return this;
    }

    public InputFieldBuilder setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public InputFieldBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public InputFieldBuilder setBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
        return this;
    }

    public InputFieldBuilder setBorderWidth(int width) {
        this.borderWidth = width;
        return this;
    }

    public InputFieldBuilder setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public InputFieldBuilder setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

}


