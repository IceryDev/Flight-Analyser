package com.still_processing.FlightData;

/**
 * @author Deea Zaharia
 */

public class FlightInfo {
    public String flightDate;
    public String iataCode;
    public String flightNumber;
    public String originAirport;
    public Airport origin;
    public Airport dest;
    public String CRSDepTime;
    public String depTime;
    public String CRSArrTime;
    public String arrTime;
    public boolean cancelled;
    public boolean diverted;
    public float distance;
    public float lateness;

    public PlaneInfo plane;
    public String airline;

    FlightInfo() {
    }
}
