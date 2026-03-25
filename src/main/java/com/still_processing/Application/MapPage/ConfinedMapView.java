package com.still_processing.Application.MapPage;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.FlightInfo;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicLine;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConfinedMapView extends MapView {

    private final FlightInfo renderFlight;
    private boolean boundsSet = false;
    public ConfinedMapView(FlightInfo render){
        super();
        this.setZoomControlsVisible(false);
        this.renderFlight = render;

        double padding = 1;
        double minLon = Math.min(renderFlight.origin.longitude, renderFlight.dest.longitude) - padding;
        double maxLon = Math.max(renderFlight.origin.longitude, renderFlight.dest.longitude) + padding;

        double minLat = Math.min(renderFlight.origin.latitude, renderFlight.dest.latitude) - padding;
        double maxLat = Math.max(renderFlight.origin.latitude, renderFlight.dest.latitude) + padding;

        double dist = maxLat - minLat;
        minLat = maxLat - (((double) 5 /3)*dist); // 5/3 is the scaling coefficient, as the whole map does not actually render

        this.addMapMarker(new InvisibleMarker(new Coordinate(maxLat, maxLon)));
        this.addMapMarker(new InvisibleMarker(new Coordinate(minLat, minLon)));

        SwingUtilities.invokeLater(() -> {
            this.drawTrajectory();
            this.setDisplayToFitMapMarkers();
            this.addMapMarker(new AirportMarker(new Coordinate(
                    renderFlight.origin.latitude, renderFlight.origin.longitude), this));
            this.addMapMarker(new AirportMarker(new Coordinate(
                    renderFlight.dest.latitude, renderFlight.dest.longitude), this));
            this.boundsSet = true;
        });
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
        if (!boundsSet) { super.moveMap(x, y); }
    }

    private void drawTrajectory(){
        GeodesicData dist = Geodesic.WGS84.Inverse(
                renderFlight.origin.latitude,
                renderFlight.origin.longitude,
                renderFlight.dest.latitude,
                renderFlight.dest.longitude
        );

        GeodesicLine line = Geodesic.WGS84.InverseLine(
                renderFlight.origin.latitude,
                renderFlight.origin.longitude,
                renderFlight.dest.latitude,
                renderFlight.dest.longitude
        );

        int sampleSize = 50;
        ArrayList<Coordinate> samples = new ArrayList<>();
        for (int i = 0; i < sampleSize; i++){
            double d = dist.s12 * ((double) i /sampleSize);
            GeodesicData nextPoint = line.Position(d);
            samples.add(new Coordinate(nextPoint.lat2, nextPoint.lon2));
        }

        MapPolygon arc = new MapPolygonImpl(samples) {
            @Override
            public boolean isVisible() { return true; }

            @Override
            public void paint(Graphics g, List<Point> points) {
                Color transparent = new Color(0, 0, 0, 0);
                g.setColor(this.getColor());

                boolean colorSwitch = false;
                for(int i = 0; i < points.size() - 1; i++) {
                    for (int j = 0; (j < 2 && i < points.size() - 1); j++, i++) {
                        Point p = points.get(i);
                        Point p2 = points.get(i+1);

                        g.setColor((colorSwitch) ? this.getColor() : transparent);
                        g.drawLine(p.x, p.y, p2.x, p2.y);
                        colorSwitch = !colorSwitch;
                    }
                }
            }
        };
        this.addMapPolygon(arc);

        GeodesicData mid = line.Position(dist.s12/2);
        PlaneMarker pm = new PlaneMarker(
                new Coordinate(mid.lat2, mid.lon2),
                mid.azi2,
                this, Settings.PLANE_BLACK);
        pm.setRadius(15);
        this.addMapMarker(pm);
    }
}
