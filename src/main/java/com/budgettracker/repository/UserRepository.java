package com.budgettracker.repository;

import com.budgettracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

        // to add a method that allows our application to find a user by email address. other methods are already included.
    Optional<User> findByEmail(String email);

        }
