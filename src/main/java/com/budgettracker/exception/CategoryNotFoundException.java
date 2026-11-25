package com.budgettracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//To handle situations where a Category doesn't exist. We created a Custom exception
@ResponseStatus(HttpStatus.NOT_FOUND)
    public class CategoryNotFoundException extends RuntimeException{

        public CategoryNotFoundException(String message) { //Accepts a description of the error.
            super(message); //Passes that message to the parent RuntimeException class.
        }
    }

