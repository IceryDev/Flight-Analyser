package com.still_processing.FlightData.Requests;

/**
 * A {@code RequestFailedException} is thrown when there has been a fatal error during
 * an http request or the maximum number of trials have been reached, to stop sending
 * more requests.
 *
 * @author Ulaş İçer
 */
public class RequestFailedException extends RuntimeException {
    private final int trials;
    private final int statusCode;

    /**
     * Constructor for the exception, parameters are as follows:
     * @param message The exception message to be displayed.
     * @param trials The amount of http request attempts before the ultimate failure.
     * @param statusCode The status code of the http request.
     *
     * @author Ulaş İçer
     */
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
