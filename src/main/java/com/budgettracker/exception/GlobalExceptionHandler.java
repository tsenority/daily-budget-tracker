package com.budgettracker.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Allows one class to handle exceptions from multiple controllers.
public class GlobalExceptionHandler {
    // For Handling Validation Error Message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //represents 400 Bad Request
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
            return errors;

    }
   //For Handing Transaction Not Found error Message
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //to display 404 Not Found
    public Map<String, String> handleTransactionNotFound(TransactionNotFoundException ex) {

        return Map.of("error", ex.getMessage());
        //Think of a Map as a dictionary: Key → Value . New concept used in java 9 instead of HashMap
    }

    //For Handing Category Not Found error Message
    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //to display 404 Not Found
    public Map<String, String> handleCategoryNotFound(CategoryNotFoundException ex) {
        return Map.of("error", ex.getMessage());
             }

    }