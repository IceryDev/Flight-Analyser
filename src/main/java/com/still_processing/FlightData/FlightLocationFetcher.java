package com.still_processing.FlightData;

import java.net.http.HttpClient;
import java.time.Instant;

/**
 * Fetches OpenSky flight data
 *
 * @author IceryDev
 */
public class FlightLocationFetcher {

    private final String TOKEN_ADDRESS = "https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token";
    private final int TOKEN_REFRESH_MARGIN = 30;

    static void fetchInstantLocations(){
        long unixTime = Instant.now().getEpochSecond();

        try(HttpClient client = HttpClient.newHttpClient()){
            //WIP
        }
    }

    static String getToken(){
        return null;
    }
}
