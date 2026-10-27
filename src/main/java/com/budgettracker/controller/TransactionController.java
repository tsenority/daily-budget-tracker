package com.budgettracker.controller;


import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.entity.Transaction;
import com.budgettracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService; //decalres the service dependency

    //Constructor injects the ServiceImpl class.
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();

    }

    @GetMapping("/{id}")
    public TransactionResponse getTransactionById( @PathVariable Long id) {
        return transactionService.getTransactionById(id);

    }

    @PostMapping
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
        return transactionService.createTransaction(request);
        /* What happens now?

            Your POST request follows this flow:

            Postman → TransactionController → TransactionRequest → TransactionServiceImpl → Transaction Entity → MySQL → TransactionResponse → Postman

            Notice that the controller no longer receives or returns the Transaction entity directly.*/
    }

    @PutMapping("/{id}")
    public TransactionResponse updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return transactionService.updateTransaction(id, request);

    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
    }



}
