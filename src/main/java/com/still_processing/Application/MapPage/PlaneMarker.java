package com.still_processing.Application.MapPage;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import javax.swing.*;
import java.awt.*;

public class PlaneMarker extends MapMarkerDot {

    public final ImageIcon icon;
    public final JMapViewer jmv;
    public PlaneMarker(Coordinate coord, ImageIcon icon, JMapViewer jmv) {
        super(coord);
        this.icon = icon;
        this.jmv = jmv;
    }

    @Override
    public void paint(Graphics g, Point position, int radius) {
        icon.paintIcon(this.jmv, g,
                position.x - icon.getIconWidth()  / 2,
                position.y - icon.getIconHeight() / 2
        );
    }
}
