package com.budgettracker.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true) // To enforce a unique constraint on the email column. No duplicate email address.
    private String email;


    @JsonIgnore
    private String password;

    // Field for Role ENUM and its getter and setter method
    @Enumerated(EnumType.STRING) // Stores USER or ADMIN as text in MySQL
    @Column(nullable = false) // Prevents the database column from containing NULL.
    private Role role = Role.USER; // 	Sets USER as the default role for new Java objects

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // A No-argument constructor.
    public User() { // a default

    }

    // A Parameterized constructor
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

 // GETTER AND SETTER METHODS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
