package com.still_processing.FlightData.Requests;

/**
 * A {@code RateLimitException} is thrown when the API puts a limit to the number
 * of requests sent by the user.
 *
 * @author Ulaş İçer
 */
public class RateLimitException extends RuntimeException {
    private final int delay;
    private final int statusCode;

    /**
     * Constructor for the exception. Parameters are as follows:
     * @param message The exception message to be displayed.
     * @param delayInSeconds The delay amount before the next request attempt.
     * @param statusCode Http status code for the error.
     *
     * @author Ulaş İçer
     */
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
