package com.budgettracker.repository;

import com.budgettracker.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //its optional to use
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
