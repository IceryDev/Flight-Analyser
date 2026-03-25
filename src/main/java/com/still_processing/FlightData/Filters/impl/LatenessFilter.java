package com.still_processing.FlightData.Filters.impl;

import java.util.List;

import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Filters.SimpleFilter;

/**
 * @author Marco Fontana
 */
public final class LatenessFilter implements SimpleFilter<List<FlightInfo>, Float> {

    /**
     * Filters a list of FlightInfo objects based on the flight lateness.
     *
     * @param items
     * @param lateness lateness[0] is the lateness to filter by
     * @return A list of FlightInfo objects that match the specified lateness.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, Float... lateness) {
        items.removeIf(flight -> flight.lateness != lateness[0]);
        return items;
    }
}
