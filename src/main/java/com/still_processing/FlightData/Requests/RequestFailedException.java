package com.still_processing.FlightData.Requests;

public class RequestFailedException extends RuntimeException {
    private final int trials;
    private final int statusCode;

    public RequestFailedException(String message, int trials, int statusCode) {
        super(message);
        this.trials = trials;
        this.statusCode = statusCode;
    }

    public int getTrials(){
        return this.trials;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
