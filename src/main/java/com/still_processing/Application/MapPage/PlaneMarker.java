package com.still_processing.Application.MapPage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * Displays markers for the planes, both for live and historical data.
 *
 * @author Ulaş İçer
 */
public class PlaneMarker implements MapMarker {

    private static final int BASE_ZOOM = 10;
    private static final double SCALE_COEF = 1.5;
    public BufferedImage icon = null;
    public BufferedImage selectedIcon = null;
    public Coordinate coord;
    public JMapViewer jmv;
    public double rot;
    public double radius = 10;

    public boolean selected = false;

    public PlaneMarker(Coordinate coord, double rot, JMapViewer jmv, String imagePath, String selectedPath) {
        try {
            this.icon = ImageIO.read(Objects.requireNonNull(getClass().getResource(imagePath)));
            this.selectedIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource(selectedPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            this.coord = new Coordinate(lat, (double) 0.0F);
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
            this.coord = new Coordinate(lon, (double) 0.0F);
        } else {
            this.coord.setLat(lon);
        }

    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double rad) {
        this.radius = rad;
    }

    @Override
    public STYLE getMarkerStyle() {
        return null;
    }

    @Override
    public void paint(Graphics g, Point position, int radius) {
        if (this.icon == null)
            return;
        int zoom = this.jmv.getZoom();

        double size = ((2 * this.radius) + (zoom - BASE_ZOOM) * SCALE_COEF);
        AffineTransform at = new AffineTransform();
        at.translate(position.x, position.y);
        at.rotate(Math.toRadians(this.rot));
        at.translate((double) -size / 2, (double) -size / 2);
        at.scale((double) size / this.icon.getWidth(), (double) size / this.icon.getHeight());
        Graphics2D g2d = (Graphics2D) g;
        if (this.selected) g2d.drawImage(this.selectedIcon, at, null);
        else g2d.drawImage(this.icon, at, null);
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
