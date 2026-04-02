package com.still_processing.Application.SearchPage;

import static com.still_processing.DefaultSettings.Settings.BACKGROUND;
import static com.still_processing.DefaultSettings.Settings.BOLD_FONT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT_20;
import static com.still_processing.DefaultSettings.Settings.LIME;
import static com.still_processing.DefaultSettings.Settings.REGULAR_FONT;
import static javax.swing.SwingConstants.CENTER;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.FlightData.Airport;
import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Filters.Filter;
import com.still_processing.FlightData.Filters.FuzzySearch;
import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.CalendarSettings;
import com.still_processing.UILib.ExpandablePanel;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.InputFieldBuilder;
import com.still_processing.UILib.TextPaneBuilder;

/**
 * @author Deea Zaharia, Jagoda Koczwara-Szuba
 */

public class SearchPanel extends JPanel implements Scrollable, ActionListener {
    private JPanel flightEntries = new JPanel();
    private ArrayList<FlightInfo> flightData = Database.offlineFlights;
    private int counter = 0;
    private LocalDate startDate = LocalDate.parse("2022-01-01");
    private LocalDate endDate = LocalDate.now();
    private String originAirport;
    private String destAirport;
    private boolean sortOrderAscend = true;
    private ArrayList<String> airportList = new ArrayList<>();
    private ImagePanel notFoundImage;

    private JTextField originInput;
    private JTextField destInput;
    private CalendarSettings startPicker;
    private CalendarSettings endPicker;
    boolean isFound = true;

    public SearchPanel(ActionListener sceneSwitch) {

        System.out.println("=== Search Panel ===");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        ImagePanel logo = new ImagePanel("/Images/logo.png", 70, 70);
        String title = "Flight Analyser | Search";
        JTextPane textPane = new TextPaneBuilder()
                .setText(title)
                .setFontSize(36)
                .setFont(BOLD_FONT)
                .build();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        FontMetrics metrics = getFontMetrics(BOLD_FONT.deriveFont(36f));
        int textHeight = metrics.getHeight() / 2 + metrics.getMaxAscent();
        int textWidth = metrics.stringWidth(title);
        textPane.setSize(new Dimension(textWidth, textHeight));
        textPane.setMaximumSize(new Dimension(textWidth, textHeight));

        JButton homeButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Return Home")
                .setFontSize(18)
                .build();
        homeButton.addActionListener(sceneSwitch);
        homeButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(homeButton);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(titlePanel);

        this.add(Box.createRigidArea(new Dimension(0, 20)));

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
        inputFieldContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        inputFieldContainer.add(originInputContainer);
        inputFieldContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        inputFieldContainer.add(destInputContainer);
        inputFieldContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        inputFieldContainer.add(startGroup);
        inputFieldContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        inputFieldContainer.add(endGroup);
        inputFieldContainer.add(Box.createHorizontalGlue());
        this.add(inputFieldContainer);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton refineSearch = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Refine Search")
                .setFontSize(18)
                .build();
        refineSearch.addActionListener(e -> {
            updateSearch();
            counter = 0;
            refreshEntries();
        });
        refineSearch.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton graphButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("View Graph")
                .setFontSize(18)
                .build();
        graphButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        graphButton.addActionListener(sceneSwitch);

        JButton sortButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Sort By Lateness")
                .setFontSize(18)
                .build();
        sortButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        sortButton.addActionListener(this);

        JButton previousButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Previous")
                .setFontSize(18)
                .build();
        previousButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        previousButton.addActionListener(e -> {
            counter -= (counter >= 25) ? 25 : 0;
            refreshEntries();
        });

        JButton nextButton = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Next")
                .setFontSize(18)
                .build();
        nextButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        nextButton.addActionListener(e -> {
            if (flightData != null)
                counter += (counter + 25 <= flightData.size()) ? 25 : 0;
            refreshEntries();
        });

        JPanel buttonContainer = new JPanel();
        buttonContainer.setOpaque(false);
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(refineSearch);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(graphButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(sortButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(previousButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(nextButton);
        buttonContainer.add(Box.createHorizontalGlue());
        this.add(buttonContainer);

        flightEntries = new JPanel();
        flightEntries.setLayout(new BoxLayout(flightEntries, BoxLayout.Y_AXIS));
        notFoundImage = new ImagePanel("/Images/not-found-page.png", 900, 500);
        this.add(notFoundImage);

        updateFlightData(Database.offlineFlights);

        this.add(flightEntries);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        refreshEntries();

        for (Airport airport : Database.getAirports().values()) {
            airportList.add(airport.name);
        }
    }

    public void updateFlightData(ArrayList<FlightInfo> newData) {
        if (flightData != null) {
            flightData = newData;
        }
    }

    public void updateSearch() {
        ArrayList<FlightInfo> flightList = Database.offlineFlights;
        List<FlightInfo> filteredList = flightList;
        List<String> resultList;
        Filter filter = new Filter(flightList);
        if (originAirport != null && originAirport.length() != 0) {
            resultList = FuzzySearch.fuzzySearch(originAirport, airportList);
            for (int searchAttempts = 0; searchAttempts < 10; searchAttempts++) {
                filteredList = filter.byOriginAirport(resultList.get(searchAttempts));

                if (filteredList != null && filteredList.size() != 0)
                    break;
            }
        }

        if (destAirport != null && destAirport.length() != 0) {
            resultList = FuzzySearch.fuzzySearch(destAirport, airportList);
            filter = new Filter((ArrayList<FlightInfo>) filteredList);
            for (int searchAttempts = 0; searchAttempts < 10; searchAttempts++) {
                filteredList = filter.byDestAirport(resultList.get(searchAttempts));

                if (filteredList != null && filteredList.size() != 0)
                    if (filteredList.size() != 0)
                        break;
            }
        }

        if (startDate != null && endDate != null) {
            filter = new Filter((ArrayList<FlightInfo>) filteredList);
            filteredList = filter.byDateRange(startDate, endDate);
        }

        this.flightData = (ArrayList<FlightInfo>) filteredList;

        this.isFound = (!(filteredList == null || filteredList.isEmpty()));
        this.refreshEntries();

    }



    private void add(Image image) {
    }

    public void updateSearchBar() {
        originInput.setText(originAirport);
        destInput.setText(destAirport);
        startPicker.setDate(startDate);
        endPicker.setDate(endDate);
    }

    public void refreshEntries() {
        flightEntries.removeAll();
        if (flightData != null && flightData.size() != 0) {
            for (int i = counter; i < (counter + 25); i++) {
                if (i >= flightData.size())
                    break;
                flightEntries.add(new ExpandablePanel(flightData.get(i)));
            }
        }
        notFoundImage.setVisible(!this.isFound);
        flightEntries.revalidate();
        flightEntries.repaint();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Sort By Lateness":
                flightData.sort((FlightInfo a, FlightInfo b) -> Float.compare(a.lateness, b.lateness));
                if (!sortOrderAscend) {
                    Collections.reverse(flightData);
                }
                sortOrderAscend = !sortOrderAscend;
                counter = 0;
                refreshEntries();
                break;
        }
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
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

        return 32;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {

        return 32;
    }

    @Override
    public void scrollRectToVisible(Rectangle aRect) {
        // Do nothing — prevents auto-scroll when components are added
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
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public String getOriginAirport() {
        return this.originAirport;
    }

    public String getDestAirport() {
        return this.destAirport;
    }
}
