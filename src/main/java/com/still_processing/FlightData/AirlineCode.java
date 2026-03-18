package com.still_processing.FlightData;

/**
 * Used in the hashmap to lookup airline IATA and name from ICAO codes.
 * @param iata
 * @param name
 *
 * @author Ulaş İçer
 */
public record AirlineCode(String iata, String name) { }
