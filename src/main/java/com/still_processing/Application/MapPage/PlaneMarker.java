package com.still_processing.Application.MapPage;

import com.still_processing.DefaultSettings.Settings;
import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class PlaneMarker implements MapMarker {

    private static final int BASE_ZOOM = 10;
    private static final double SCALE_COEF = 1.5;
    public BufferedImage icon = null;
    public Coordinate coord;
    public JMapViewer jmv;
    public double rot;
    public double radius = 10;

    public PlaneMarker(Coordinate coord, double rot, JMapViewer jmv) {
        try{
            this.icon = ImageIO.read(Objects.requireNonNull(getClass().getResource(Settings.PLANE_RED)));
        }catch(IOException e){e.printStackTrace();}
        this.coord = coord;
        this.rot = rot;
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
    public STYLE getMarkerStyle() {
        return null;
    }

    @Override
    public void paint(Graphics g, Point position, int radius) {
        if (this.icon == null) return;
        int zoom = this.jmv.getZoom();

        double size = ((2 * this.radius) + (zoom - BASE_ZOOM) * SCALE_COEF);
        AffineTransform at = new AffineTransform();
        at.translate(position.x, position.y);
        at.rotate(Math.toRadians(this.rot));
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
