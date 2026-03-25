package com.still_processing.FlightData;

import java.util.Arrays;
import java.lang.Math;

/**
 * @author Jagoda Koczwara-Szuba
 */

public class Statistics {

    public static float arithmeticMean(int[] values) {
        return ((float) Arrays.stream(values).sum())/values.length;
    }

    public static float arithmeticMean(float[] values) {
        float sum = 0;
        for (float value : values)
            sum += value;
        return sum/values.length;
    }

    public static float median(int[] values) {
        Arrays.sort(values);
        if (values.length % 2 == 0)
            return ((float) (values[values.length/2-1] + values[(values.length/2)]))/2;
        else
            return values[values.length/2];
    }

    public static float median(float[] values) {
        Arrays.sort(values);
        if (values.length % 2 == 0)
            return (values[values.length/2-1] + values[(values.length/2)])/2;
        else
            return values[values.length/2];
    }

    public static float variance(int[] values) {
        float mean = arithmeticMean(values);
        float sum = 0;
        for (int value : values)
            sum += (float) Math.pow(value-mean, 2);
        return sum/values.length;
    }

    public static float variance(float[] values) {
        float mean = arithmeticMean(values);
        float sum = 0;
        for (float value : values)
            sum += (float) Math.pow(value-mean, 2);
        return sum/values.length;
    }

    public static float standardDeviation(int[] values) {
        return (float) Math.sqrt(variance(values));
    }

    public static float standardDeviation(float[] values) {
        return (float) Math.sqrt(variance(values));
    }
}
