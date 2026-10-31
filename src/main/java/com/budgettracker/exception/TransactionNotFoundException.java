package com.budgettracker.exception;


//To handle situations where a transaction doesn't exist. We created a custom exception
//Instead of a generic 500 Internal Server Error. We want 404 Not Found error message.
public class TransactionNotFoundException extends RuntimeException{

    public TransactionNotFoundException(String message) { //Accepts a description of the error.
        super(message); //Passes that message to the parent RuntimeException class.
    }
}
