package com.example.lifetutor.config;

import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handlerException(IllegalArgumentException e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handlerException(MethodArgumentNotValidException e){
        Sentry.captureException(e); //validate exception
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(
                msg, HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handlerException(EntityNotFoundException e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MissingPathVariableException.class)
    public void handleMissingPathVariable(MissingPathVariableException e){
        Sentry.captureException(e);
    }
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<String> handlerException(TransactionSystemException e){
        Sentry.captureException(e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public void handleExceptions(Exception e){
        Sentry.captureException(e);
    }
}
