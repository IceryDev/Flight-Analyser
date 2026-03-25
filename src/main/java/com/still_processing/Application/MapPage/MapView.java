package com.still_processing.Application.MapPage;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import java.awt.*;

public class MapView extends JMapViewer {

    public MapView(){
        this.setTileSource(new OsmTileSource.Mapnik());
        this.setTileLoader(new OsmTileLoader(this));
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        this.setMapMarkerVisible(true);
        this.setScrollWrapEnabled(true);
    }
}
