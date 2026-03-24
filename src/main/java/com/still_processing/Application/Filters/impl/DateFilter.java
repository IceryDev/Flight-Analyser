package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class DateFilter implements SimpleFilter<List<FlightInfo>, String> {

    /**
     * Filters a list of FlightInfo objects based on a range of flight dates.
     *
     * @param items
     * @param flightDateRange flightDateRange[0] is the start date, flightDateRange[1] is the end date
     * @return A list of FlightInfo objects that fall within the specified date range.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, String... flightDateRange) {
        if (flightDateRange.length != 2) {
            throw new IllegalArgumentException("Parameter must be a range of two dates (start and end).");
        }
        // Expected format: "DD-MM-YYYY"
        String startDate = flightDateRange[0];
        String endDate = flightDateRange[1];
        items.removeIf(flightInfo -> {
            String flightDate = flightInfo.flightDate;
            return flightDate.compareTo(startDate) < 0 || flightDate.compareTo(endDate) > 0;
        });
        return items;
    }
}
