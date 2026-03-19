package com.still_processing.Application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;

import com.still_processing.Application.AnalysisPage.AnalysisPanel;
import com.still_processing.Application.HomePage.BodyPanel;
import com.still_processing.Application.MapPage.MapPanel;
import com.still_processing.Application.SearchPage.SearchPanel;
import com.still_processing.UILib.ScrollPaneFactory;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia, Jagoda Koczwara-Szuba, Zhou Sun
 */

public class MainWindow extends JFrame implements ActionListener {
    CardLayout cardLayout = new CardLayout();
    Panel cards = new Panel(cardLayout);

    public MainWindow() {
        ImageIcon image = new ImageIcon(getClass().getResource("/Images/logo.jpg"));
        this.setIconImage(image.getImage());

        JPanel body = new BodyPanel(this);
        JScrollPane scrollPane = ScrollPaneFactory.createPane();
        scrollPane.setViewportView(body);
        scrollPane.getViewport().setBackground(BACKGROUND);
        cards.add(scrollPane, "Main");

        JPanel search = new SearchPanel(this);
        scrollPane = ScrollPaneFactory.createPane();
        scrollPane.setViewportView(search);
        scrollPane.getViewport().setBackground(BACKGROUND);
        cards.add(scrollPane, "Search");

        JPanel analyse = new AnalysisPanel(this);
        scrollPane = ScrollPaneFactory.createPane();
        scrollPane.setViewportView(analyse);
        scrollPane.getViewport().setBackground(BACKGROUND);
        cards.add(scrollPane, "Analyse");

        JPanel map = new MapPanel(this);
        scrollPane = ScrollPaneFactory.createPane();
        scrollPane.setViewportView(map);
        scrollPane.getViewport().setBackground(BACKGROUND);
        cards.add(scrollPane, "Map");

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
                cardLayout.show(cards, "Main");
                break;
            case "Search":
                cardLayout.show(cards, "Search");
                break;
            case "Analyse":
                cardLayout.show(cards, "Analyse");
                break;
            case "Map View":
                cardLayout.show(cards, "Map");
                break;
        }
    }
}
