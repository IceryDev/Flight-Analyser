package com.still_processing.FlightData.Filters;

public interface SimpleFilter<T, K> {

    T filter(T value, K... params);
}
