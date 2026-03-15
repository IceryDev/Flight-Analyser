package com.still_processing;
import com.still_processing.Application.HomePage.HomePage;

import javax.swing.UIManager;

import javax.swing.BorderFactory;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * Application Entry point
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==== Flight Analyser Application ====");

        System.setProperty("sun.java2d.uiScale", "1");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.getDefaults().put("TableHeader.cellBorder", BorderFactory.createMatteBorder(5, 0, 5, 0, GRAY));
            UIManager.getDefaults().put("TableBody.cellBorder", BorderFactory.createMatteBorder(5, 0, 5, 0, GRAY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        new HomePage();
    }
}
