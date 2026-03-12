package com.still_processing.FlightData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.DefaultSettings.Settings.*;
import java.time.LocalDate;
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

    public static HttpResponse<String> liveData() { //daily flight statistics and routes
        String currentDay = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://aerodatabox.p.rapidapi.com/airports/iata/YYZ/stats/routes/daily/" + currentDay))
                .header("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
                .header("x-rapidapi-key", Settings.API_KEY)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException e) {
            System.out.println("IOException in liveData: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException in liveData: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }finally {
            client.close();
        }
    }

    // For Testing:
//    public static void main(String[] args){
//        historicalData(5, 3, 2025, 10, 3, 2025);
//        try{
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//        liveData();
//    }
}
