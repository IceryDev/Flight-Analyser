package com.still_processing.Application;

import javax.swing.JFrame;

public class MainWindow extends JFrame {

    public MainWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Flight Analyser");
        this.setSize(1024, 720);
        this.setVisible(true);
    }
}
