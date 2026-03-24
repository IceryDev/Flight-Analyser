package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class AirlineNameFilter implements SimpleFilter<List<FlightInfo>, String> {


    /**
     * Filters a list of FlightInfo objects based on the airline name.
     *
     * @param items
     * @param airlineName airlineName[0] is the name of the airline to filter by
     * @return A list of FlightInfo objects that match the specified airline name.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, String... airlineName) {
        items.removeIf(flight -> !flight.airline.equals(airlineName[0]));
        return items;
    }
}
