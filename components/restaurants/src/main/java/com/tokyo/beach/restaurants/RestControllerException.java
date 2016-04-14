package com.tokyo.beach.restaurants;

public class RestControllerException extends RuntimeException {

    private String errorMessage;

    public RestControllerException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @SuppressWarnings("unused")
    public String getErrorMessage() {
        return errorMessage;
    }
}
