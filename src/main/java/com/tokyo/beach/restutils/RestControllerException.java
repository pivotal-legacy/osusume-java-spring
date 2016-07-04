package com.tokyo.beach.restutils;

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
