package com.still_processing.FlightData.Filters.impl;

import java.util.ArrayList;
import java.util.List;

import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Filters.SimpleFilter;

/**
 * @author Marco Fontana
 */
public final class DestinationAirportNameFilter implements SimpleFilter<List<FlightInfo>, String> {

    /**
     * Filters a list of FlightInfo objects based on the destination airport name.
     *
     * @param items
     * @param destinationAirportName destinationAirportName[0] is the name of the
     *                               destination airport to filter by
     * @return A list of FlightInfo objects that match the specified destination
     *         airport name.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, String... destinationAirportName) {
        List<FlightInfo> result = new ArrayList<>();
        System.out.println("Items: " + items.size());
        result.addAll(items);
        result.removeIf(flight -> !flight.dest.name.equals(destinationAirportName[0]));
        return result;
    }
}
