package com.sunbaseassignment.util;

/**
 * Interface defining common HTTP status codes for API responses.
 *
 * @author Nikhil Kumar
 */
public interface StatusCode {
    // Successful responses (200–299)
    int OK = 200; // Successful HTTP request.
    int CREATED = 201; // New resource created.
    int ACCEPTED = 202; // Request accepted for processing.
    int NO_CONTENT = 204; // Server processed the request successfully, no content returned.

    // Redirection messages (300–399)
    int MOVED_PERMANENTLY = 301; // Resource permanently moved.
    int FOUND = 302; // Resource temporarily found under a different URL.
    int SEE_OTHER = 303; // Response found under a different URI.
    int NOT_MODIFIED = 304; // Resource not modified since last request.
    int TEMPORARY_REDIRECT = 307; // Resource temporarily found under a different URL.
    int PERMANENT_REDIRECT = 308; // Resource permanently found under a different URL.

    // Client error responses (400–499)
    int BAD_REQUEST = 400; // Server cannot process the request due to client error.
    int UNAUTHORIZED = 401; // Request lacks valid authentication credentials.
    int FORBIDDEN = 403; // Server refuses to authorize the request.
    int NOT_FOUND = 404; // Requested resource not found.
    int METHOD_NOT_ALLOWED = 405; // Method not allowed for the resource.
    int CONFLICT = 409; // Request could not be completed due to resource conflict.
    int GONE = 410; // Requested resource no longer available.
    int UNSUPPORTED_MEDIA_TYPE = 415; // Server refuses request due to unsupported media type.
    int TOO_MANY_REQUESTS = 429; // Too many requests within a given time frame.

    // Server error responses (500–599)
    int INTERNAL_SERVER_ERROR = 500; // Unexpected condition encountered on the server.
    int NOT_IMPLEMENTED = 501; // Server does not support functionality required to fulfill the request.
    int BAD_GATEWAY = 502; // Server received invalid response from upstream server.
    int SERVICE_UNAVAILABLE = 503; // Server currently unable to handle request due to temporary overload or maintenance.
    int GATEWAY_TIMEOUT = 504; // Server did not receive timely response from upstream server.
}