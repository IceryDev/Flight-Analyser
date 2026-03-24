package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class DepartureTimeFilter implements SimpleFilter<List<FlightInfo>, String> {


    /**
     * Filters a list of FlightInfo objects based on a range of departure times.
     *
     * @param items
     * @param departureTimeRange departureTimeRange[0] is the start time, departureTimeRange[1] is the end time
     * @return A list of FlightInfo objects that fall within the specified departure time range.
     *
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, String... departureTimeRange) {
        if (departureTimeRange.length != 2) {
            throw new IllegalArgumentException("Parameter must be a range of two times (start and end).");
        }
        String startTime = departureTimeRange[0];
        String endTime = departureTimeRange[1];
        items.removeIf(flightInfo -> {
            String departureTime = flightInfo.arrTime;
            return departureTime.compareTo(startTime) < 0 || departureTime.compareTo(endTime) > 0;
        });
        return items;
    }
}
