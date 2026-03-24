package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class DistanceFilter implements SimpleFilter<List<FlightInfo>, Float> {


    /**
     * Filters a list of FlightInfo objects based on the flight distance.
     *
     * @param items
     * @param distance distance[0] is the distance to filter by
     * @return A list of FlightInfo objects that match the specified distance.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, Float... distance) {
        items.removeIf(flight -> flight.distance != distance[0]);
        return items;
    }
}
