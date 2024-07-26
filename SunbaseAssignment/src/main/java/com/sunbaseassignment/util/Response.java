package com.sunbaseassignment.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Response<T> {
    private T data;
    private String message;
    private int status;
    private String jwtToken;

    public Response(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public Response(T data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public Response(String message, int status, String jwtToken) {
        this.message = message;
        this.status = status;
        this.jwtToken = jwtToken;
    }
}