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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Fetches OpenSky flight data
 *
 * @author IceryDev
 */
public class FlightFetcher {

    // URLs
    private static final String API_URL = "https://opensky-network.org/api/states/all";
    private static final String OAUTH_URL =
            "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token";
    private static final String GET_AIRCRAFT_URL = "https://hexdb.io/api/v1/aircraft/";
    private static final String GET_IATA_URL = "https://hexdb.io/icao-iata?icao=";
    private static final String GET_ROUTE_URL = "https://hexdb.io/api/v1/route/iata/";

    // Data Processing
    private static final String AIRLINE_FILE_PATH = "/airlines.json";
    private static final String ROUTE_SPLIT_DELIMITER = "-";
    private static final int MAX_RETRY = 3;
    private static final int BATCH_SIZE = 25;

    // Request Codes
    private static final int ERROR_CODE = 400;
    private static final int SUCCESS = 200;

    private static AuthenticatedRequest aRequest;

    static ArrayList<FlightInfo> fetchLiveFlightInfo() throws RequestFailedException, InterruptedException {

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

                completeFlightInfo(result, mapper, false, 100);

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


        return result;
    }

    static void completeFlightInfo(ArrayList<FlightInfo> array, ObjectMapper mapper, boolean debug, int limit) throws InterruptedException {
        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {

            Map<String, String> iataCodes = new ConcurrentHashMap<>();

            for (int i = 0; i < array.size() && i < limit; i+=BATCH_SIZE) {
                ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
                int end = Math.min(Math.min(limit, i + BATCH_SIZE), array.size());
                ArrayList<FlightInfo> processed = new ArrayList<>(array.subList(i, end));

                for (FlightInfo info : processed) {
                    if (info.plane.callSign == null || info.plane.callSign.isBlank()) {
                        continue;
                    }
                    CompletableFuture<?> future = CompletableFuture
                            .supplyAsync(() -> {
                                int status = 0;
                                try {
                                    HttpResponse<String> aircraftResponse = aRequest.getClient().send(
                                            HttpRequest.newBuilder()
                                                    .uri(URI.create(GET_AIRCRAFT_URL + info.plane.icao24))
                                                    .GET()
                                                    .build(), HttpResponse.BodyHandlers.ofString()
                                    );
                                    status = aircraftResponse.statusCode();
                                    System.out.println("Body Stage 1: " + aircraftResponse.body());

                                    return mapper.readTree(aircraftResponse.body());

                                } catch (IOException | InterruptedException e) {
                                    if (debug) {
                                        System.err.println(
                                                "Skip: A network error occurred while getting aircraft info. Skipping instance..." +
                                                        "\nRequest Status Code: " + status);
                                    }
                                    return null;
                                }
                            }, executor)
                            .thenApplyAsync(json -> {
                                int status = 0;
                                String airlineIcao;

                                if (json != null && json.get("OperatorFlagCode") != null) {
                                    airlineIcao = json.get("OperatorFlagCode").asText();
                                    if (!Database.airlineIcaoToIata.containsKey(airlineIcao)){
                                        return null;
                                    }
                                    info.iataCode = Database.airlineIcaoToIata.get(airlineIcao).iata();
                                    info.airline = Database.airlineIcaoToIata.get(airlineIcao).name();

                                    System.out.println(info.iataCode + ":" + info.airline);
                                    return info.iataCode;
                                } else {
                                    return null;
                                }

                            }, executor)
                            .thenApplyAsync(iata -> {
                                int status = 0;
                                try {
                                    if (iata == null) {
                                        return null;
                                    }
                                    info.plane.callSign = info.iataCode + info.plane.callSign.trim().substring(3);
                                    HttpResponse<String> routeResponse = aRequest.getClient().send(
                                            HttpRequest.newBuilder()
                                                    .uri(URI.create(GET_ROUTE_URL + info.plane.callSign))
                                                    .GET()
                                                    .build(), HttpResponse.BodyHandlers.ofString()
                                    );

                                    System.out.println("Call 3: " + routeResponse.body());
                                    status = routeResponse.statusCode();
                                    return mapper.readTree(routeResponse.body());
                                } catch (IOException | InterruptedException e) {
                                    if (debug) {
                                        System.err.println("Skip: A network error occurred while getting route info. " +
                                                "Skipping instance...\nRequest Status Code: " + status);
                                    }
                                    return null;
                                }
                            }, executor)
                            .thenApply(json -> {
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

                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                processed.removeIf(info ->
                        info.iataCode == null || info.iataCode.isBlank() ||
                                info.origin == null ||
                                info.dest == null
                );
                Database.flights.addAll(processed);
                Thread.sleep(400);
            }

        }
    }

    static void extractPlaneInfo(ArrayList<FlightInfo> array, String body, ObjectMapper mapper) throws JsonProcessingException{
        JsonNode root = mapper.readTree(body);
        JsonNode states = root.get("states");

        //Extract state vectors
        for (JsonNode flight : states) {
            FlightInfo tmp = new FlightInfo();
            tmp.plane = new PlaneInfo();

            tmp.plane.icao24 = flight.get(0).asText();
            tmp.plane.callSign = flight.get(1).asText().trim();
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

    public static void getAirlineCodes(){
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = Objects.requireNonNull(
                CSVHandler.class.getResourceAsStream(AIRLINE_FILE_PATH));


        try {
            JsonNode root = mapper.readTree(is);
            for (JsonNode airline : root){
                if (airline != null){
                    String iata = airline.get("iata").asText().trim();
                    Database.airlineIcaoToIata.put(airline.get("icao").asText().trim(),
                            new AirlineCode(
                                    (iata.isBlank()) ? "N/A" : iata,
                                    airline.get("name").asText().trim()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CSVHandler.loadAirportCSV();
        getAirlineCodes();
        for (AirlineCode c : Database.airlineIcaoToIata.values()){
            System.out.println(c.iata());
        }
        try{
            FlightFetcher.fetchLiveFlightInfo();

            for (FlightInfo b : Database.flights){
                System.out.println(b.airline);
            }
        } catch (RequestFailedException e) {
            System.err.println(e.getStatusCode());
        }
    }
}
