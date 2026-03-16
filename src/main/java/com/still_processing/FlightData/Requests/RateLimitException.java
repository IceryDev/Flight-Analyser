package com.still_processing.FlightData.Requests;

public class RateLimitException extends RuntimeException {
    private final int delay;
    private final int statusCode;

    public RateLimitException(String message, int delayInSeconds, int statusCode) {
        super(message);
        this.delay = delayInSeconds;
        this.statusCode = statusCode;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
