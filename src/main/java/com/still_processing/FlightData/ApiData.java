package com.still_processing.FlightData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.still_processing.DefaultSettings.Settings;
import com.still_processing.DefaultSettings.Settings.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.still_processing.FlightData.CheckDate.convertToString;

/**
 * Makes HTTP GET requests to the AeroDataBox API to fetch flight information.
 * @author Jessica Chen
 */
public class ApiData {


    public static HttpResponse<String> historicalData(int startDate, int startMonth, int startYear, int endDate, int endMonth, int endYear){
        String startData = convertToString(startDate, startMonth, startYear);
        String endData = convertToString(endDate, endMonth, endYear);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://aerodatabox.p.rapidapi.com/flights/number/DL47/" +  startData + "/" + endData + "?dateLocalRole=Both"))
                .header("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
                .header("x-rapidapi-key", Settings.API_KEY)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response;
        } catch (IOException e) {
            System.out.println("IOException in historicalData: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException in historicalData: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }finally {
            client.close();
        }
    }

    /**
     * Extract current live flights data for a given airport IATA code.
     *
     * @author Marco Fontana
     * @param iataCode The IATA code of the airport for which to fetch live flight data
    */
    public static HttpResponse<String> extractLiveData(String iataCode) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");
        String startingCurrentDate = LocalDateTime.now().format(formatter);
        // Todo: Data range handling needs to be fixed
        String endingCurrentDate = startingCurrentDate + "23:59";
        startingCurrentDate = startingCurrentDate + "00:00";
        URI uri = URI.create("https://aerodatabox.p.rapidapi.com/flights/airports/Iata/" +  iataCode + "/" + startingCurrentDate + "/" + endingCurrentDate);
        System.out.println(uri.getPath());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
                .header("x-rapidapi-key", Settings.API_KEY)
                .GET()
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // Testing
        System.out.println(extractLiveData("YYC").body());
    }
}