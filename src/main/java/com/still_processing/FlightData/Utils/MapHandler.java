package com.still_processing.FlightData.Utils;

import com.still_processing.Application.MapPage.ConfinedMapView;
import com.still_processing.Application.MapPage.MapContainer;
import com.still_processing.FlightData.FlightInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapHandler {
    private static final int MAX_CACHE = 5;
    private static LinkedHashMap<MapContainer, ConfinedMapView> cache =
            new LinkedHashMap<>(MAX_CACHE, 0.75f, true){
                @Override
                public boolean removeEldestEntry(Map.Entry eldest) {
                    if (size() > MAX_CACHE) {
                        MapContainer tmp = (MapContainer) eldest.getKey();
                        if (!tmp.parentExpanded){
                            tmp.remove((ConfinedMapView) eldest.getValue());
                            tmp.revalidate();
                            tmp.repaint();
                        }
                        return true;
                    }
                    return false;
                }
    };

    public static void cacheInfoMap(ConfinedMapView cmv, MapContainer attached){
        cache.computeIfAbsent(attached, k -> cmv);
    }

    public static ConfinedMapView getInfoMap(FlightInfo data){
        return new ConfinedMapView(data);
    }

}
