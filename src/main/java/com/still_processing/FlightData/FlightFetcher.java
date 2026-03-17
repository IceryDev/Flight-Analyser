package com.still_processing.FlightData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Requests.AuthenticatedRequest;
import com.still_processing.FlightData.Requests.RateLimitException;
import com.still_processing.FlightData.Requests.RequestFailedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fetches OpenSky flight data
 *
 * @author IceryDev
 */
public class FlightFetcher {

    private static final String API_URL = "https://opensky-network.org/api/states/all";
    private static final String OAUTH_URL =
            "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token";
    private static final String GET_AIRCRAFT_URL = "https://hexdb.io/api/v1/aircraft/";
    private static final String GET_IATA_URL = "https://hexdb.io/api/v1/icao-iata?icao=";
    private static final String GET_ROUTE_URL = "https://hexdb.io/api/v1/route/iata/";
    private static final int MAX_RETRY = 3;

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
            try (ExecutorService executor = Executors.newFixedThreadPool(5)){

                response = aRequest.sendAsync(API_URL);

                HttpResponse<String> responseAsHttp = response.join();

                extractPlaneInfo(result, responseAsHttp.body(), mapper);

                ArrayList<CompletableFuture<JsonNode>> futures = new ArrayList<>();
                for (FlightInfo info : result){
                    CompletableFuture<JsonNode> future = CompletableFuture
                            .supplyAsync(() -> {
                                try {
                                    HttpResponse<String> aircraftResponse = aRequest.getClient().send(
                                            HttpRequest.newBuilder()
                                                    .uri(URI.create(GET_AIRCRAFT_URL + info.plane.icao24))
                                                    .GET()
                                                    .build(), HttpResponse.BodyHandlers.ofString()
                                    );
                                    return mapper.readTree(aircraftResponse.body());
                                }
                                catch (IOException | InterruptedException e){
                                    System.err.println("Error: A network error occurred during stage 1 hexDb. " + e.getMessage());
                                    return null;
                                }
                            })
                            .thenApplyAsync(json -> {
                                try {
                                    String airlineIcao;
                                    if (json != null){
                                        info.airline = json.get("RegisteredOwners").asText();
                                        airlineIcao = json.get("OperatorFlagCode").asText();
                                    }
                                    else{
                                        return null;
                                    }

                                    HttpResponse<String> iataResponse = aRequest.getClient().send(
                                            HttpRequest.newBuilder()
                                                    .uri(URI.create(GET_IATA_URL + airlineIcao))
                                                    .GET()
                                                    .build(), HttpResponse.BodyHandlers.ofString()
                                    );
                                    if (iataResponse.statusCode() != SUCCESS){
                                        throw new IOException();
                                    }
                                    info.iataCode = iataResponse.body();
                                    return mapper.readTree(iataResponse.body());
                                }
                                catch (IOException | InterruptedException e){
                                    System.err.println("Error: A network error occurred during stage 2 hexDb. " + e.getMessage());
                                    return null;
                                }
                            })
                            .thenApplyAsync(json -> {
                                try {

                                    HttpResponse<String> routeResponse = aRequest.getClient().send(
                                            HttpRequest.newBuilder()
                                                    .uri(URI.create(GET_ROUTE_URL + info.iataCode + info.plane.callSign.trim().substring(3)))
                                                    .GET()
                                                    .build(), HttpResponse.BodyHandlers.ofString()
                                    );
                                    return mapper.readTree(routeResponse.body());
                                }
                                catch (IOException | InterruptedException e){
                                    System.err.println("Error: A network error occurred during stage 3 hexDb. " + e.getMessage());
                                    return null;
                                }
                            })
                            .thenApply(json -> {
                                String[] route;
                                if (json != null) {
                                    route = json.get("route").asText().split("-");
                                    info.origin = Database.airports.get(route[0]);
                                    info.dest = Database.airports.get(route[1]);
                                }
                                else {
                                    System.err.println("Error: A network error occurred during stage 4 hexDb.");
                                }
                                return null;
                            });
                    futures.add(future);
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

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

    public static void main(String[] args) throws InterruptedException {
        try{
            ArrayList<FlightInfo> a = FlightFetcher.fetchLiveFlightInfo();

            for (FlightInfo b : a){
                System.out.println(b.origin.iataCode);
            }
        } catch (RequestFailedException e) {
            System.err.println(e.getStatusCode());
        }
    }
}
