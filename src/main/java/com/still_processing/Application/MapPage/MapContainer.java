package com.still_processing.Application.MapPage;

import javax.swing.JPanel;

/**
 * Passes the isExpanded parameter from the {@link com.still_processing.UILib.ExpandablePanel}
 * to the {@link com.still_processing.FlightData.Utils.MapHandler} for caching.
 *
 * @author Ulaş İçer
 */
public class MapContainer extends JPanel {
    public boolean parentExpanded = false;
}
