package com.budgettracker.repository;


import com.budgettracker.entity.Category;
import com.budgettracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Spring Data JPA automatically generates a query that retrieves categories belonging to the specified user.
    List<Category> findByUser(User user);

    // Method to Retrieve Category by ID and User
    Optional<Category> findByIdAndUser(Long id, User user);
}
