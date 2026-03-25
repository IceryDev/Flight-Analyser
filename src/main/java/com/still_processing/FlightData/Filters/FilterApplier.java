package com.still_processing.FlightData.Filters;

import com.still_processing.FlightData.FlightInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies filters fluently to a working flights list.
 *
 * @author Marco Fontana
 */
public final class FilterApplier {

    private List<FlightInfo> filteredFlights;

    public FilterApplier(List<FlightInfo> items) {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null.");
        }
        this.filteredFlights = new ArrayList<>(items);
    }

    /**
     * Applies a filter with parameters immediately.
     *
     * @param simpleFilter
     * @param params
     * @param <K>          Parameter type accepted by the filter
     * @return Updated filtered flights
     */
    @SafeVarargs
    public final <K> List<FlightInfo> apply(SimpleFilter<List<FlightInfo>, K> simpleFilter, K... params) {
        if (simpleFilter == null) {
            throw new IllegalArgumentException("Filter cannot be null.");
        }
        filteredFlights = simpleFilter.filter(filteredFlights, params);
        return filteredFlights;
    }
}
