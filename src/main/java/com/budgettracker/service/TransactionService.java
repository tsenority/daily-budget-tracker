package com.budgettracker.service;

import com.budgettracker.dto.SummaryResponse;
import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.TransactionType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    // A Page contains the transactions plus useful information such as size,number, content etc.
    Page<TransactionResponse> getAllTransactions(String sortBy, //For Sorting field
                                                 String direction, //for Sorting ASC or DESC
                                                 TransactionType type, //for filtering by Income or Expense
                                                 String keyword, // For Searching keywords
                                                 LocalDate startDate,
                                                 LocalDate endDate, // For Date Filtering
                                                 int page, //for page numbers(pagination)
                                                 int size); //// for maximum transactions per page


    TransactionResponse getTransactionById(Long id);

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse updateTransaction(Long id, TransactionRequest request);

    void deleteTransaction(Long id);

    SummaryResponse getSummary();


}
