package com.still_processing.Application.Filters;

public interface SimpleFilter<T, K> {

    T filter(T value, K... params);
}
