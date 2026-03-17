package com.still_processing.FlightData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Requests.AuthenticatedRequest;
import com.still_processing.FlightData.Requests.RateLimitException;
import com.still_processing.FlightData.Requests.RequestFailedException;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Fetches OpenSky flight data
 *
 * @author IceryDev
 */
public class FlightFetcher {

    private static final String API_URL = "https://opensky-network.org/api/states/all";
    private static final String OAUTH_URL =
            "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token";
    private static final int MAX_RETRY = 3;

    private static AuthenticatedRequest aRequest;

    static ArrayList<FlightInfo> fetchLiveFlightInfo() throws RequestFailedException, InterruptedException {

        int retryCount = 0;

        ArrayList<FlightInfo> result = new ArrayList<>();
        CompletableFuture<HttpResponse<String>> response;

        if (aRequest == null){
            HttpClient client = HttpClient.newHttpClient();
            aRequest = new AuthenticatedRequest(client, OAUTH_URL, Settings.USER_NAME_OPENSKY, Settings.CLIENT_SECRET);
        }
        boolean processFinished = false;
        while (!processFinished) {
            try{
                response = aRequest.sendAsync(API_URL);

                HttpResponse<String> responseAsHttp = response.join();
                System.out.println(responseAsHttp.body());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseAsHttp.body());
                JsonNode states = root.get("states");

                for (JsonNode flight : states) {
                    String icao24       = flight.get(0).asText();
                    String callSign     = flight.get(1).asText().trim();
                    String country      = flight.get(2).asText();
                    long lastContact    = flight.get(4).asLong();
                    double longitude    = flight.get(5).asDouble();
                    double latitude     = flight.get(6).asDouble();
                    double altitude     = flight.get(7).asDouble();
                    boolean onGround    = flight.get(8).asBoolean();
                    double velocity     = flight.get(9).asDouble();
                    double heading      = flight.get(10).asDouble();
                    double verticalRate = flight.get(11).asDouble();
                    double geoAltitude  = flight.get(13).asDouble();
                    String squawk       = flight.get(14).isNull() ? null : flight.get(14).asText();
                }



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
                else {
                    processFinished = true;
                    e.printStackTrace();
                }
            }
            catch (JsonProcessingException e){
                System.err.println("Error: An error occurred while parsing JSON structure.");
            }
        }


        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        FlightFetcher f = new FlightFetcher();
        try{
            f.fetchLiveFlightInfo();
        } catch (RequestFailedException e) {
            System.err.println(e.getStatusCode());
        }
    }
}
