package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class OriginAirportNameFilter implements SimpleFilter<List<FlightInfo>, String> {


    /**
     * Filters a list of FlightInfo objects based on the origin airport name.
     *
     * @param items
     * @param originAirportName originAirportName[0] is the name of the origin airport to filter by
     * @return A list of FlightInfo objects that match the specified origin airport name.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, String... originAirportName) {
        items.removeIf(flight -> !flight.origin.name.equals(originAirportName[0]));
        return items;
    }
}
