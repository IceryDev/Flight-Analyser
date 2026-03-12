package com.still_processing.FlightData;


import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CSVHandler {

    static final String CSV_DELIMITER = ",";
    static final String AIRPORT_FILE_PATH = "/airports.csv";
    static final String OFFLINE_FLIGHT_FILE_PATH = "/flights10k.csv";
    static final String CSV_SPLIT_REGEX = ",(?=[\",\\d-]|$)(?<=[\",\\d-])";

    /**
     * Takes the airport.csv file, extracts all data, and stores it in a hashmap
     * whose keys are the IATA codes, and the values are Airport objects.
     *
     * @author
     * IceryDev (Ulaş İçer)
     */
    public static void loadAirportCSV (){
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        CSVHandler.class.getResourceAsStream(AIRPORT_FILE_PATH))))){

            String[] headerArgs = reader.readLine()
                                        .replaceAll("\"", "")
                                        .split(CSV_DELIMITER);
            String line;
            while ((line = reader.readLine()) != null){
                // Split commas between {"}, {,}, {any digit}, and {-} to get individual data.
                String[] args = line.split(CSV_SPLIT_REGEX);

                Airport tmp = new Airport();
                for (int i = 0; i < Math.min(args.length, 19); i++){
                    args[i] = args[i].replaceAll("\"", "");
                    if (args[i] == null || args[i].isEmpty()) { continue; }

                    switch (headerArgs[i]){
                        case "name":
                            tmp.name = args[i];
                            break;
                        case "latitude_deg":
                            tmp.latitude = Float.parseFloat(args[i]);
                            break;
                        case "longitude_deg":
                            tmp.longitude = Float.parseFloat(args[i]);
                            break;
                        case "iso_country":
                            tmp.country = args[i];
                            break;
                        case "iso_region":
                            tmp.region = args[i];
                            break;
                        case "municipality":
                            tmp.municipality = args[i];
                            break;
                        case "iata_code":
                            tmp.iataCode = args[i];
                            break;
                        default:
                            break;
                    }
                }
                tmp.id = Database.airports.size();
                Database.airports.put(tmp.iataCode, tmp);
            }
            Database.airports.remove(null);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Takes the offline flights .csv file, extracts all data, and stores it in an {@code ArrayList}
     * of {@code FlightInfo} objects.
     *
     * @author
     * IceryDev (Ulaş İçer)
     */
    public static void loadOfflineFlightCSV (){
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        CSVHandler.class.getResourceAsStream(OFFLINE_FLIGHT_FILE_PATH))))){

            ArrayList<String> headerArgs = (ArrayList<String>) Arrays.stream(reader.readLine()
                    .replaceAll("\"", "")
                    .split(CSV_DELIMITER)).toList();

            headerArgs.add(4, "ORIGIN_CITY_NAME");
            headerArgs.add(8, "DEST_CITY_NAME");

            String line;
            while ((line = reader.readLine()) != null){

                String[] args = line.split(CSV_DELIMITER);

                FlightInfo tmp = new FlightInfo();
                for (int i = 0; i < Math.min(args.length, headerArgs.size()); i++) {
                    args[i] = args[i].trim();
                    if (args[i].startsWith("\"")){
                        continue;
                    }
                    else if(args[i].endsWith("\n")){
                        args[i] = args[i-1] + args[i];
                    }

                    switch(headerArgs.get(i)){
                        case "FL_DATE":
                            tmp.flightDate = args[i];
                            break;
                        case "MKT_CARRIER":
                            tmp.IATA_Code_Marketing_Airline = args[i];
                            break;
                        case "MKT_CARRIER_FL_NUM":
                            tmp.Flight_Number_Marketing_Airline = args[i];
                            break;
                        case "ORIGIN":
                            tmp.originAirport = args[i];
                            break;
                        default:
                            break;

                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // For testing
    public static void main(String[] args){
        loadAirportCSV();

        for (Airport a : Database.airports.values()){
        }
    }
}
