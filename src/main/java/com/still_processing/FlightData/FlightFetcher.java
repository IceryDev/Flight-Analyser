package com.still_processing.FlightData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Requests.AuthenticatedRequest;
import com.still_processing.FlightData.Requests.RateLimitException;
import com.still_processing.FlightData.Requests.RequestFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Fetches live OpenSky flight data, enriches the data with local database elements and
 * flight route information from hexdb.io lookup API.
 *
 * @author Ulaş İçer
 */
public class FlightFetcher {

    // URLs
    private static final String API_URL = "https://opensky-network.org/api/states/all";
    private static final String OAUTH_URL =
            "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token";
    private static final String GET_ROUTE_URL = "https://hexdb.io/api/v1/route/iata/";

    // Data Processing
    private static final String AIRLINE_FILE_PATH = "/airlines.json";
    private static final String ROUTE_SPLIT_DELIMITER = "-";
    private static final int MAX_RETRY = 3;
    private static final int BATCH_SIZE = 25;
    private static final int THREAD_COUNT = 5;

    private static AuthenticatedRequest aRequest;

    /**
     * Periodically populates {@link Database#flights} with live info with information fetched
     * from two consecutive APIs.
     * <p>
     *     Workflow: <p>
     *     - Authenticate API request (Only done once per refresh cycle.)<p>
     *     - Fetch OpenSky data, populate airline and aircraft data. <p>
     *     - Fetch hexdb.io data, populate origin and destination airport data. <p>
     * </p>
     * @implNote {@link Database#airports} and {@link Database#airlineIcaoToIata} MUST be populated before
     * running. Will fail to work unless the following initializations are done pre-emptively.
     * {@snippet :
     * CSVHandler.loadAirportCSV();
     * FlightFetcher.getAirlineCodes();}
     * @param limit Number of flights whose data to be acquired. Maxes out at the instant flight count.
     * @throws RequestFailedException Thrown when there is a fatal error in API requests.
     * @throws InterruptedException Thread-related errors.
     *
     * @author Ulaş İçer
     */
    static void fetchLiveFlightInfo(int limit) throws RequestFailedException, InterruptedException {

        int retryCount = 0;

        ArrayList<FlightInfo> result = new ArrayList<>();
        CompletableFuture<HttpResponse<String>> response;
        ObjectMapper mapper = new ObjectMapper();

        if (aRequest == null){
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            aRequest = new AuthenticatedRequest(client, OAUTH_URL, Settings.USER_NAME_OPENSKY, Settings.CLIENT_SECRET);
        }
        boolean processFinished = false;
        while (!processFinished) {
            try {

                response = aRequest.sendAsync(API_URL);

                HttpResponse<String> responseAsHttp = response.join();

                extractPlaneInfo(result, responseAsHttp.body(), mapper);

                completeFlightInfo(result, mapper, false, limit);

                processFinished = true;
            }
            catch (CompletionException e) {
                if(e.getCause() instanceof RateLimitException rle){
                    if (retryCount >= MAX_RETRY){
                        throw new RequestFailedException(
                                "RequestFailedException: Http request failed after " + MAX_RETRY + " trials.",
                                MAX_RETRY,
                                rle.getStatusCode());
                    }
                    retryCount++;
                    System.err.println(rle.getMessage() + " Retrying...");
                    Thread.sleep(rle.getDelay());
                }
                else if (e.getCause() instanceof RequestFailedException rfe){
                    throw new RequestFailedException(
                            "RequestFailedException: Http request failed to get from hexDb database.",
                            MAX_RETRY,
                            rfe.getStatusCode());
                }
                else {
                    processFinished = true;
                    e.printStackTrace();
                }
            }
            catch (JsonProcessingException e){
                System.err.println("Error: An error occurred while parsing JSON structure.");
            }
        }
    }

    /**
     * Enrichens the aircraft + flight data with the origin and destination airport parameters.
     * Done periodically, {@link Database#flights} is updated after each batch.
     * Full list of parameters filled: <p>
     *     - {@code FlightInfo.origin} //See {@link CSVHandler#loadAirportCSV()} for more information.<p>
     *     - {@code FlightInfo.dest} //See {@link CSVHandler#loadAirportCSV()} for more information. <p>
     * @param array The array of flights to be enrichened.
     * @param mapper {@code ObjectMapper} object to extract JSON.
     * @param debug Prints debug messages if set to true.
     * @param limit Maximum flight process limit.
     * @throws InterruptedException Thread-related errors.
     * @see CSVHandler
     *
     * @author Ulaş İçer
     */
    private static void completeFlightInfo(ArrayList<FlightInfo> array, ObjectMapper mapper, boolean debug, int limit) throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {

            for (int i = 0; i < array.size() && i < limit; i+=BATCH_SIZE) {
                ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
                int end = Math.min(Math.min(limit, i + BATCH_SIZE), array.size());
                ArrayList<FlightInfo> processed = new ArrayList<>(array.subList(i, end));

                for (FlightInfo info : processed) {
                    // Skip if no call sign
                    if (info.plane.callSign == null || info.plane.callSign.isBlank()) {
                        continue;
                    }
                    // Send request + populate object values.
                    CompletableFuture<?> future = CompletableFuture
                                .supplyAsync(() -> {
                                    HttpRequest routeRequest = HttpRequest.newBuilder()
                                            .uri(URI.create(GET_ROUTE_URL + info.plane.callSign))
                                            .GET()
                                            .build();
                                    try {
                                        return aRequest.getClient()
                                                .send(routeRequest, HttpResponse.BodyHandlers.ofString());
                                    } catch (IOException | InterruptedException e) {
                                        return null;
                                    }
                                }, executor)
                                .thenApply(response -> {
                                    if (response == null) { return null; }
                                    JsonNode json;
                                    try {
                                        json = mapper.readTree(response.body());
                                    } catch (JsonProcessingException e) {
                                        System.err.println("Skip: No match found for instance. Skipping...");
                                        return null;
                                    }
                                    String[] route;
                                    if (json != null && json.get("route") != null) {
                                        route = json.get("route").asText().split(ROUTE_SPLIT_DELIMITER);
                                        info.origin = Database.airports.get(route[0]);
                                        info.dest = Database.airports.get(route[1]);
                                    } else {
                                        if (debug) {
                                            System.err.println("Skip: No match found for instance. Skipping...");
                                        }
                                    }
                                    return null;
                                });
                        futures.add(future);
                        Thread.sleep(200);
                        // End of instance
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                processed.removeIf(info ->
                        info.iataCode == null || info.iataCode.isBlank() || info.origin == null || info.dest == null
                );
                Database.flights.addAll(processed);
                Thread.sleep(1000);
                // End of batch
            }

        }
    }

