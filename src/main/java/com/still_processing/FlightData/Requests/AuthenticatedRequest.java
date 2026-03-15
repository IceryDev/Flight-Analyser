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

    private final String apiKey;

    private final HttpClient client;

    /**
     * The {@code AuthenticatedRequest} constructor.
     * @param client The corresponding {@code HttpClient} to send request from.
     * @param key The API key to be authenticated.
     *
     * @author Ulaş İçer
     */
    public AuthenticatedRequest(HttpClient client, String key){
        this.apiKey = key;
        this.client = client;
    }

    /**
     * Wraps around HttpResponse.send() method to append the authentication.
     * @param url The corresponding URL to send the request to.
     * @return Response as a String
     * @throws IOException Default possible exception from the .send() method.
     * @throws InterruptedException Default possible exception from the .send() method.
     *
     * @author Ulaş İçer
     */
    public HttpResponse<String> send(String url) throws IOException, InterruptedException {

        return client.send(this.buildRequest(url), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Wraps around HttpResponse.sendAsync() method to append the authentication.
     * @param url The corresponding URL to send the request to.
     * @return Response as a String
     *
     * @author Ulaş İçer
     */
    public CompletableFuture<HttpResponse<String>> sendAsync(String url){

        return client.sendAsync(this.buildRequest(url), HttpResponse.BodyHandlers.ofString());
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
                .header("Authorization", "Bearer " + apiKey)
                .GET().build();
    }
}
