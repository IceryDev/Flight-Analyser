package com.still_processing.FlightData;

/**
 * @author Deea Zaharia
 */

public class FlightInfo {
    String flightDate;
    String IATA_Code_Marketing_Airline;
    String Flight_Number_Marketing_Airline;
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

    FlightInfo(){}

    FlightInfo(String flightDate, String IATA_Code_Marketing_Airline,
               String Flight_Number_Marketing_Airline, String originAirport,
               Airport origin, Airport dest, String CRSDepTime, String depTime,
               String CRSArrTime, String arrTime, boolean cancelled, boolean diverted,
               float distance){
        this.flightDate = flightDate;
        this.IATA_Code_Marketing_Airline = IATA_Code_Marketing_Airline;
        this.Flight_Number_Marketing_Airline = Flight_Number_Marketing_Airline;
        this.originAirport = originAirport;
        this.origin = origin;
        this.dest = dest;
        this.CRSDepTime = CRSDepTime;
        this.depTime = depTime;
        this.CRSArrTime = CRSArrTime;
        this.arrTime = arrTime;
        this.cancelled = cancelled;
        this.diverted = diverted;
        this.distance = distance;
    }

}
