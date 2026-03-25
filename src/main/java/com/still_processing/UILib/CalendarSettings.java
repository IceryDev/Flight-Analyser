package com.still_processing.UILib;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.still_processing.DefaultSettings.Settings.*;
/**
 * @author Jessica Chen
 */
public class CalendarSettings extends DatePicker {

    public CalendarSettings() {
        super(createSettings());
        this.setDate(LocalDate.now());
        styleComponents();
        this.addDateChangeListener(event ->
                updateButtonLabel(this.getComponentToggleCalendarButton(), this.getDate())
        );
    }

    private static DatePickerSettings createSettings() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesBeforeCommonEra("dd/MM/yyyy");
        settings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        settings.setAllowEmptyDates(false);
        settings.setFontTodayLabel(REGULAR_FONT);
        settings.setFontCalendarWeekdayLabels(REGULAR_FONT);
        settings.setFontMonthAndYearMenuLabels(REGULAR_FONT);
        settings.setFontCalendarDateLabels(REGULAR_FONT);
        return settings;
    }

    private void styleComponents() {
        JTextField dateTextField = this.getComponentDateTextField();
        dateTextField.setVisible(false);
        dateTextField.setPreferredSize(new Dimension(0, 0));
        dateTextField.setMaximumSize(new Dimension(0, 0));
        JButton toggleButton = this.getComponentToggleCalendarButton();
        toggleButton.setBackground(HIGHLIGHT);
        toggleButton.setForeground(BACKGROUND);
        toggleButton.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 18));
        toggleButton.setOpaque(true);
        toggleButton.setBorderPainted(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        toggleButton.setFocusPainted(false);

        updateButtonLabel(toggleButton, this.getDate());
    }

    private void updateButtonLabel(JButton button, LocalDate date) {
        if (date != null) {
            button.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }
}