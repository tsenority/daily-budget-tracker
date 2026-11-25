package com.budgettracker.repository;

import com.budgettracker.entity.Transaction;

import com.budgettracker.entity.TransactionType;
import com.budgettracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository //its optional to use
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //Unlike List<Transaction>, a Page<Transaction> contains both transaction records and pagination.
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
    //This new method allows MySQL to filter, sort, and retrieve a specific page of transactions
    //Remember, Pageable already contains the sorting instructions, so we no longer need a separate Sort parameter.

    // For Authenticated User only
    Page<Transaction> findByUserAndType(User user, TransactionType type, Pageable pageable);

    Page<Transaction> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);

    //For Authenticated User only
    Page<Transaction> findByUserAndDescriptionContainingIgnoreCase(User user, String keyword, Pageable pageable);

    Page<Transaction> findByDescriptionContainingIgnoreCaseAndType(String keyword,TransactionType type, Pageable pageable);
    // Spring Data JPA recognizes ContainingIgnoreCase in the repository method name and automatically generates a database query for searching.

    // Only transactions belonging to the specified user.
    Page<Transaction> findByUserAndDescriptionContainingIgnoreCaseAndType(User user, String keyword,TransactionType type, Pageable pageable);

    //For Summary Calculation
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type And t.user = :user")
    BigDecimal sumAmountByType(@Param("type") TransactionType type, @Param("user") User user);
    // @Query needs the actual JPQL statement that tells Spring Data JPA what calculation to perform.

    // For Date-Range Filtering, Returns matching transactions along with pagination information.
    Page<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // For specified User
    Page<Transaction> findByUserAndDateBetween(User user,LocalDate startDate, LocalDate endDate, Pageable pageable);

    // A method that retrieves transactions belonging to one specific user.
    List<Transaction> findByUser(User user); // Example: transactionRepository.findByUser(john);

    // It retrieves transactions belonging to a specific user while supporting pagination and sorting.
    Page<Transaction> findByUser(User user, Pageable pageable);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    // To calculate how much the logged-in user spent in each category.
    @Query("""
    SELECT t.category.name, SUM(t.amount)
    FROM Transaction t
    WHERE t.user = :user
    AND t.type = :type
    GROUP BY t.category.name
    """)
    List<Object[]> findExpensesByCategory(
            @Param("user") User user,
            @Param("type") TransactionType type
    );



}
