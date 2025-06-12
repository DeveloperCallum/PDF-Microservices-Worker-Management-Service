package com.willcocks.callum.workermanagementservice.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        ex.printStackTrace(); // log full trace
        return ResponseEntity.status(500).body(new errorReporting(ex.getMessage(), ex));
    }

public record errorReporting(String message, Exception e){}
}
