package com.still_processing.FlightData.Filters.impl;

import java.util.List;

import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Filters.SimpleFilter;

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
