package com.still_processing.Application.MapPage;

import com.still_processing.DefaultSettings.Settings;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code AirportMarker} class is used in the generation of the map markers for airports
 * in {@link ConfinedMapView} objects.
 *
 * @author Ulaş İçer
 */
public class AirportMarker implements MapMarker{
    private static final int BASE_ZOOM = 10;
    private static final double SCALE_COEF = 1.5;
    public BufferedImage icon = null;
    public Coordinate coord;
    public JMapViewer jmv;
    public double radius = 15;

    public AirportMarker(Coordinate coord, JMapViewer jmv) {
        try{
            this.icon = ImageIO.read(Objects.requireNonNull(getClass().getResource(Settings.MAP_MARKER)));
        }catch(IOException e){e.printStackTrace();}
        this.coord = coord;
        this.jmv = jmv;
    }

    @Override
    public Coordinate getCoordinate() {
        return this.coord;
    }

    @Override
    public double getLat() {
        return this.coord.getLat();
    }

    @Override
    public void setLat(double lat) {
        if (this.coord == null) {
            this.coord = new Coordinate(lat, (double)0.0F);
        } else {
            this.coord.setLat(lat);
        }
    }

    @Override
    public double getLon() {
        return this.coord.getLon();
    }

    @Override
    public void setLon(double lon) {
        if (this.coord == null) {
            this.coord = new Coordinate(lon, (double)0.0F);
        } else {
            this.coord.setLat(lon);
        }

    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public MapMarker.STYLE getMarkerStyle() {
        return null;
    }

    /**
     * Overrides the paint method to render images instead of dots.
     * @param g The Graphics object to perform the task with.
     * @param position Lat/Lon position of the marker.
     * @param radius Marker size.
     *
     * @author Ulaş İçer
     */
    @Override
    public void paint(Graphics g, Point position, int radius) {
        if (this.icon == null) return;
        int zoom = this.jmv.getZoom();

        double size = ((2 * this.radius) + (zoom - BASE_ZOOM) * SCALE_COEF);
        AffineTransform at = new AffineTransform();
        at.translate(position.x, position.y);
        at.translate((double) -size /2, (double) -size /2);
        at.scale((double) size / this.icon.getWidth(), (double) size / this.icon.getHeight());
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.icon, at, null);
    }

    @Override
    public Layer getLayer() {
        return null;
    }

    @Override
    public void setLayer(Layer layer) {

    }

    @Override
    public Style getStyle() {
        return null;
    }

    @Override
    public Style getStyleAssigned() {
        return null;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public Color getBackColor() {
        return null;
    }

    @Override
    public Stroke getStroke() {
        return null;
    }

    @Override
    public Font getFont() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
