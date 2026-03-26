package com.still_processing.Application.MapPage;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import java.awt.Graphics;
import java.awt.Point;

/**
 * Allows the use of {@link JMapViewer#setDisplayToFitMapMarkers()} function
 * to confine the map in a specific frame.
 *
 * @author Ulaş İçer
 */
public class InvisibleMarker extends MapMarkerDot {

    public InvisibleMarker(Coordinate coord) {
        super(coord);
    }

    @Override
    public void paint(Graphics g, Point position, int radius) {
        //Do nothing
    }
}
