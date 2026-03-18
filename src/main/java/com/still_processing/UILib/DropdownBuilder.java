package com.still_processing.UILib;

import static com.still_processing.DefaultSettings.Settings.*;

import javax.swing.JComboBox;

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;

/**
 * @author Jagoda Koczwara-Szuba
 */

public class DropdownBuilder {

    private final int[] size = new int[2];

    {
        size[1] = 50;
    }

    private Color background = BACKGROUND;
    private Color foreground = TEXT_COLOR;

    private int fontSize = FONT_SIZE;
    private Font font = REGULAR_FONT;

    boolean hasBorder = false;
    int borderWidth = 5;

    private String[] options;

    public JComboBox build() {

        JComboBox<String> dropdown = new JComboBox<>(options);       // options must be a String

        dropdown.setEditable(false);
        dropdown.setSize(size[0], size[1]);

        dropdown.setBackground(background);
        dropdown.setForeground(foreground);

        dropdown.setFont(this.font.deriveFont(Font.PLAIN, this.fontSize));

        if (hasBorder) {
            dropdown.setBorder(new LineBorder(Color.BLACK, borderWidth));
        }

        return dropdown;
    }

    public DropdownBuilder setSize(int sizeX, int sizeY) {
        this.size[0] = sizeX;
        this.size[1] = sizeY;
        return this;
    }

    public DropdownBuilder setBackground(Color background) {
        this.background = background;
        return this;
    }

    public DropdownBuilder setForeground(Color foreground) {
        this.foreground = foreground;
        return this;
    }

    public DropdownBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public DropdownBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public DropdownBuilder setBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
        return this;
    }

    public DropdownBuilder setBorderWidth(int width) {
        this.borderWidth = width;
        return this;
    }

    public DropdownBuilder setOptions(String[] options) {
        this.options = options;
        return this;
    }
}