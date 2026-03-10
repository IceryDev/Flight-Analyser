package com.still_processing.flight_data;


import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CSVHandler {

    static final String CSV_DELIMITER = ",";
    static final String AIRPORT_FILE_PATH = "/airports.csv";

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
                String[] args = line.split(",(?=[\",\\d-]|$)(?<=[\",\\d-])");

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

                Database.airports.put(tmp.iataCode, tmp);
            }
            Database.airports.remove(null);
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
