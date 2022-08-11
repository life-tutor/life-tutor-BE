package com.example.lifetutor.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handlerException(IllegalArgumentException e){
        String msg = e.getMessage();
        return new ResponseEntity<>(
                msg, HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handlerException(MethodArgumentNotValidException e){
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(
                msg, HttpStatus.BAD_REQUEST
        );
    }
}
