package com.still_processing.FlightData;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.Requests.AuthenticatedRequest;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Fetches OpenSky flight data
 *
 * @author IceryDev
 */
public class FlightFetcher {

    private static final String API_URL = "https://opensky-network.org/api/flights/all?";


    static ArrayList<FlightInfo> fetchFlightInfoByDate(LocalDate start, LocalDate end){
        HttpClient client = HttpClient.newHttpClient();
        AuthenticatedRequest aRequest = new AuthenticatedRequest(client, Settings.CLIENT_SECRET);

        try{
            //WIP
        }
        catch (Exception e){

        }

        return null;
    }
}
