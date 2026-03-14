package com.still_processing.Application.HomePage;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;

import com.still_processing.Application.HomePage.BodyPanel
import com.still_processing.UILib.ScrollPaneFactory;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Jagoda Koczwara-Szuba (Zhou Sun)
 */

public class HomePage extends JFrame {
    public HomePage() {
        ImageIcon image = new ImageIcon(getClass().getResource("/Images/icon.jpeg"));
        this.setIconImage(image.getImage());

        JPanel body = new BodyPanel();
        JScrollPane scrollPane = ScrollPaneFactory.createPane();
        scrollPane.setViewportView(body);
        scrollPane.getViewport().setBackground(BACKGROUND);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setTitle("Flight Analyser");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(BACKGROUND);
        this.setSize(1024, 720);
        this.setMinimumSize(new Dimension(512, 512));
        this.setVisible(true);
    }
}

