package com.budgettracker.controller;


import com.budgettracker.dto.SummaryResponse;
import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.TransactionType;
import com.budgettracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public Page<TransactionResponse> getAllTransactions(
            @RequestParam(defaultValue ="amount") String sortBy, //Field to sort by; defaults to amount
            @RequestParam(defaultValue = "DESC") String direction, // Sorting direction; defaults to DESC
            @RequestParam(required = false) TransactionType type,//// Optional filter: INCOME or EXPENSE
            @RequestParam(required = false) String keyword, // for search functionality
            @RequestParam(required = false) LocalDate startDate, // for dat filtering
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page, // retrieves first page as a default. users can change it later.
            @RequestParam(defaultValue = "5") int size) { //up to 5 transactions, default size, can be changed.

        return transactionService.getAllTransactions(sortBy, direction,type, keyword,startDate, endDate, page, size);

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


    // Summary API Endpoint for React and Postman
    @GetMapping("/summary")
    public SummaryResponse getSummary() {
        return transactionService.getSummary();
    }



}
