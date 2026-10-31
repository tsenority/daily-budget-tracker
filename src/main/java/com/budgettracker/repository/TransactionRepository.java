package com.budgettracker.repository;

import com.budgettracker.entity.Transaction;

import com.budgettracker.entity.TransactionType;
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

@Repository //its optional to use
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //Unlike List<Transaction>, a Page<Transaction> contains both transaction records and pagination.
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
    //This new method allows MySQL to filter, sort, and retrieve a specific page of transactions
    //Remember, Pageable already contains the sorting instructions, so we no longer need a separate Sort parameter.

    Page<Transaction> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Transaction> findByDescriptionContainingIgnoreCaseAndType(String keyword,TransactionType type, Pageable pageable);
    // Spring Data JPA recognizes ContainingIgnoreCase in the repository method name and automatically generates a database query for searching.

    //For Summary Calculation
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") TransactionType type);
    // @Query needs the actual JPQL statement that tells Spring Data JPA what calculation to perform.

    // For Date-Range Filtering, Returns matching transactions along with pagination information.
    Page<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);


}
