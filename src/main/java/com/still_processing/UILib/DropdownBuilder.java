package com.still_processing.UILib;

import javax.accessibility.Accessible;
import javax.swing.JComboBox;

import static com.still_processing.DefaultSettings.Settings.*;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

/**
 * @author Jagoda Koczwara-Szuba
 */

//public class JComboBox<E> extends JComponent
//        implements ItemSelectable, ListDataListener,ActionListener, Accessible {
//        }

//extends JComponent implements ItemSelectable, ListDataListener,ActionListener, Accessible

public class DropdownBuilder {

    private final int[] size = new int[2];

    private Color background = BACKGROUND;
    private Color foreground = TEXT_COLOR;

    private int fontSize = FONT_SIZE;
    private Font font = REGULAR_FONT;

    private Vector<String> options = new Vector<>();

    private ActionListener listener;

    public JComboBox build() {

        JComboBox<String> dropdown = new JComboBox<>(options);       // options must be a String

        dropdown.setEditable(false);
        dropdown.setSize(size[0], size[1]);

        dropdown.setBackground(background);
        dropdown.setForeground(foreground);

        dropdown.setFont(this.font.deriveFont(Font.PLAIN, this.fontSize));

        dropdown.setMaximumRowCount(10);

        if (listener != null) {
            dropdown.addActionListener(e -> {
                JComboBox source = (JComboBox) e.getSource();
                System.out.println("Selected: " + source.getSelectedItem());
            });
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

    public DropdownBuilder setOptions(Vector<String> options) {
        this.options = options;
        return this;
    }

    public DropdownBuilder select(ActionListener listener) {
        this.listener = listener;
        return this;
    }
}
