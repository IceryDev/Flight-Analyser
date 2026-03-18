package com.still_processing.FlightData.Requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * An {@code AuthenticatedRequest} object will wrap around the {@code HttpRequest}
 * to handle OAuth2 and additionally the "Bearer" type of authentication.
 *
 * @author Ulaş İçer
 *
 */
public class AuthenticatedRequest {

    private static final int RATE_LIMIT_CODE = 429;
    private static final int SUCCESS = 200;
    private static final int DEFAULT_DELAY = 30;
    private static final int TOKEN_REFRESH = 250;

    private final String clientSecret;
    private final String clientId;
    private final HttpClient client;
    private final String oAuth2Url;

    private String token;
    private long lastRetrieved;

    /**
     * The {@code AuthenticatedRequest} constructor.
     * @param client The corresponding {@code HttpClient} to send request from.
     * @param key The API key to be authenticated.
     *
     * @author Ulaş İçer
     */
    public AuthenticatedRequest(HttpClient client, String oAuth2Url, String clientId, String key){
        this.clientSecret = key;
        this.clientId = clientId;
        this.client = client;
        this.lastRetrieved = -1;
        this.oAuth2Url = oAuth2Url;

        this.retrieveToken();
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
    public HttpResponse<String> send(String url) throws IOException, InterruptedException, RateLimitException, RequestFailedException {
        if (this.checkTokenUpdate()){
            retrieveToken();
        }
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
        if (this.checkTokenUpdate()){
            retrieveToken();
        }

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
                .header("Authorization", "Bearer " + token)
                .GET().build();
    }

    /**
     * Retrieves the OAuth2 token required by OpenSky API.
     * @throws RequestFailedException Thrown if credentials are incorrect or does not exist.
     * @author Ulaş İçer
     */
    private void retrieveToken() throws RequestFailedException{
        String postData = "grant_type=client_credentials" + "&client_id=" +
                URLEncoder.encode(clientId, StandardCharsets.UTF_8) + "&client_secret=" +
                URLEncoder.encode(clientSecret, StandardCharsets.UTF_8);
        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(oAuth2Url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(postData))
                .build();

        try {
            HttpResponse<String> response =  client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != SUCCESS){
                throw new RequestFailedException("Authentication failed! Make sure you have your correct client Id and secret!",
                        30, response.statusCode());
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.body());

            this.token = json.get("access_token").asText();
        }
        catch (IOException | InterruptedException e){
            throw new RequestFailedException("Network error during authentication", 30, 0);
        }

    }

    /**
     * Determines whether the OAuth2 token needs refreshing.
     * @return true/false -> refresh/no refresh
     * @author Ulaş İçer
     */
    private boolean checkTokenUpdate(){
        long current = Instant.now().getEpochSecond();
        if (current - lastRetrieved >= TOKEN_REFRESH){
            lastRetrieved = current;
            return true;
        }
        return false;
    }

    public HttpClient getClient(){
        return this.client;
    }
}
