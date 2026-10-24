package com.budgettracker.entity;


// We use enum because It prevents invalid transaction types. we'll use an enum to restrict the allowed values.
//When you create an enum in Java, you're creating a custom data type with a fixed set of possible values.
//Here, TransactionType becomes a Java type, just like String or Integer are types.
public enum TransactionType {
    INCOME,
    EXPENSE
}
