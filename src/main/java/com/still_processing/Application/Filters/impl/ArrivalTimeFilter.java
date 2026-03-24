package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class ArrivalTimeFilter implements SimpleFilter<List<FlightInfo>, String> {


    /**
     * Filters a list of FlightInfo objects based on a range of arrival times.
     *
     * @param items
     * @param arrivalTimeRange arrivalTimeRange[0] is the start time, arrivalTimeRange[1] is the end time
     * @return A list of FlightInfo objects that fall within the specified arrival time range.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, String... arrivalTimeRange) {
        if (arrivalTimeRange.length != 2) {
            throw new IllegalArgumentException("Parameter must be a range of two times (start and end).");
        }
        String startTime = arrivalTimeRange[0];
        String endTime = arrivalTimeRange[1];
        items.removeIf(flightInfo -> {
            String arrivalTime = flightInfo.arrTime;
            return arrivalTime.compareTo(startTime) < 0 || arrivalTime.compareTo(endTime) > 0;
        });
        return items;
    }
}
