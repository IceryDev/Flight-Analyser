package com.still_processing.FlightData;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public class Database {
    static HashMap<String, Airport> airports = new HashMap<>();

    public static Map<String, Airport> getAirports() {
        return Collections.unmodifiableMap(airports);
    }
}