    /**
     * Extracts flight and aircraft parameters from OpenSky JSON {@code HttpResponse}.
     * Full list of parameters that are filled: <p>
     *     - {@code FlightInfo.plane.icao24} <p>
     *     - {@code FlightInfo.plane.callSign} <p>
     *     - {@code FlightInfo.iataCode} <p>
     *     - {@code FlightInfo.airline} <p>
     *     - {@code FlightInfo.plane.country} <p>
     *     - {@code FlightInfo.plane.lastContact} <p>
     *     - {@code FlightInfo.plane.longitude} <p>
     *     - {@code FlightInfo.plane.latitude} <p>
     *     - {@code FlightInfo.plane.altitude} <p>
     *     - {@code FlightInfo.plane.onGround} <p>
     *     - {@code FlightInfo.plane.velocity} <p>
     *     - {@code FlightInfo.plane.heading} <p>
     *     - {@code FlightInfo.plane.verticalRate} <p>
     *     - {@code FlightInfo.plane.geoAltitude} <p>
     *     - {@code FlightInfo.plane.squawk} <p>
     *     - More Info: <a href="https://openskynetwork.github.io/opensky-api/rest.html#all-state-vectors">OpenSky API docs</a>
     * @param array The array to be populated.
     * @param body The HTML body extracted from OpenSky response.
     * @param mapper Mapper object to parse JSON.
     * @throws JsonProcessingException JSON-related exception.
     * @see <a href="https://openskynetwork.github.io/opensky-api/rest.html#all-state-vectors">OpenSky API docs</a>
     *
     * @author Ulaş İçer
     */
    private static void extractPlaneInfo(ArrayList<FlightInfo> array, String body, ObjectMapper mapper) throws JsonProcessingException{
        JsonNode root = mapper.readTree(body);
        JsonNode states = root.get("states");

        //Extract state vectors
        for (JsonNode flight : states) {
            FlightInfo tmp = new FlightInfo();
            tmp.plane = new PlaneInfo();

            tmp.plane.icao24 = flight.get(0).asText();
            tmp.plane.callSign = flight.get(1).asText().trim();
            if (tmp.plane.callSign.length() > 3){
                String airlineIcao = tmp.plane.callSign.trim().substring(0, 3);
                if(Database.airlineIcaoToIata.containsKey(airlineIcao)){
                    tmp.iataCode = Database.airlineIcaoToIata.get(airlineIcao).iata();
                    tmp.airline = Database.airlineIcaoToIata.get(airlineIcao).name();
                }
            }
            tmp.plane.country = flight.get(2).asText();
            tmp.plane.lastContact = flight.get(4).asLong();
            tmp.plane.longitude = flight.get(5).asDouble();
            tmp.plane.latitude = flight.get(6).asDouble();
            tmp.plane.altitude = flight.get(7).asDouble();
            tmp.plane.onGround = flight.get(8).asBoolean();
            tmp.plane.velocity = flight.get(9).asDouble();
            tmp.plane.heading = flight.get(10).asDouble();
            tmp.plane.verticalRate = flight.get(11).asDouble();
            tmp.plane.geoAltitude = flight.get(13).asDouble();
            tmp.plane.squawk = flight.get(14).isNull() ? null : flight.get(14).asText();

            array.add(tmp);
        }
    }

    /**
     * Populates {@link Database#airlineIcaoToIata} with airline information.
     * Required to be run before fetching live data.
     *
     * @see FlightFetcher#fetchLiveFlightInfo(int)
     * @author Ulaş İçer
     */
    public static void getAirlineCodes(){
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = Objects.requireNonNull(
                CSVHandler.class.getResourceAsStream(AIRLINE_FILE_PATH));

        try {
            JsonNode root = mapper.readTree(is);
            for (JsonNode airline : root){
                if (airline != null){
                    String icao = airline.get("icao").asText().trim();
                    String iata = airline.get("iata").asText().trim();

                    if (!icao.isEmpty()){
                        Database.airlineIcaoToIata.put(airline.get("icao").asText().trim(),
                                new AirlineCode(
                                        (iata.isBlank()) ? "N/A" : iata,
                                        airline.get("name").asText().trim()));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        CSVHandler.loadAirportCSV();
//        getAirlineCodes();
//        try{
//            FlightFetcher.fetchLiveFlightInfo(25);
//
//        } catch (RequestFailedException e) {
//            System.err.println(e.getStatusCode());
//        }
    }
}
