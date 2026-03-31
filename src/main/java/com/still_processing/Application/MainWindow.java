package com.still_processing.Application;

import static com.still_processing.DefaultSettings.Settings.BACKGROUND;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.still_processing.Application.AnalysisPage.AnalysisPanel;
import com.still_processing.Application.HomePage.HomePage;
import com.still_processing.Application.MapPage.MapPanel;
import com.still_processing.Application.SearchPage.SearchPanel;
import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Airport;
import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Filters.FilterApplier;
import com.still_processing.FlightData.Filters.FuzzySearch;
import com.still_processing.FlightData.Filters.impl.OriginAirportNameFilter;
import com.still_processing.FlightData.Filters.impl.DestinationAirportNameFilter;
import com.still_processing.FlightData.Utils.LiveDataHandler;
import com.still_processing.UILib.ScrollPaneFactory;

/**
 * @author Deea Zaharia, Jagoda Koczwara-Szuba, Zhou Sun
 */

public class MainWindow extends JFrame implements ActionListener {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);
    private AnalysisPanel analyse;
    private MapPanel map;
    private SearchPanel search;
    private HomePage body;
    private JScrollPane scrollPaneHome;
    private JScrollPane scrollPaneSearch;
    private JScrollPane scrollPaneAnalyse;
    private JScrollPane scrollPaneMap;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public MainWindow() {
        ImageIcon image = new ImageIcon(getClass().getResource("/Images/logo.png"));
        this.setIconImage(image.getImage());
        cards.setOpaque(false);

        Settings.setGlassPane((JPanel) this.getGlassPane());

        body = new HomePage(this);
        scrollPaneHome = ScrollPaneFactory.createPane();
        scrollPaneHome.setViewportView(body);
        scrollPaneHome.getViewport().setBackground(BACKGROUND);
        scrollPaneHome.setOpaque(false);
        cards.add(scrollPaneHome, "Main");

        search = new SearchPanel(this);
        scrollPaneSearch = ScrollPaneFactory.createPane();
        scrollPaneSearch.setViewportView(search);
        scrollPaneSearch.getViewport().setBackground(BACKGROUND);
        cards.add(scrollPaneSearch, "Search");

        analyse = new AnalysisPanel(this);
        scrollPaneAnalyse = ScrollPaneFactory.createPane();
        scrollPaneAnalyse.setViewportView(analyse);
        scrollPaneAnalyse.getViewport().setBackground(BACKGROUND);
        cards.add(scrollPaneAnalyse, "Analyse");

        map = new MapPanel(this);
        map.setBackground(BACKGROUND);
        cards.add(map, "Map");

        this.add(cards, BorderLayout.CENTER);

        this.setTitle("Flight Analyser");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(BACKGROUND);
        this.setSize(1024, 720);
        this.setMinimumSize(new Dimension(512, 512));
        this.setVisible(true);
    }

    /**
     * @author Deea Zaharia
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Home Page":
            case "Return Home":
                cardLayout.show(cards, "Main");
                LiveDataHandler.stopRefresh();
                break;
            case "Search":
                cardLayout.show(cards, "Search");
                search.setOriginAirport(body.getOriginAirport());
                search.setDestAirport(body.getDestAirport());
                search.setStartDate(body.getStartDate());
                search.setEndDate(body.getEndDate());

                search.updateSearch();
                search.updateSearchBar();
                search.refreshEntries();
                scrollPaneSearch.getVerticalScrollBar().setValue(0);
                break;
            case "Analyse":
            case "View Graph":
                cardLayout.show(cards, "Analyse");
                analyse.startRender();
                break;
            case "Map View":
                cardLayout.show(cards, "Map");
                LiveDataHandler.startRefresh();
                if (LiveDataHandler.mvf != null){
                    if (LiveDataHandler.mvf.getSelectedInfo() != null)
                        LiveDataHandler.mvf.getSelectedInfo().selected = false;
                    LiveDataHandler.mvf.setSelectedInfo(null);
                    LiveDataHandler.mvf.setLastSelected(null);
                }
                break;
        }
        Settings.getGlassPane().setVisible(false);
        cards.repaint();
        revalidate();
    }
}
