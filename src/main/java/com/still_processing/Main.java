package com.still_processing;

import javax.swing.*;

import com.still_processing.FlightData.CSVHandler;
import com.still_processing.Application.MainWindow;
import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightFetcher;
import com.still_processing.FlightData.FlightInfo;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * Application Entry point
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==== Flight Analyser Application ====");
        CSVHandler.loadAirportCSV();
        CSVHandler.loadOfflineFlightCSV();
        FlightFetcher.getAirlineCodes();

        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.getDefaults().put("TableHeader.cellBorder", BorderFactory.createMatteBorder(5, 0, 5, 0, GRAY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
