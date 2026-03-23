package com.still_processing.Application.MapPage;

import com.still_processing.DefaultSettings.Settings;
import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class PlaneMarker implements MapMarker {

    public BufferedImage icon = null;
    public Coordinate coord;
    public double rot;
    public double radius = 30;

    public PlaneMarker(Coordinate coord) {
        try{
            this.icon = ImageIO.read(Objects.requireNonNull(getClass().getResource(Settings.ICON_PATH)));
        }catch(IOException e){e.printStackTrace();}
        this.coord = coord;
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
        double size = 2 * this.radius;
        AffineTransform at = new AffineTransform();
        at.translate(position.x, position.y);
        at.rotate(this.rot);
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
