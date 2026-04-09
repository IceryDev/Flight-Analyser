package com.still_processing.UILib;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * creates a Custom DatePciker with some default setups
 * and updates the label
 *
 * @author Jessica Chen
 */
public class CalendarSettings extends DatePicker {

    public CalendarSettings() {
        super(createSettings());
        this.setDate(LocalDate.now());
        styleComponents();
        this.addDateChangeListener(event -> updateButtonLabel(this.getComponentToggleCalendarButton(), this.getDate()));
    }

    private static DatePickerSettings createSettings() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesBeforeCommonEra("dd/MM/yyyy");
        settings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        settings.setAllowEmptyDates(false);
        settings.setFontTodayLabel(REGULAR_FONT.deriveFont(12f));
        settings.setFontCalendarWeekdayLabels(REGULAR_FONT.deriveFont(12f));
        settings.setFontMonthAndYearMenuLabels(REGULAR_FONT.deriveFont(12f));
        settings.setFontCalendarDateLabels(REGULAR_FONT.deriveFont(12f));
        settings.setColor(DatePickerSettings.DateArea.CalendarDefaultBackgroundHighlightedDates, HIGHLIGHT_20);
        return settings;
    }

    private void styleComponents() {
        JTextField dateTextField = this.getComponentDateTextField();
        dateTextField.setVisible(false);
        dateTextField.setPreferredSize(new Dimension(0, 0));
        dateTextField.setMaximumSize(new Dimension(0, 0));

        JButton toggleButton = this.getComponentToggleCalendarButton();
        setOpaque(false);
        toggleButton.setForeground(HIGHLIGHT);
        toggleButton.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        toggleButton.setOpaque(true);
        toggleButton.setBorderPainted(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        toggleButton.setFocusPainted(false);

        for (Component c : this.getComponents()) {
            if (c != null)
                c.setBackground(LIME);
        }

        updateButtonLabel(toggleButton, this.getDate());
    }

    private void updateButtonLabel(JButton button, LocalDate date) {
        if (date != null) {
            button.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }
}
