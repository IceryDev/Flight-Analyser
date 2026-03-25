package com.still_processing.Application.HomePage;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.CalendarSettings;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.TextPaneBuilder;
import static com.still_processing.DefaultSettings.Settings.*;

import com.still_processing.UILib.CalendarSettings.*;

/**
 * @author Zhou Sun, Deea Zaharia
 */

/**
 * Added the calendar
 * @author Jessica Chen
 */
public class HomePage extends JPanel implements Scrollable {

    public HomePage(ActionListener sceneSwitch) {
        System.out.println("=== Home Page ===");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);
        this.add(Box.createRigidArea(new Dimension(0, 50)));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));

        ImagePanel logo = new ImagePanel("/Images/logo.png", 200, 200);
        String title = "Flight Analyser";
        JTextPane textPane = new TextPaneBuilder()
                .setText(title)
                .setFontSize(48)
                .setFont(BOLD_FONT)
                .build();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        FontMetrics metrics = getFontMetrics(BOLD_FONT.deriveFont(48f));
        int textHeight = metrics.getHeight() / 2 + metrics.getMaxAscent();
        int textWidth = metrics.stringWidth(title);
        textPane.setSize(new Dimension(textWidth, textHeight));
        textPane.setMaximumSize(new Dimension(textWidth, textHeight));

        titlePanel.setOpaque(false);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        this.add(titlePanel);

        this.add(Box.createRigidArea(new Dimension(0, 50)));

        CalendarSettings startPicker = new CalendarSettings();
        CalendarSettings endPicker = new CalendarSettings();

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

        JLabel startLabel = new JLabel("Departure");
        startLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 18));

        JLabel endLabel = new JLabel("Arrival");
        endLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 18));

        JPanel dateContainer = new JPanel();
        dateContainer.setLayout(new BoxLayout(dateContainer, BoxLayout.X_AXIS));
        dateContainer.setOpaque(false);

        dateContainer.add(Box.createRigidArea(new Dimension( Toolkit.getDefaultToolkit().getScreenSize().width /20, 0)));
        dateContainer.add(Box.createHorizontalGlue());
        dateContainer.add(startLabel);
        dateContainer.add(Box.createHorizontalGlue());
        dateContainer.add(startPicker);

        dateContainer.add(Box.createRigidArea(new Dimension(40, 0)));
        dateContainer.add(endLabel);
        dateContainer.add(endPicker);
        dateContainer.add(Box.createRigidArea(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width /20, 0)));
        dateContainer.add(Box.createHorizontalGlue());
        this.add(dateContainer);

        this.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton analyseButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Analyse")
                .setFontSize(18)
                .build();
        analyseButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton mapButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Map View")
                .setFontSize(18)
                .build();
        mapButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton searchButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Search")
                .setFontSize(18)
                .build();
        searchButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        analyseButton.addActionListener(sceneSwitch);
        mapButton.addActionListener(sceneSwitch);
        searchButton.addActionListener(sceneSwitch);

        Dimension buttonSize = analyseButton.getPreferredSize();
        startLabel.setPreferredSize(buttonSize);
        endLabel.setPreferredSize(buttonSize);


        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(analyseButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(40, 0)));
        buttonContainer.add(mapButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(40, 0)));
        buttonContainer.add(searchButton);
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.setOpaque(false);
        this.add(buttonContainer);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return null;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
            int direction) {
        return 32;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 32;
    }
}
