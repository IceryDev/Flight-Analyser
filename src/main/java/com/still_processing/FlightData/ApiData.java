package com.still_processing.FlightData;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class ApiData {
    private static final String API_KEY = "91d4826d60msh41511ece48f601fp157718jsnf2a7f2abdfdf";
    public static void main(String[] args){
        historicalData();
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        liveData();
    }
    public static void historicalData(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://aerodatabox.p.rapidapi.com/flights/number/DL47/2025-01-01/2025-01-07?dateLocalRole=Both"))
                .header("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
                .header("x-rapidapi-key", API_KEY)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Live Data: " + response.body());
        } catch (IOException e) {
            System.out.println("IOException in historicalData: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("InterruptedException in historicalData: " + e.getMessage());
            Thread.currentThread().interrupt();
        }finally {
            client.close();
        }
    }
    public static void liveData() { //daily flight statistics and routes
        String currentDay = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://aerodatabox.p.rapidapi.com/airports/iata/YYZ/stats/routes/daily/" + currentDay))
                .header("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
                .header("x-rapidapi-key", API_KEY)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Live Data: " + response.body());
        } catch (IOException e) {
            System.out.println("IOException in liveData: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("InterruptedException in liveData: " + e.getMessage());
            Thread.currentThread().interrupt();
        }finally {
            client.close();
        }
    }
}
