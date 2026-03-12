package com.still_processing.FlightData;

import java.util.Map;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    static HashMap<String, Airport> airports = new HashMap<>();
    static ArrayList<FlightInfo> flights = new ArrayList<>();
    static ArrayList<FlightInfo> offlineFlights = new ArrayList<>();

    public static Map<String, Airport> getAirports() {
        return Collections.unmodifiableMap(airports);
    }

    /**
     * Parses a JSON structure containing route information and extracts flight data,
     * adding them to the internal collections of flights.
     *
     * @author Marco Fontana
     * @param <T> Related type of input data containing the JSON representation
     * @param abstractData An object whose returns a JSON string describing airports & flights routes
     * @throws JsonProcessingException
     */
    public <T> void deserialize(T abstractData) throws JsonProcessingException {
        JsonNode rootNode = new ObjectMapper().readTree(abstractData.toString());
        JsonNode routes = rootNode.get("routes");
        for (JsonNode r : routes) {
            JsonNode destinationNode = r.get("destination");
            if (destinationNode == null) {
                continue;
            }
            JsonNode nameNode = destinationNode.get("name");
            JsonNode locationNode = destinationNode.get("location");
            JsonNode countryNode = destinationNode.get("countryCode");
            JsonNode municipalityNode = destinationNode.get("municipalityName");
            JsonNode iataNode = destinationNode.get("iata");
            if (nameNode == null || locationNode == null || countryNode == null || municipalityNode == null || iataNode == null) {
                continue;
            }
            JsonNode latitudeNode = locationNode.get("lat");
            JsonNode longitudeNode = locationNode.get("lon");
            if (latitudeNode == null || longitudeNode == null) {
                continue;
            }
            Airport destinationAirport = airports.get(iataNode.asText());
            JsonNode operatorsNode = r.get("operators");
            // We can't directly continue without getting operators
            assert operatorsNode != null;
            for (JsonNode op : operatorsNode) {
                boolean hasIata = op.has("iata");
                boolean hasIcao = op.has("icao");
                String airlineCode = hasIata ? op.get("iata").asText() : (hasIcao ? op.get("icao").asText() : null);
                assert airlineCode != null;
                FlightInfo flight = new FlightInfo();
                flight.dest = destinationAirport;
                flight.IATA_Code_Marketing_Airline = airlineCode;
                flights.add(flight);
            }
        }
    }
}
