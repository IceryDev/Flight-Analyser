package com.still_processing.FlightData.Filters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.still_processing.FlightData.FlightInfo;

public class Filter {
    private ArrayList<FlightInfo> initialList;

    public Filter(ArrayList<FlightInfo> initialList) {
        this.initialList = initialList;
    }

    public ArrayList<FlightInfo> byOriginAirport(String originAirportName) {
        if (initialList != null && initialList.size() != 0 &&
                originAirportName != null && originAirportName.length() != 0) {
            ArrayList<FlightInfo> resultList = new ArrayList<>();
            resultList.addAll(initialList);
            resultList.removeIf(flight -> !flight.origin.name.equals(originAirportName));
            return resultList;
        }
        return null;
    }

    public ArrayList<FlightInfo> byDestAirport(String destinationAirportName) {
        if (initialList != null && initialList.size() != 0 &&
                destinationAirportName != null && destinationAirportName.length() != 0) {
            ArrayList<FlightInfo> resultList = new ArrayList<>();
            resultList.addAll(initialList);
            resultList.removeIf(flight -> !flight.dest.name.equals(destinationAirportName));
            return resultList;
        }
        return null;
    }

    public ArrayList<FlightInfo> byDateRange(LocalDate startDate, LocalDate endDate) {
        if (initialList != null && initialList.size() != 0) {
            ArrayList<FlightInfo> resultList = new ArrayList<>();
            resultList.addAll(initialList);
            DateTimeFormatter usDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            resultList.removeIf(flight -> {
                String dateString = flight.flightDate.substring(0, 10);
                LocalDate flightDate = LocalDate.parse(dateString, usDateFormat);
                boolean before = flightDate.isBefore(startDate);
                boolean after = flightDate.isAfter(endDate);
                return before || after;
            });

            return resultList;
        }
        return null;
    }
}
