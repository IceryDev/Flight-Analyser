package com.still_processing.Application;

import javax.swing.*;

import java.awt.*;

import static com.still_processing.DefaultSettings.Settings.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Flight Analyser");
        this.setSize(1024, 720);
        this.setVisible(true);
        this.getContentPane().setBackground(BACKGROUND);

        this.setLayout(new FlowLayout());
        JLabel label = new JLabel();
        label.setText("Flight Analyser");
        label.setFont(BOLD_FONT.deriveFont(50f));
        this.add(label);
    }
}
