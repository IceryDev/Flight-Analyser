package com.still_processing.FlightData.Requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * An {@code AuthenticatedRequest} object will wrap around the {@code HttpRequest}
 * to handle the "Bearer" type authentication.
 * <p>
 * Example use:
 * {@snippet :
 * AuthenticatedRequest aRequest = new AuthenticatedRequest(HttpClient.newHttpClient(), API_KEY);
 * //Synchronous Request
 * HttpResponse<String> response = aRequest.send(REQUEST_URL);
 * //Asynchronous Request
 * CompletableFuture<HttpResponse<String>> response = aRequest.sendAsync(REQUEST_URL);
 * }
 *
 * @author Ulaş İçer
 *
 */
public class AuthenticatedRequest {

    private static final int RATE_LIMIT_CODE = 429;
    private static final int SUCCESS = 200;
    private static final int DEFAULT_DELAY = 30;

    private final String clientSecret;
    private final String clientId;
    private final HttpClient client;

    /**
     * The {@code AuthenticatedRequest} constructor.
     * @param client The corresponding {@code HttpClient} to send request from.
     * @param key The API key to be authenticated.
     *
     * @author Ulaş İçer
     */
    public AuthenticatedRequest(HttpClient client, String clientId, String key){
        this.clientSecret = key;
        this.clientId = clientId;
        this.client = client;


    }

    /**
     * Wraps around HttpResponse.send() method to append the authentication.
     * @param url The corresponding URL to send the request to.
     * @return Response as a String
     * @throws IOException Default possible exception from the .send() method.
     * @throws InterruptedException Default possible exception from the .send() method.
     * @throws RateLimitException Thrown when the API limit is exceeded.
     *
     * @author Ulaş İçer
     */
    public HttpResponse<String> send(String url) throws IOException, InterruptedException, RateLimitException {
        HttpResponse<String> response = client.send(this.buildRequest(url), HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == RATE_LIMIT_CODE){
            int delay;
            try{
                delay = Integer.parseInt(response.headers()
                        .firstValue("X-Rate-Limit-Retry-After-Seconds")
                        .orElse("" + DEFAULT_DELAY));
            }
            catch (NumberFormatException e){
                delay = DEFAULT_DELAY;
            }

            throw new RateLimitException("RateLimitException: API rate limit exceeded.", delay, RATE_LIMIT_CODE);
        }
        else if (response.statusCode() != SUCCESS){
            throw new RateLimitException(
                    "RateLimitException: An error occurred during request.", DEFAULT_DELAY, response.statusCode());
        }
        return response;
    }

    /**
     * Wraps around HttpResponse.sendAsync() method to append the authentication.
     * @param url The corresponding URL to send the request to.
     * @return Response as a String
     *
     * @author Ulaş İçer
     */
    public CompletableFuture<HttpResponse<String>> sendAsync(String url){

        return client.sendAsync(this.buildRequest(url), HttpResponse.BodyHandlers.ofString())
                     .thenApply(response -> {
                         if (response.statusCode() == RATE_LIMIT_CODE){
                             int delay;
                             try{
                                 delay = Integer.parseInt(response.headers()
                                         .firstValue("X-Rate-Limit-Retry-After-Seconds")
                                         .orElse("" + DEFAULT_DELAY));
                             }
                             catch (NumberFormatException e){
                                 delay = DEFAULT_DELAY;
                             }

                             throw new RateLimitException(
                                     "RateLimitException: API rate limit exceeded.", delay, RATE_LIMIT_CODE);
                         }
                         else if (response.statusCode() != SUCCESS){
                             throw new RateLimitException(
                                     "RateLimitException: An error occurred during request.", DEFAULT_DELAY, response.statusCode());
                         }
                         else return response;
                     });
    }

    /**
     * A helper method for reusability in both send() and sendAsync() methods.
     * @param url The corresponding URL to send the request to.
     * @return The corresponding HttpRequest given the authentication.
     *
     * @author Ulaş İçer
     */
    private HttpRequest buildRequest(String url){
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + clientSecret)
                .GET().build();
    }
}
