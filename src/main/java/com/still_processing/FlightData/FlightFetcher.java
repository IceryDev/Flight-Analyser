package com.still_processing.FlightData;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Requests.AuthenticatedRequest;
import com.still_processing.FlightData.Requests.RateLimitException;
import com.still_processing.FlightData.Requests.RequestFailedException;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Fetches OpenSky flight data
 *
 * @author IceryDev
 */
public class FlightFetcher {

    private static final String API_URL = "https://opensky-network.org/api/flights/all?";
    private static final int OPENSKY_HOURS_LIMIT = 1;
    private static final long EPOCH_HOUR = 3600;
    private static final long EPOCH_1_DAY = 86400;
    private static final int MAX_RETRY = 3;

    private static AuthenticatedRequest aRequest;

    static ArrayList<FlightInfo> fetchLiveFlightInfo() throws RequestFailedException, InterruptedException {

        long endEpoch = Instant.now().getEpochSecond();
        long startEpoch = endEpoch - (EPOCH_HOUR * OPENSKY_HOURS_LIMIT);
        int retryCount = 0;

        ArrayList<FlightInfo> result = new ArrayList<>();
        CompletableFuture<HttpResponse<String>> response;

        if (aRequest == null){
            HttpClient client = HttpClient.newHttpClient();
            aRequest = new AuthenticatedRequest(client, Settings.USER_NAME_OPENSKY, Settings.CLIENT_SECRET);
        }
        boolean processFinished = false;
        while (!processFinished) {
            try{
                response = aRequest.sendAsync(API_URL + "begin=" + startEpoch + "&end=" + endEpoch);

                HttpResponse<String> responseAsHttp = response.join();


                System.out.println(responseAsHttp.body());

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
