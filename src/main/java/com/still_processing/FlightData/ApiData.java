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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.still_processing.FlightData.CheckDate.convertToString;

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
     * Since API only allows maximum range of 12 hours, it gives current live flights with a range from current time and 12 hours previous.
     *
     * @author Marco Fontana && Jessica Chen
     * @param iataCode The IATA code of the airport for which to fetch live flight data
    */
//    API documentation:
    public static HttpResponse<String> extractLiveData(String iataCode) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twelveHoursAgo = now.minusHours(12);
        String endingCurrentDate = now.format(formatter);
        String startingCurrentDate = twelveHoursAgo.format(formatter);

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
//          System.out.println(extractLiveData("YYC").body());
    }
}