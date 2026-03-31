package com.still_processing.Application.MapPage;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.FlightInfo;

import javax.swing.*;
import java.awt.*;

public class MapSideOverlay extends JPanel {
    public final float TRANSPARENCY = 0.85f;

    public MapSideOverlay(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
        g2d.setColor(Settings.TEXT_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
        super.paintComponent(g);
        g.dispose();
    }

    public void setFlightToDisplay(FlightInfo fi){
        System.out.println("Displaying: " + fi.iataCode);
    }
}
