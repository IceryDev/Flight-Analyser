package com.still_processing.Application.MapPage;

import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Utils.LiveDataHandler;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * A full map view for live flights.
 *
 * @author Ulaş İçer
 */
public class MapViewFull extends MapView implements MouseListener {

    private static final double MAX_LAT =  85.0;
    private static final double MIN_LAT = -85.0;
    private final int MAX_ZOOM = 3;

    private FlightInfo selectedInfo;
    private PlaneMarker lastSelected;
    public boolean inDatabase = true;


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

    /**
     * Prohibits panning out of the map
     * @param zoom Current zoom level.
     *
     * @author Ulaş İçer
     */
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

    public FlightInfo getSelectedInfo() {
        return selectedInfo;
    }

    public void setSelectedInfo(FlightInfo selectedInfo) {
        this.selectedInfo = selectedInfo;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point clicked = e.getPoint();

        for (MapMarker marker : new ArrayList<>(this.getMapMarkerList())){
            Point markerP = this.getMapPosition(marker.getLat(), marker.getLon(), false);

            if (marker instanceof PlaneMarker pm && markerP != null && clicked.distance(markerP) <= marker.getRadius()){

                if (this.lastSelected != null) this.lastSelected.selected = false;

                pm.selected = true;
                this.lastSelected = pm;
                this.selectedInfo = LiveDataHandler.markers.get(pm);
                this.inDatabase = true;

            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
