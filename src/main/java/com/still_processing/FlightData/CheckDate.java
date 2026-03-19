package com.still_processing.FlightData;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;

/**
 * Checks to see if the date is valid and returns a string.
 * @author Jessica Chen
 */
public class CheckDate {

    public static String convertToString(int date, int month, int year){
        String s = "";
        if (isValidDate(date, month, year)){
                s = s.concat(year + "-" );
            if (month < 10){
                s = s.concat("0" + month + "-");
            }
            else{
                s = s.concat(month + "-");
            }
            if (date < 10){
                s = s.concat("0" + date);
            }
            else{
                s = s.concat(String.valueOf(date));
            }
        }
        else{
            System.out.println("Date is not valid.");
        }
        return s;
    }

    public static boolean isLeapYear(int year){
        return ((year % 100 != 0) || (year % 400 == 0) && (year % 4 == 0));
    }

    public static boolean isValidDate(int date, int month, int year){
        int maxYear = Year.now().getValue() - 1;
        int minYear = Year.now().getValue() - 100;

        if (year > maxYear || year < minYear){
            return false;
        }
        if (month < 1 || month > 12){
            return false;
        }
        if (date < 1 || date > 31){
            return false;
        }
        if ( month == 2){
            if( isLeapYear(year)){
                return (date <= 29);
            }
            else{
                return (date <= 28);
            }
        }
        if ( month == 4 || month == 6 || month == 9 || month == 11){
            return (date <= 30);
        }
        return true;
    }

    /**
     * Takes a dd/mm/yyyy format string and a delimiter, and returns a {@code LocalDate} object.
     *
     * @param dateString The date string of format dd/mm/yyyy
     * @param delimiter The delimiter to split the date string (if above, it is "/")
     * @param mode Whether to work with dd/mm/yyyy (true) or mm/dd/yyyy (false) format
     * @return Corresponding {@code LocalDate} object
     * @author IceryDev (Ulaş İçer)
     */
    public static LocalDate getDateFromString(String dateString,
                                              String delimiter, boolean mode) {
        String[] values = dateString.split(delimiter);
        int[] dateTimeParams = new int[3];
        for (int i = 0; i < dateTimeParams.length; i++){
            dateTimeParams[i] = Integer.parseInt(values[i]);
        }
        if (!mode) { //Switch register contents if US format
            dateTimeParams[0] ^= dateTimeParams[1];
            dateTimeParams[1] ^= dateTimeParams[0];
            dateTimeParams[0] ^= dateTimeParams[1];
        }

        if (!isValidDate(dateTimeParams[0], dateTimeParams[1], dateTimeParams[2])) {
            System.err.printf("Date %s is not valid.\n", dateString);
        }

        return LocalDate.of(dateTimeParams[2], dateTimeParams[1], dateTimeParams[1]);
    }

    /**
     * Return Unix time (epoch seconds) of a given date.
     * @param date Date to be turned into Unix time
     * @return {@code long} epochSeconds
     * @author IceryDev (Ulaş İçer)
     */
    public static long getUnixTime(LocalDate date){
        Instant time = date.atStartOfDay().atZone(ZoneId.of("UTC")).toInstant();
        return time.getEpochSecond();
    }

    public static void main(String[] args) {
        System.out.println(getUnixTime(getDateFromString("12/12/2005", "/", true)));
    }
}
