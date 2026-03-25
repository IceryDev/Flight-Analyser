package com.still_processing.Application.MapPage;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import java.awt.*;

public class InvisibleMarker extends MapMarkerDot {

    public InvisibleMarker(Coordinate coord) {
        super(coord);
    }

    @Override
    public void paint(Graphics g, Point position, int radius) {
        //Do nothing
    }
}
