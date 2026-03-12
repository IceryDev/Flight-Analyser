package com.still_processing.FlightData;
import java.time.Year;
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
}
