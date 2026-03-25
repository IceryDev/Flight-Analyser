package com.still_processing.FlightData.Filters.impl;

import java.util.List;

import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Filters.SimpleFilter;

/**
 * @author Marco Fontana
 */
public final class DivertedFilter implements SimpleFilter<List<FlightInfo>, Boolean> {

    /**
     * Filters a list of FlightInfo objects based on the diversion status.
     *
     * @param items
     * @param isDiverted isDiverted[0] is the diversion status to filter by (true
     *                   for diverted flights, false for non-diverted flights)
     * @return A list of FlightInfo objects that match the specified diversion
     *         status.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, Boolean... isDiverted) {
        items.removeIf(flight -> flight.diverted != isDiverted[0]);
        return items;
    }
}
