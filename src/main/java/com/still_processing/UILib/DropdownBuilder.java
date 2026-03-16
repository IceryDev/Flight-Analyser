package com.still_processing.UILib;

import javax.accessibility.Accessible;
import javax.swing.JComboBox;

import static com.still_processing.DefaultSettings.Settings.*;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jagoda Koczwara-Szuba
 */

//public class JComboBox<E> extends JComponent
//        implements ItemSelectable, ListDataListener,ActionListener, Accessible {
//        }

//extends JComponent implements ItemSelectable, ListDataListener,ActionListener, Accessible

public class DropdownBuilder {

    private final int[] size = new int[2];

    private int fontSize = FONT_SIZE;
    private Font font = REGULAR_FONT;

    private String[] options = {"first", "second", "third"};

    private Color foreground = TEXT_COLOR;
    private Color background = BACKGROUND;

    private ActionListener listener;

    public JComboBox build() {

//        JFrame frame = new JFrame();

        JComboBox<String> dropdown = new JComboBox<>(options);

//        dropdown.setEnabled(false);
        dropdown.setEditable(false);
        dropdown.setSize(size[0], size[1]);

        dropdown.setFont(this.font.deriveFont(Font.PLAIN, this.fontSize));

        if (listener != null) {
            dropdown.addActionListener(e -> {
                JComboBox source = (JComboBox) e.getSource();
                System.out.println("Selected: " + source.getSelectedItem());
            });
        }

//        dropdown.addActionListener(e -> {
//            String selected = (String) dropdown.getSelectedItem();
//        });          ??? Why not this?

//        frame.add(dropdown);
//        frame.setSize(size[0], size[1]);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);

        return dropdown;
    }

    public DropdownBuilder setOptions() {
        // ?????
    }

    public DropdownBuilder setSize(int sizeX, int sizeY) {
        this.size[0] = sizeX;
        this.size[1] = sizeY;
        return this;
    }

    public DropdownBuilder setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public DropdownBuilder setBackground(Color background) {
        this.background = background;
        return this;
    }

    public DropdownBuilder setFont(Font font) {
        this.font = font;
        return this;
    }

    public DropdownBuilder setForeground(Color foreground) {
        this.foreground = foreground;
        return this;
    }

    public DropdownBuilder select(ActionListener listener) {
        this.listener = listener;
        return this;
    }
}
