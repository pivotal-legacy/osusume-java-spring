package com.tokyo.beach.restutils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(RestControllerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResourceError resourceNotFound(RestControllerException e) {
        return new ResourceError(e.getErrorMessage());
    }

}
