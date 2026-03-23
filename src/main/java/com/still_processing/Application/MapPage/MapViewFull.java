package com.still_processing.Application.MapPage;

import com.still_processing.FlightData.FlightInfo;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

import java.awt.*;
import java.util.ArrayList;

public class MapViewFull extends JMapViewer {

    // mode true/false -> live/historical
    public MapViewFull(boolean mode, ArrayList<FlightInfo> flights){
        if (mode){

        }
    }

    @Override
    public Dimension getPreferredSize() {
        return getMaximumSize();
    }
}
