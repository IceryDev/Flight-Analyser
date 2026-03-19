package com.still_processing.FlightData;

public class FlightInfo {
    String flightDate;
    String iataCode;
    String flightNumber;
    String originAirport;
    Airport origin;
    Airport dest;
    String CRSDepTime;
    String depTime;
    String CRSArrTime;
    String arrTime;
    boolean cancelled;
    boolean diverted;
    float distance;
    float lateness;

    PlaneInfo plane;
    String airline;

    FlightInfo(){}
}
