package com.still_processing;

import static com.still_processing.DefaultSettings.Settings.GRAY;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.still_processing.Application.MainWindow;
import com.still_processing.FlightData.CSVHandler;
import com.still_processing.FlightData.FlightFetcher;

/**
 * Application Entry point
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==== Flight Analyser Application ====");
        CSVHandler.loadAirportCSV();
        CSVHandler.loadOfflineFlightCSV();
        FlightFetcher.getAirlineCodes();

        // OS specific settings
        String os = System.getProperty("os.name");
        if (os.equals("Mac OS X")) {
            System.setProperty("sun.java2d.uiScale", "2.0");
        } else {
            System.setProperty("sun.java2d.uiScale", "1.0");
        }
        if (os.equals("Linux")) {
            System.setProperty("sun.java2d.opengl", "true");
        }
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
