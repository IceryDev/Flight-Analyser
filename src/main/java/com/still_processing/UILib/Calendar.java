package com.still_processing.UILib;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static com.still_processing.DefaultSettings.Settings.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Calendar extends JPanel {

    public Calendar(){
        setLayout(new BorderLayout(10, 10));
        setSize(new Dimension(400, 450));
        buildCalendarView();

        setVisible(true);
    }

    private void buildCalendarView(){
        DatePickerSettings startSettings = createDefaultSettings();
        DatePickerSettings endSettings = createDefaultSettings();
        DatePicker startPicker = new DatePicker(startSettings);
        DatePicker endPicker = new DatePicker(endSettings);

        startPicker.setDate(LocalDate.now());
        endPicker.setDate(LocalDate.now());

        startPicker.addDateChangeListener(event -> {
            LocalDate start = startPicker.getDate();
            if(start != null){
                endPicker.getSettings().setDateRangeLimits(start, null);
                if(endPicker.getDate() != null && endPicker.getDate().isBefore(start)){
                    endPicker.setDate(start);
                }
            }
        });

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JLabel startLabel = new JLabel("Start Date");
        startPanel.add(startLabel);
        startPanel.add(startPicker);

        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JLabel endLabel = new JLabel("End Date");
        endPanel.add(endLabel);
        endPanel.add(endPicker);

        add(startPanel, BorderLayout.NORTH);
        add(endPanel, BorderLayout.CENTER);
    }

    private DatePickerSettings createDefaultSettings(){
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesBeforeCommonEra("dd/MM/yyyy");
        settings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        settings.setAllowEmptyDates(false);
        return settings;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.add(new Calendar());
            frame.pack();
            frame.setVisible(true);
        });
    }

}
