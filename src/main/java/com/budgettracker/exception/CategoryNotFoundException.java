package com.budgettracker.exception;


//To handle situations where a Category doesn't exist. We created a Custom exception
    public class CategoryNotFoundException extends RuntimeException{

        public CategoryNotFoundException(String message) { //Accepts a description of the error.
            super(message); //Passes that message to the parent RuntimeException class.
        }
    }

