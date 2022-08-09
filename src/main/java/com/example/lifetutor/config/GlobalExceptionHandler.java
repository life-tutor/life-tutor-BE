package com.example.lifetutor.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handlerException(IllegalArgumentException e){
        String msg = e.getMessage();
        return new ResponseEntity<>(
                msg, HttpStatus.BAD_REQUEST
        );
    }
}
