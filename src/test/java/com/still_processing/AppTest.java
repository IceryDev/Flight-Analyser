package com.still_processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.still_processing.FlightData.Filters.FilterApplier;
import com.still_processing.FlightData.Filters.impl.DistanceFilter;
import com.still_processing.FlightData.Filters.impl.OriginAirportNameFilter;
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

        FilterApplier filter = new FilterApplier(flightList);
        List<FlightInfo> fileredList = filter.apply(new OriginAirportNameFilter(),
                "Charlotte Douglas International Airport");
        System.out.println("Filter Size: " + fileredList.size());
        System.out.println("Flight Entries:" + flightList.size());
    }
}
