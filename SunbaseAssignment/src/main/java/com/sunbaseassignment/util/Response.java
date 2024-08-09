package com.sunbaseassignment.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A generic class for constructing API responses.
 *
 * @param <T> The type of data to be included in the response.
 * @author Nikhil Kumar
 */
@Getter
@Setter
@NoArgsConstructor
public class Response<T> {
    private T data;          // The data to be included in the response.
    private String message;  // A message to be included in the response.
    private int status;      // The HTTP status code for the response.
    private String jwtToken; // Optional JWT token for authentication.

    /**
     * Constructs a Response with a message and status.
     *
     * @param message The message to be included in the response.
     * @param status  The HTTP status code for the response.
     */
    public Response(String message, int status) {
        this.message = message;
        this.status = status;
    }

    /**
     * Constructs a Response with data, a message, and status.
     *
     * @param data    The data to be included in the response.
     * @param message The message to be included in the response.
     * @param status  The HTTP status code for the response.
     */
    public Response(T data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    /**
     * Constructs a Response with a message, status, and JWT token.
     *
     * @param message  The message to be included in the response.
     * @param status   The HTTP status code for the response.
     * @param jwtToken The JWT token for authentication.
     */
    public Response(String message, int status, String jwtToken) {
        this.message = message;
        this.status = status;
        this.jwtToken = jwtToken;
    }
}