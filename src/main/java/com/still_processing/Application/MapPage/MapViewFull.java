package com.still_processing.Application.MapPage;

import com.still_processing.FlightData.FlightInfo;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MapViewFull extends MapView {

    private static final double MAX_LAT =  85.0;
    private static final double MIN_LAT = -85.0;
    private final int MAX_ZOOM = 3;


    // mode true/false -> live/historical
    public MapViewFull(boolean mode, ArrayList<FlightInfo> flights, JPanel parent){
        super();
        if (mode) {
            SwingUtilities.invokeLater(() -> {
                this.setDisplayPosition(new Coordinate(53.3498, -6.2603), 7);
                this.repaint();
            });
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return getMaximumSize();
    }

    @Override
    public void setZoom(int zoom, Point mapPoint) {
        super.setZoom(Math.max(zoom, MAX_ZOOM), mapPoint);
    }

    @Override
    public void setZoom(int zoom) {
        super.setZoom(Math.max(zoom, MAX_ZOOM));
    }

    @Override
    public void moveMap(int x, int y) {
        super.moveMap(x, y);
        confineLatitude(getZoom());
    }

    public void confineLatitude(int zoom){
        ICoordinate topLeft     = this.getPosition(new Point(0, 0));
        ICoordinate bottomRight = this.getPosition(new Point(getWidth(), getHeight()));
        ICoordinate center      = this.getPosition();

        double halfLat = Math.abs(topLeft.getLat() - center.getLat());

        boolean outOfBoundsAbove = topLeft.getLat() > MAX_LAT;
        boolean outOfBoundsBelow = bottomRight.getLat() < MIN_LAT;

        double lat = center.getLat();
        if (outOfBoundsAbove){
            lat = MAX_LAT - halfLat;
        }
        else if(outOfBoundsBelow){
            lat = MIN_LAT + halfLat;
        }

        if (Math.abs(lat - center.getLat()) > 1e-5){
            super.setDisplayPosition(new Coordinate(lat, center.getLon()), zoom);
        }
    }


}
