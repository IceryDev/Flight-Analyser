package com.still_processing.Application.HomePage;

import static com.still_processing.DefaultSettings.Settings.BACKGROUND;
import static com.still_processing.DefaultSettings.Settings.BOLD_FONT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT_20;
import static com.still_processing.DefaultSettings.Settings.LIME;
import static com.still_processing.DefaultSettings.Settings.REGULAR_FONT;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.CalendarSettings;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.InputFieldBuilder;
import com.still_processing.UILib.RoundedHighlightBorder;
import com.still_processing.UILib.TextPaneBuilder;
import com.still_processing.UILib.RoundedButton;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Zhou Sun
 * @author Deea Zaharia
 * @author Marco Fontana
 * @author Jessica Chen
 */
public class HomePage extends JPanel implements Scrollable {
    private LocalDate startDate = LocalDate.parse("2022-01-01");
    private LocalDate endDate = LocalDate.now();
    private String originAirport;
    private String destAirport;

    private JTextField originInput;
    private JTextField destInput;
    private CalendarSettings startPicker;
    private CalendarSettings endPicker;

    public HomePage(ActionListener sceneSwitch) {
        System.out.println("=== Home Page ===");

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

        Dimension inputFieldSize = new Dimension(300, 50);

        JLabel fromLabel = new JLabel("From");
        fromLabel.setPreferredSize(new Dimension(100, inputFieldSize.height));
        fromLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        fromLabel.setForeground(HIGHLIGHT);
        fromLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        fromLabel.setHorizontalAlignment(SwingConstants.LEFT);
        fromLabel.setVerticalAlignment(SwingConstants.CENTER);
        Image fromLabelIcon = new ImageIcon(getClass().getResource("/Images/plane-departing.png")).getImage();
        Image scaledFromLabelIcon = fromLabelIcon.getScaledInstance(inputFieldSize.height - 20,
                inputFieldSize.height - 20, Image.SCALE_SMOOTH);
        fromLabel.setIcon(new ImageIcon(scaledFromLabelIcon));

        JLabel destinationLabel = new JLabel("To");
        destinationLabel.setPreferredSize(new Dimension(80, inputFieldSize.height));
        destinationLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        destinationLabel.setForeground(HIGHLIGHT);
        destinationLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        destinationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        destinationLabel.setVerticalAlignment(SwingConstants.CENTER);
        Image destinationLabelIcon = new ImageIcon(getClass().getResource("/Images/plane-landing.png")).getImage();
        Image scaledDestinationLabelIcon = destinationLabelIcon.getScaledInstance(inputFieldSize.height - 20,
                inputFieldSize.height - 20, Image.SCALE_SMOOTH);
        destinationLabel.setIcon(new ImageIcon(scaledDestinationLabelIcon));

        JPanel originInputContainer = new JPanel(new BorderLayout(6, 0));
        originInputContainer.setBorder(RoundedHighlightBorder.getRoundedBorder(HIGHLIGHT_20));
        originInputContainer.setOpaque(false);
        originInputContainer.setMaximumSize(inputFieldSize);
        originInputContainer.setPreferredSize(inputFieldSize);
        originInputContainer.setMinimumSize(inputFieldSize);
        originInputContainer.setBackground(LIME);
        originInputContainer.add(fromLabel, BorderLayout.WEST);

        originInput = new InputFieldBuilder()
                .setFont(REGULAR_FONT)
                .setForeground(HIGHLIGHT)
                .build();
        originInput.setOpaque(false);
        originInput.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 12));
        originInputContainer.add(originInput, BorderLayout.CENTER);
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

        JPanel destInputContainer = new JPanel(new BorderLayout(6, 0));
        destInputContainer.setBorder(RoundedHighlightBorder.getRoundedBorder(HIGHLIGHT_20));
        destInputContainer.setOpaque(false);
        destInputContainer.setMaximumSize(inputFieldSize);
        destInputContainer.setPreferredSize(inputFieldSize);
        destInputContainer.setMinimumSize(inputFieldSize);
        destInputContainer.setBackground(LIME);
        destInputContainer.add(destinationLabel, BorderLayout.WEST);

        destInput = new InputFieldBuilder()
                .setFont(REGULAR_FONT)
                .setForeground(HIGHLIGHT)
                .build();
        destInput.setOpaque(false);
        destInput.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 12));
        destInputContainer.add(destInput, BorderLayout.CENTER);
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

        startPicker.setDate(startDate);
        endPicker.setDate(endDate);
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

        int dateLabelColumnWidth = 130;
        JLabel startLabel = new JLabel("Depart");
        startLabel.setPreferredSize(new Dimension(dateLabelColumnWidth, inputFieldSize.height));
        startLabel.setMinimumSize(new Dimension(dateLabelColumnWidth, inputFieldSize.height));
        startLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        startLabel.setForeground(HIGHLIGHT);
        startLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        startLabel.setHorizontalAlignment(SwingConstants.LEFT);
        startLabel.setVerticalAlignment(SwingConstants.CENTER);
        Image startLabelIcon = new ImageIcon(getClass().getResource("/Images/calendar.png")).getImage();
        Image scaledStartLabelIcon = startLabelIcon.getScaledInstance(inputFieldSize.height - 20,
                inputFieldSize.height - 20, Image.SCALE_SMOOTH);
        startLabel.setIcon(new ImageIcon(scaledStartLabelIcon));

        JLabel endLabel = new JLabel("Return");
        endLabel.setPreferredSize(new Dimension(dateLabelColumnWidth, inputFieldSize.height));
        endLabel.setMinimumSize(new Dimension(dateLabelColumnWidth, inputFieldSize.height));
        endLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        endLabel.setForeground(HIGHLIGHT);
        endLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        endLabel.setHorizontalAlignment(SwingConstants.LEFT);
        endLabel.setVerticalAlignment(SwingConstants.CENTER);
        Image endLabelIcon = new ImageIcon(getClass().getResource("/Images/calendar.png")).getImage();
        Image scaledEndLabelIcon = endLabelIcon.getScaledInstance(inputFieldSize.height - 20,
                inputFieldSize.height - 20, Image.SCALE_SMOOTH);
        endLabel.setIcon(new ImageIcon(scaledEndLabelIcon));

        Dimension datePickerSize = new Dimension(300, 50);
        Border dateFieldBorder = BorderFactory.createCompoundBorder(
                RoundedHighlightBorder.getRoundedBorder(HIGHLIGHT_20),
                BorderFactory.createEmptyBorder(2, 4, 2, 14));

        JPanel startGroup = new JPanel(new BorderLayout(6, 0));
        startGroup.setBorder(dateFieldBorder);
        startGroup.setOpaque(false);
        startGroup.add(startLabel, BorderLayout.WEST);
        startGroup.add(startPicker, BorderLayout.CENTER);
        startGroup.setMaximumSize(datePickerSize);
        startGroup.setPreferredSize(datePickerSize);
        startGroup.setMinimumSize(datePickerSize);

        JPanel endGroup = new JPanel(new BorderLayout(6, 0));
        endGroup.setBorder(dateFieldBorder);
        endGroup.setOpaque(false);
        endGroup.add(endLabel, BorderLayout.WEST);
        endGroup.add(endPicker, BorderLayout.CENTER);
        endGroup.setMaximumSize(datePickerSize);
        endGroup.setPreferredSize(datePickerSize);
        endGroup.setMinimumSize(datePickerSize);
        endGroup.setBackground(HIGHLIGHT_20);

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

        Dimension buttonSize = new Dimension(200, 40);
        JButton mapButton = new RoundedButton("Map View", 55, HIGHLIGHT, LIGHT_HIGHLIGHT, 17);
        ((RoundedButton) mapButton).setButtonIcon(new ImageIcon((getClass().getResource("/Images/map.PNG"))), 14);
        mapButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        mapButton.setMaximumSize(buttonSize);
        mapButton.setPreferredSize(buttonSize);
        mapButton.setMinimumSize(buttonSize);

        JButton searchButton = new RoundedButton("Search", 55, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        ((RoundedButton) searchButton).setButtonIcon(new ImageIcon((getClass().getResource("/Images/plane-white.PNG"))),
                18);
        searchButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        searchButton.setMaximumSize(buttonSize);
        searchButton.setPreferredSize(buttonSize);
        searchButton.setMinimumSize(buttonSize);

        mapButton.addActionListener(sceneSwitch);
        searchButton.addActionListener(sceneSwitch);

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(searchButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(mapButton);
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.setOpaque(false);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);
        this.add(Box.createRigidArea(new Dimension(0, 200)));
        this.add(titlePanel);
        this.add(Box.createRigidArea(new Dimension(0, 50)));
        this.add(inputFieldContainer);
        this.add(Box.createRigidArea(new Dimension(0, 50)));
        this.add(buttonContainer);
        this.add(Box.createVerticalGlue());
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
        return true;
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
