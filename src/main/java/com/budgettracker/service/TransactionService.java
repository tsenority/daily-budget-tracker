package com.budgettracker.service;

import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getAllTransactions();

    TransactionResponse getTransactionById(Long id);

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse updateTransaction(Long id, TransactionRequest request);

    void deleteTransaction(Long id);


}
