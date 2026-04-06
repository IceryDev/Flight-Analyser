package com.still_processing.Application.SearchPage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
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
import com.still_processing.UILib.*;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia, Jagoda Koczwara-Szuba, Jessica Chen
 * <br>
 *
 * Added icons and rounded borders to the input fields
 * @author Marco Fontana
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
    private boolean liveData = true;
    private ImagePanel notFoundImage;

    private JTextField originInput;
    private JTextField destInput;
    private CalendarSettings startPicker;
    private CalendarSettings endPicker;
    boolean isFound = true;
    JTextPane resultCount;

    private JTextPane pageDisplay;

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

        JButton homeButton = new RoundedButton("Return Home", 55, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
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

        Dimension inputFieldSize = new Dimension(300, 50);

        JLabel fromLabel = new JLabel("From");
        fromLabel.setPreferredSize(new Dimension(98, inputFieldSize.height));
        fromLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        fromLabel.setForeground(HIGHLIGHT);
        fromLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        fromLabel.setHorizontalAlignment(SwingConstants.LEFT);
        fromLabel.setVerticalAlignment(SwingConstants.CENTER);
        Image fromLabelIcon = new ImageIcon(getClass().getResource("/Images/plane-departing.png")).getImage();
        Image scaledFromLabelIcon = fromLabelIcon.getScaledInstance(inputFieldSize.height - 20, inputFieldSize.height - 20, Image.SCALE_SMOOTH);
        fromLabel.setIcon(new ImageIcon(scaledFromLabelIcon));

        JLabel destinationLabel = new JLabel("To");
        destinationLabel.setPreferredSize(new Dimension(130, inputFieldSize.height));
        destinationLabel.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 12));
        destinationLabel.setForeground(HIGHLIGHT);
        destinationLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        destinationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        destinationLabel.setVerticalAlignment(SwingConstants.CENTER);
        Image destinationLabelIcon = new ImageIcon(getClass().getResource("/Images/plane-landing.png")).getImage();
        Image scaledDestinationLabelIcon = destinationLabelIcon.getScaledInstance(inputFieldSize.height - 20, inputFieldSize.height - 20, Image.SCALE_SMOOTH);
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
        originInputContainer.add(originInput, BorderLayout.CENTER);

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
        destInputContainer.add(destInput, BorderLayout.CENTER);

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
        Image scaledStartLabelIcon = startLabelIcon.getScaledInstance(inputFieldSize.height - 20, inputFieldSize.height - 20, Image.SCALE_SMOOTH);
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
        Image scaledEndLabelIcon = endLabelIcon.getScaledInstance(inputFieldSize.height - 20, inputFieldSize.height - 20, Image.SCALE_SMOOTH);
        endLabel.setIcon(new ImageIcon(scaledEndLabelIcon));

        Dimension datePickerSize = new Dimension(300, 50);
        Border dateFieldChrome = BorderFactory.createCompoundBorder(
                RoundedHighlightBorder.getRoundedBorder(HIGHLIGHT_20),
                BorderFactory.createEmptyBorder(2, 4, 2, 14));

        JPanel startGroup = new JPanel(new BorderLayout(6, 0));
        startGroup.setBorder(dateFieldChrome);
        startGroup.setOpaque(false);
        startGroup.add(startLabel, BorderLayout.WEST);
        startGroup.add(startPicker, BorderLayout.CENTER);
        startGroup.setMaximumSize(datePickerSize);
        startGroup.setPreferredSize(datePickerSize);
        startGroup.setMinimumSize(datePickerSize);

        JPanel endGroup = new JPanel(new BorderLayout(6, 0));
        endGroup.setBorder(dateFieldChrome);
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

        JButton refineSearch = new RoundedButton("Refine Search", 55,HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        ((RoundedButton) refineSearch).setButtonIcon(new ImageIcon((getClass().getResource("/Images/plane-white.PNG"))), 18);
        refineSearch.addActionListener(e -> {
            updateSearch();
            counter = 0;
            refreshEntries();
        });
        refineSearch.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton graphButton = new RoundedButton("View Graph", 55,HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        ((RoundedButton) graphButton).setButtonIcon(new ImageIcon((getClass().getResource("/Images/graph.PNG"))), 18);
        graphButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        graphButton.addActionListener(sceneSwitch);

        String[] graphOptions = {"--Sort Option--", "Lateness - Ascending", "Lateness - Descending", "Distance - Ascending", "Distance - Descending"};

        JComboBox<String> sortDropDown = new DropdownBuilder(graphOptions)
                .setFontSize(18)
                .build();
        sortDropDown.setMaximumSize(new Dimension(240, 85));
        sortDropDown.setMinimumSize(new Dimension(240, 85));
        sortDropDown.setPreferredSize(new Dimension(240, 85));
        sortDropDown.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sortDropDown.addActionListener(this);

        FontMetrics pageFont = getFontMetrics(BOLD_FONT.deriveFont(22f));
        int pageTextHeight = pageFont.getHeight() / 2 + pageFont.getMaxAscent();
        String pageText = String.format("%d", (int) ((float) counter / 25) + 1);

        pageDisplay = new TextPaneBuilder()
                .setText(pageText)
                .setForeground(TEXT_COLOR)
                .setFont(BOLD_FONT)
                .setFontSize(22)
                .build();
        pageDisplay.setMaximumSize(new Dimension(pageFont.stringWidth(pageText), pageTextHeight));

        JButton previousButton = new RoundedButton("<", 20, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        previousButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        previousButton.addActionListener(e -> {
            counter -= (counter >= 25) ? 25 : 0;
            refreshEntries();
            pageDisplay.setText(String.format("%d", (int) ((float) counter / 25) + 1));
        });

        JButton previousPreviousButton = new RoundedButton("<<", 20, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        previousPreviousButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        previousPreviousButton.addActionListener(e -> {
            counter -= (counter >= 250) ? 250 : counter;
            counter = (counter < 0) ? 0 : counter;
            refreshEntries();
            pageDisplay.setText(String.format("%d", (int) ((float) counter / 25) + 1));
        });

        JButton nextButton = new RoundedButton(">", 20, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        nextButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nextButton.addActionListener(e -> {
            if (flightData != null)
                counter += (counter + 25 <= flightData.size()) ? 25 : 0;
            refreshEntries();
            pageDisplay.setText(String.format("%d", (int) ((float) counter / 25) + 1));
        });

        JButton nextNextButton = new RoundedButton(">>", 20, HIGHLIGHT, LIGHT_HIGHLIGHT, 18);
        nextNextButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        nextNextButton.addActionListener(e -> {
            if (flightData != null) {
                int remainingFlightCount = flightData.size() - counter;
                // this is a floor division, get the remainingFlightCount -
                // remainder
                int remainingJumpValue = (remainingFlightCount / 25) * 25;

                if (counter + remainingJumpValue > flightData.size() + 25)
                    remainingJumpValue = 0;

                counter += (counter + 250 <= flightData.size()) ? 250 : remainingJumpValue;
            }
            refreshEntries();
            pageDisplay.setText(String.format("%d", (int) ((float) counter / 25) + 1));
        });

        JButton liveDataButton = new RoundedButton("LIVE", 55, GRAY, LIME, 18);
        ((RoundedButton) liveDataButton).setButtonIcon(new ImageIcon((getClass().getResource("/Images/signal.PNG"))), 18);
        liveDataButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        liveDataButton.addActionListener(e -> {
            liveData = !liveData;
            if (liveData) {
                ((RoundedButton) liveDataButton).setBackgroundColor(GRAY.darker());
                ((RoundedButton) liveDataButton).setHoverColor(GRAY);
            }
            else{
                ((RoundedButton) liveDataButton).setBackgroundColor(LIVE_BUTTON_COLOR);
                ((RoundedButton) liveDataButton).setHoverColor(LIVE_BUTTON_COLOR_LIGHT);
            }

            Database.toggleSelectedFlights();
            updateSearch();
            refreshEntries();
        });

        String resultCountText = String.format("%d Result%s Found", flightData.size(), (flightData.size() == 1) ? "" : "s");
        resultCount = new TextPaneBuilder()
                .setText(resultCountText)
                .setFontSize(22)
                .setFont(BOLD_FONT)
                .build();
        resultCount.setMaximumSize(new Dimension(pageFont.stringWidth(resultCountText), pageTextHeight));

        JPanel buttonContainer = new JPanel();
        buttonContainer.setOpaque(false);
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(refineSearch);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(graphButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(sortDropDown);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(liveDataButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(resultCount);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(previousPreviousButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonContainer.add(previousButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(pageDisplay);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonContainer.add(nextButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonContainer.add(nextNextButton);
        buttonContainer.add(Box.createRigidArea(new Dimension(20, 0)));
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
        if (newData != null) {
            flightData = newData;
        }
    }

    public void updateSearch() {
        ArrayList<FlightInfo> flightList = Database.baseFlights;
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
        pageDisplay.setText(String.format("%d", (int) ((float) counter / 25) + 1));
        if (flightData != null && flightData.size() != 0) {
            resultCount.setText(String.format("%d Result%s Found", flightData.size(), (flightData.size() == 1) ? "" : "s"));
            for (int i = counter; i < (counter + 25); i++) {
                if (i >= flightData.size())
                    break;
                flightEntries.add(new ExpandablePanel(flightData.get(i)));
            }
        }
        else{
            resultCount.setText("No Results Found");
        }
        notFoundImage.setVisible(!this.isFound);
        flightEntries.revalidate();
        flightEntries.repaint();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> dropDown = (JComboBox<String>) e.getSource();
        dropDown.getParent().repaint();
        switch ((String) dropDown.getSelectedItem()) {
            case "--Sort Option--":
                break;
            case "Lateness - Ascending":
                flightData.sort((FlightInfo a, FlightInfo b) -> Float.compare(a.lateness, b.lateness));
                counter = 0;
                refreshEntries();
                break;
            case "Lateness - Descending":
                flightData.sort((FlightInfo a, FlightInfo b) -> Float.compare(a.lateness, b.lateness));
                Collections.reverse(flightData);
                counter = 0;
                refreshEntries();
                break;
            case "Distance - Ascending":
                flightData.sort((FlightInfo a, FlightInfo b) -> Float.compare(a.distance, b.distance));
                counter = 0;
                refreshEntries();
                break;
            case "Distance - Descending":
                flightData.sort((FlightInfo a, FlightInfo b) -> Float.compare(a.distance, b.distance));
                Collections.reverse(flightData);
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
