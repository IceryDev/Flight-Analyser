package com.still_processing.FlightData;

import java.util.Arrays;
import java.lang.Math;

/**
 * @author Jagoda Koczwara-Szuba
 */

public class Statistics {

    public static double arithmeticMean(int[] values) {
        return ((double) Arrays.stream(values).sum())/values.length;
    }

    public static double arithmeticMean(double[] values) {
        return Arrays.stream(values).sum()/values.length;
    }

    public static double median(int[] values) {
        Arrays.sort(values);
        if (values.length % 2 == 0)
            return (double) ((values[values.length/2-1] + values[(values.length/2)])/2);
        else
            return values[values.length/2];
    }

    public static double median(double[] values) {
        Arrays.sort(values);
        if (values.length % 2 == 0)
            return (double) ((values[values.length/2-1] + values[(values.length/2)])/2);
        else
            return values[values.length/2];
    }

    public static double variance(int[] values) {
        double mean = arithmeticMean(values);
        double sum = 0;
        for (int value : values)
            sum += Math.pow(value-mean, 2);
        return sum/values.length;
    }

    public static double variance(double[] values) {
        double mean = arithmeticMean(values);
        double sum = 0;
        for (double value : values)
            sum += Math.pow(value-mean, 2);
        return sum/values.length;
    }

    public static double standardDeviation(int[] values) {
        return Math.sqrt(variance(values));
    }

    public static double standardDeviation(double[] values) {
        return Math.sqrt(variance(values));
    }
}
