package com.budgettracker.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description; // e.g, gas, groceries , salary etc.

    private BigDecimal amount; // We use BigDecimal instead of Double because financial calculations require accurate decimal arithmetic.

    @Enumerated(EnumType.STRING) //tells Hibernate to store the enum's name as text in MySQL. such as Income and Expense.
    private TransactionType type;

    private LocalDate date; // LocalDate represents a calendar date without a time.

    @ManyToOne // Many transactions can refer to one same category.
    @JoinColumn(name = "category_id") //foreign key in the transaction table that reference Category Table.
    private Category category; // A custom class or enum can be used as a data type in different class.Relationship between objects.
                                //This custom class is called Reference Type OR User Defined Data Type.
    //Category=Reference Type, category=Reference Variable

    //No Argument Constructor : Java automatically provides it when no constructors are declared.
    //We explicitly write it when we want to preserve it alongside other constructors.
    public Transaction() {
                 }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}


