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

    static final int MIN_IN_HRS = 60;
    static final String TIME_DELIMITER = ":";

    /**
     * Takes the airport.csv file, extracts all data, and stores it in a hashmap
     * whose keys are the IATA codes, and the values are Airport objects.
     *
     * @author
     *         IceryDev (Ulaş İçer)
     */
    public static void loadAirportCSV() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        CSVHandler.class.getResourceAsStream(AIRPORT_FILE_PATH))))) {

            String[] headerArgs = reader.readLine()
                    .replaceAll("\"", "")
                    .split(CSV_DELIMITER);
            String line;
            while ((line = reader.readLine()) != null) {
                // Split commas between {"}, {,}, {any digit}, and {-} to get individual data.
                String[] args = line.split(CSV_SPLIT_REGEX);
                String tempIcao = "";

                Airport tmp = new Airport();
                for (int i = 0; i < Math.min(args.length, 19); i++) {
                    args[i] = args[i].replaceAll("\"", "");
                    if (args[i] == null || args[i].isEmpty()) {
                        continue;
                    }

                    switch (headerArgs[i]) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the offline flights .csv file, extracts all data, and stores it in an
     * {@code ArrayList}
     * of {@code FlightInfo} objects.
     *
     * @author
     *         IceryDev (Ulaş İçer)
     */
    public static void loadOfflineFlightCSV() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        CSVHandler.class.getResourceAsStream(OFFLINE_FLIGHT_FILE_PATH))))) {

            String[] tempArray = reader.readLine()
                    .replaceAll("\"", "")
                    .split(CSV_DELIMITER);

            ArrayList<String> headerArgs = new ArrayList<>(Arrays.asList(tempArray));

            headerArgs.add(4, "ORIGIN_CITY_NAME");
            headerArgs.add(9, "DEST_CITY_NAME");

            String line;
            while ((line = reader.readLine()) != null) {

                String[] args = line.split(CSV_DELIMITER);
                Airport originTmp = new Airport();
                Airport destTmp = new Airport();
                FlightInfo tmp = new FlightInfo();
                for (int i = 0; i < Math.min(args.length, headerArgs.size()); i++) {
                    args[i] = args[i].trim();
                    if (args[i].startsWith("\"")) {
                        continue;
                    } else if (args[i].endsWith("\"")) {
                        args[i] = args[i - 1] + ", " + args[i];
                    }

                    switch (headerArgs.get(i)) {
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
                            originTmp.iataCode = args[i];
                            break;
                        case "DEST":
                            destTmp.iataCode = args[i];
                            break;
                        case "CRS_DEP_TIME":
                            tmp.CRSDepTime = formatTimeString(args[i]);
                            break;
                        case "DEP_TIME":
                            tmp.depTime = formatTimeString(args[i]);
                            break;
                        case "CRS_ARR_TIME":
                            tmp.CRSArrTime = formatTimeString(args[i]);
                            break;
                        case "ARR_TIME":
                            tmp.arrTime = formatTimeString(args[i]);
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

                tmp.lateness = (tmp.cancelled) ? 0
                        : formattedTimeToMinutes(tmp.depTime) - formattedTimeToMinutes(tmp.CRSDepTime);

                tmp.origin = Database.airports.get(originTmp.iataCode);
                tmp.dest = Database.airports.get(destTmp.iataCode);
                Database.offlineFlights.add(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns how many minutes from start of the day has elapsed until the
     * given time string of format "00:00"
     * 
     * @param time Formatted time string as above.
     * @return Equivalent value in minutes from the start of the day.
     */
    private static int formattedTimeToMinutes(String time) {
        if (time.length() < 4) {
            return 0;
        }

        int mins = Integer.parseInt(time.substring(time.length() - 2));
        int hrs = Integer.parseInt(time.substring(0, time.length() - 3));

        return mins + (hrs * MIN_IN_HRS);
    }

    /**
     * Takes in a non-formatted time string as "1234" and formats it as "12:34"
     * Warning! Assumes the time is a valid time of the day.
     * 
     * @param time The non-formatted string.
     * @return Formatted string.
     */
    private static String formatTimeString(String time) {
        if (time.isEmpty() || !time.replaceAll("\\d", "").isEmpty()) {
            return "00:00";
        }

        if (time.length() != 4) {
            time = "0".repeat(4 - time.length()) + time;
        }
        time = String.format("%02d:%02d", Integer.parseInt(time.substring(0, time.length() - 2)),
                Integer.parseInt(time.substring(time.length() - 2)));
        return time;
    }

    // For testing
    public static void main(String[] args) {
        // loadOfflineFlightCSV();
        //
        // for (FlightInfo a : Database.offlineFlights){
        // System.out.println(a.lateness);
        // }
    }
}
