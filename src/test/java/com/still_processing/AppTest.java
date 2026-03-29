package com.still_processing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.still_processing.FlightData.Filters.Filter;
import com.still_processing.FlightData.Filters.FilterApplier;
import com.still_processing.FlightData.Filters.FuzzySearch;
import com.still_processing.FlightData.Filters.impl.DistanceFilter;
import com.still_processing.FlightData.Filters.impl.OriginAirportNameFilter;
import com.still_processing.FlightData.Airport;
import com.still_processing.FlightData.CSVHandler;
import com.still_processing.FlightData.Database;
import com.still_processing.FlightData.FlightFetcher;
import com.still_processing.FlightData.FlightInfo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
        CSVHandler.loadAirportCSV();
        CSVHandler.loadOfflineFlightCSV();
        FlightFetcher.getAirlineCodes();
        CSVHandler.loadOfflineFlightCSV();
        ArrayList<FlightInfo> flightList = Database.offlineFlights;

        ArrayList<String> airportList = new ArrayList<>();
        for (Airport airport : Database.getAirports().values()) {
            airportList.add(airport.name);
        }
        String searchTerm = "John F. Kennedy";
        List<String> resultList = FuzzySearch.fuzzySearch(searchTerm, airportList);
        System.out.println("Searching for airpot: " + searchTerm);
        for (int i = 0; i < 10; i++) {
            System.out.println(resultList.get(i));
        }

        FilterApplier filter = new FilterApplier(flightList);
        List<FlightInfo> filteredList = filter.apply(new OriginAirportNameFilter(), resultList.get(0));
        System.out.println("Filter Size: " + filteredList.size());
        for (int i = 0; i < 25; i++) {
            FlightInfo flight = filteredList.get(i);
            System.out.println(flight.iataCode + flight.flightNumber);
        }
        System.out.println("Flight Entries:" + flightList.size());

        LocalDate now = LocalDate.parse("2022-01-02");
        LocalDate then = LocalDate.parse("2022-01-04");
        Filter filterTest = new Filter(flightList);
        filteredList = filterTest.byDateRange(now, then);
        for (int i = 0; i < 25; i++) {
            FlightInfo flight = filteredList.get(i);
            System.out.println(flight.iataCode + flight.flightNumber + ":" + flight.flightDate);
        }

    }
}
