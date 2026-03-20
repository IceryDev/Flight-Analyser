package com.still_processing.FlightData.Graphs;

/**
 * Enum {@code PropertyType}. Contains types for data to be passed into analysis.
 * {@code isCategorical} variable determines if the parameter is categorical or quantitative.
 *
 * @author IceryDev (Ulaş İçer)
 */
public enum PropertyType {
    DATE(false, "Date"),
    IATA(true, "IATA Code"),
    AIRLINE(true, "Airline Name"),
    ORIGIN(true, "Origin Airport"),
    DESTINATION(true, "Destination Airport"),
    DEPARTURE(false, "Departure Time"),           //Departure Time
    ARRIVAL(false, "Arrival Time"),             //Arrival Time
    DISTANCE(false, "Flight Distance"),
    LATENESS(false, "Flight Lateness"),
    CANCELLED(true, "Cancelled"),
    DIVERTED(true, "Diverted");



    public final boolean isCategorical;
    public final String paramName;
    PropertyType(boolean isCategorical, String name){
        this.isCategorical = isCategorical;
        this.paramName = name;
    }
}
