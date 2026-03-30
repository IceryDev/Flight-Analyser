package com.still_processing;

import static com.still_processing.DefaultSettings.Settings.GRAY;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                FlightFetcher.fetchLiveFlightInfo(100);
                return null;
            }
        }.execute();

        System.setProperty("sun.java2d.uiScale", "1.0");
        // System.setProperty("sun.java2d.opengl", "true");
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
