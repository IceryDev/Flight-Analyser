package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class CancelledFilter implements SimpleFilter<List<FlightInfo>, Boolean> {

    /**
     * Filters a list of FlightInfo objects based on the cancellation status.
     *
     * @param items
     * @param isCancelled isCancelled[0] is the cancellation status to filter by (true for canceled flights, false for non-cancelled flights)
     * @return A list of FlightInfo objects that match the specified cancellation status.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, Boolean... isCancelled) {
        items.removeIf(flight -> flight.cancelled != isCancelled[0]);
        return items;
    }
}
