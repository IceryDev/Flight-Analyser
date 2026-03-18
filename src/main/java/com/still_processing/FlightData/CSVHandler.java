package com.still_processing.FlightData;


import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
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
                String tempIcao = "";

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
                        case "icao_code":
                            tempIcao = args[i];
                            break;
                        default:
                            break;
                    }
                }
                tmp.id = Database.airports.size();
                Database.airports.put(tmp.iataCode, tmp);
                if (!tempIcao.isEmpty() && tmp.iataCode != null && !tmp.iataCode.isEmpty()) {
                    Database.airportIcaoToIata.put(tempIcao, tmp.iataCode);
                }
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

            String[] tempArray = reader.readLine()
                                       .replaceAll("\"", "")
                                       .split(CSV_DELIMITER);

            ArrayList<String> headerArgs = new ArrayList<>(Arrays.asList(tempArray));

            headerArgs.add(4, "ORIGIN_CITY_NAME");
            headerArgs.add(9, "DEST_CITY_NAME");

            String line;
            while ((line = reader.readLine()) != null){

                String[] args = line.split(CSV_DELIMITER);
                Airport originTmp = new Airport();
                Airport destTmp = new Airport();
                FlightInfo tmp = new FlightInfo();
                for (int i = 0; i < Math.min(args.length, headerArgs.size()); i++) {
                    args[i] = args[i].trim();
                    if (args[i].startsWith("\"")){
                        continue;
                    }
                    else if(args[i].endsWith("\"")){
                        args[i] = args[i-1] + ", " + args[i];
                    }

                    switch(headerArgs.get(i)){
                        case "FL_DATE":
                            tmp.flightDate = args[i];
                            break;
                        case "MKT_CARRIER":
                            tmp.iataCode = args[i];
                            break;
                        case "MKT_CARRIER_FL_NUM":
                            tmp.flightNumber = args[i];
                            break;
                        case "ORIGIN":
                            tmp.originAirport = args[i];
                            originTmp.iataCode = args[i];
                            break;
                        case "ORIGIN_CITY_NAME":
                            originTmp.municipality = args[i];
                            break;
                        case "ORIGIN_STATE_ABR":
                            originTmp.region = args[i];
                            break;
                        case "DEST":
                            destTmp.iataCode = args[i];
                            break;
                        case "DEST_CITY_NAME":
                            destTmp.municipality = args[i];
                            break;
                        case "DEST_STATE_ABR":
                            destTmp.region = args[i];
                            break;
                        case "CRS_DEP_TIME":
                            tmp.CRSDepTime = args[i];
                            break;
                        case "DEP_TIME":
                            tmp.depTime = args[i];
                            break;
                        case "CRS_ARR_TIME":
                            tmp.CRSArrTime = args[i];
                            break;
                        case "ARR_TIME":
                            tmp.arrTime = args[i];
                            break;
                        case "CANCELLED":
                            tmp.cancelled = (args[i].equals("1"));
                            break;
                        case "DIVERTED":
                            tmp.diverted = (args[i].equals("1"));
                            break;
                        case "DISTANCE":
                            tmp.distance = Float.parseFloat(args[i]);
                            break;
                        default:
                            break;

                    }
                }

                tmp.origin = originTmp;
                tmp.dest = destTmp;
                Database.offlineFlights.add(tmp);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // For testing
    public static void main(String[] args){
//        loadOfflineFlightCSV();
//
//        for (FlightInfo a : Database.offlineFlights){
//            System.out.println(a.dest.iataCode);
//        }
    }
}
