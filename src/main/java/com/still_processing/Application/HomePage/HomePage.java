package com.still_processing.Application.HomePage;

import static com.still_processing.DefaultSettings.Settings.BACKGROUND;
import static com.still_processing.DefaultSettings.Settings.BOLD_FONT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT_20;
import static com.still_processing.DefaultSettings.Settings.LIME;
import static com.still_processing.DefaultSettings.Settings.REGULAR_FONT;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Scrollable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.CalendarSettings;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.InputFieldBuilder;
import com.still_processing.UILib.TextPaneBuilder;

/**
 * @author Zhou Sun, Deea Zaharia
 */

/**
 * Added the calendar
 * 
 * @author Jessica Chen
 */
public class HomePage extends JPanel implements Scrollable {
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now();
    private String originAirport;
    private String destAirport;

    private JTextField originInput;
    private JTextField destInput;
    private CalendarSettings startPicker;
    private CalendarSettings endPicker;

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
        textPane.setMaximumSize(new Dimension(textWidth, textHeight));

        titlePanel.setOpaque(false);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        this.add(titlePanel);

        this.add(Box.createRigidArea(new Dimension(0, 50)));

        Dimension inputFieldSize = new Dimension(350, 50);
        JPanel originInputContainer = new JPanel();
        originInputContainer.setBorder(BorderFactory.createLineBorder(HIGHLIGHT, 2));
        originInputContainer.setMaximumSize(inputFieldSize);
        originInputContainer.setPreferredSize(inputFieldSize);
        originInputContainer.setMinimumSize(inputFieldSize);
        originInputContainer.setBackground(LIME);

        originInput = new InputFieldBuilder()
                .setFont(REGULAR_FONT)
                .setForeground(HIGHLIGHT)
                .build();
        originInput.setOpaque(false);
        originInput.setMaximumSize(inputFieldSize);
        originInput.setPreferredSize(inputFieldSize);
        originInput.setMinimumSize(inputFieldSize);
        originInput.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 20));
        originInputContainer.add(originInput);
        originInput.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                sync(e);
            }

            public void removeUpdate(DocumentEvent e) {
                sync(e);
            }

            public void insertUpdate(DocumentEvent e) {
                sync(e);
            }

            private void sync(DocumentEvent e) {
                originAirport = originInput.getText();
            }
        });

        JPanel destInputContainer = new JPanel();
        destInputContainer.setBorder(BorderFactory.createLineBorder(HIGHLIGHT, 2));
        destInputContainer.setMaximumSize(inputFieldSize);
        destInputContainer.setPreferredSize(inputFieldSize);
        destInputContainer.setMinimumSize(inputFieldSize);
        destInputContainer.setBackground(LIME);

        destInput = new InputFieldBuilder()
                .setFont(REGULAR_FONT)
                .setForeground(HIGHLIGHT)
                .build();
        destInput.setOpaque(false);
        destInput.setMaximumSize(inputFieldSize);
        destInput.setPreferredSize(inputFieldSize);
        destInput.setMinimumSize(inputFieldSize);
        destInput.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 20));
        destInputContainer.add(destInput);
        destInput.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                sync(e);
            }

            public void removeUpdate(DocumentEvent e) {
                sync(e);
            }

            public void insertUpdate(DocumentEvent e) {
                sync(e);
            }

            private void sync(DocumentEvent e) {
                destAirport = destInput.getText();
            }
        });

        startPicker = new CalendarSettings();
        endPicker = new CalendarSettings();

        startPicker.setDate(LocalDate.now());
        endPicker.setDate(LocalDate.now());
        startPicker.setOpaque(false);
        startPicker.addDateChangeListener(event -> {
            LocalDate start = startPicker.getDate();
            if (start != null) {
                endPicker.getSettings().setDateRangeLimits(start, null);
                if (endPicker.getDate() != null && endPicker.getDate().isBefore(start)) {
                    endPicker.setDate(start);
                }
                startDate = start;
            }
        });
        endPicker.addDateChangeListener(event -> {
            LocalDate end = endPicker.getDate();
            if (end != null) {
                endDate = end;
            }
        });

        JLabel startLabel = new JLabel("Departure");
        startLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        startLabel.setForeground(HIGHLIGHT);
        startLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 20));

        JLabel endLabel = new JLabel("Arrival");
        endLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        endLabel.setForeground(HIGHLIGHT);
        endLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 20));

        JPanel startGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        startGroup.setBackground(HIGHLIGHT_20);
        startGroup.setBorder(BorderFactory.createLineBorder(HIGHLIGHT, 2));
        startGroup.add(startLabel);
        startGroup.add(startPicker);
        startGroup.setMaximumSize(new Dimension(500, 100));

        JPanel endGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        endGroup.setBackground(HIGHLIGHT_20);
        endGroup.setBorder(BorderFactory.createLineBorder(HIGHLIGHT, 2));
        endGroup.add(endLabel);
        endGroup.add(endPicker);
        endGroup.setMaximumSize(new Dimension(500, 100));

        JPanel inputFieldContainer = new JPanel();
        inputFieldContainer.setLayout(new BoxLayout(inputFieldContainer, BoxLayout.X_AXIS));
        inputFieldContainer.setOpaque(false);
        inputFieldContainer.add(Box.createHorizontalGlue());
        inputFieldContainer.add(originInputContainer);
        inputFieldContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        inputFieldContainer.add(destInputContainer);
        inputFieldContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        inputFieldContainer.add(startGroup);
        inputFieldContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        inputFieldContainer.add(endGroup);
        inputFieldContainer.add(Box.createHorizontalGlue());
        this.add(inputFieldContainer);

        this.add(Box.createRigidArea(new Dimension(0, 80)));

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

        mapButton.addActionListener(sceneSwitch);
        searchButton.addActionListener(sceneSwitch);

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(searchButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(40, 0)));
        buttonContainer.add(mapButton);
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.setOpaque(false);
        this.add(buttonContainer);
    }

    public void updateSearchBar() {
        originInput.setText(originAirport);
        destInput.setText(destAirport);
        startPicker.setDate(startDate);
        endPicker.setDate(endDate);
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

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }

    public void setDestAirport(String destAirport) {
        this.destAirport = destAirport;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getOriginAirport() {
        return originAirport;
    }

    public String getDestAirport() {
        return destAirport;
    }
}
