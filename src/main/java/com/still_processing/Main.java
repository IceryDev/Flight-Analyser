package com.still_processing;

import com.still_processing.Application.MainWindow;

/**
 * Hello world!
 *
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        System.setProperty("sun.java2d.uiScale", "1.5");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        new MainWindow();
    }
}
