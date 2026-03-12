package com.still_processing.FlightData.Graphs;

/**
 * Enum {@code PropertyType}. Contains types for data to be passed into analysis.
 * {@code isCategorical} variable determines if the parameter is categorical or quantitative.
 *
 * @author IceryDev (Ulaş İçer)
 */
public enum PropertyType {
    DATE(false),
    IATA(true),
    ORIGIN(true),
    DESTINATION(true),
    DEPARTURE(false),           //Departure Time
    ARRIVAL(false),             //Arrival Time
    DISTANCE(false),
    CANCELLED(true),
    DIVERTED(true);



    final boolean isCategorical;
    PropertyType(boolean isCategorical){

        this.isCategorical = isCategorical;
    }
}
