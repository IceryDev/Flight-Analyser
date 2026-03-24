package com.still_processing.Application.Filters.impl;

import com.still_processing.Application.Filters.SimpleFilter;
import com.still_processing.FlightData.FlightInfo;

import java.util.List;

/**
 * @author Marco Fontana
 */
public final class IATACodeFilter implements SimpleFilter<List<FlightInfo>, String> {


    /**
     * Filters a list of FlightInfo objects based on the IATA code.
     *
     * @param items
     * @param iataCode iataCode is the IATA code to filter by
     * @return A list of FlightInfo objects that match the specified IATA code.
     */
    @Override
    public List<FlightInfo> filter(List<FlightInfo> items, String... iataCode) {
        items.removeIf(flight -> !flight.iataCode.equals(iataCode[0]));
        return items;
    }
}
