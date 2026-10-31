package com.budgettracker.service;


import com.budgettracker.dto.SummaryResponse;
import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.entity.Category;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.TransactionType;
import com.budgettracker.exception.CategoryNotFoundException;
import com.budgettracker.exception.TransactionNotFoundException;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.stream;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository; //To Save, retrieve, update, and delete transactions
    private final CategoryRepository categoryRepository; // To Retrieve the category associated with a transaction

    public TransactionServiceImpl(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public Page<TransactionResponse> getAllTransactions(String sortBy,
                                                        String direction,
                                                        TransactionType type,
                                                        String keyword,
                                                        LocalDate startDate,
                                                        LocalDate endDate,
                                                        int page,
                                                        int size) {


        //SORTING
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);// Converts "ASC" or "DESC" into an enum

        Sort sort = Sort.by(sortDirection, sortBy); // Creates sorting instructions in sort variable

        Pageable pageable = PageRequest.of(page, size, sort);


        // FILTERING
        Page<Transaction> transactions; //DECLARES A TRANSACTIONS VARIABLE TO BE USED BELOW.

        // Add Date-Filtering Logic
        if (startDate != null && endDate != null) { // Checks if both dates are provided
            transactions = transactionRepository.findByDateBetween(startDate,endDate, pageable);

        }
        // search and filter
        else if (keyword != null && !keyword.isBlank() && type != null) {
            transactions = transactionRepository.findByDescriptionContainingIgnoreCaseAndType(keyword, type, pageable);

            // search only
        } else if (keyword != null && !keyword.isBlank()) {
            transactions = transactionRepository.findByDescriptionContainingIgnoreCase(keyword, pageable);

            // Filter by transaction type
        } else if (type != null) {
            transactions = transactionRepository.findByType(type, pageable); // Filter only

        } else {
            transactions = transactionRepository.findAll(pageable); //retrieves all
        }


        // Converts entities into response DTOs USING STREAM.
        return transactions.map(this::mapToResponse); //This converts each Transaction entity into a TransactionResponse DTO while preserving pagination information.
    }

    //private helper method to retrieve the Transaction entity
    private Transaction findTransactionById(Long id) {
        return transactionRepository.findById(id)

                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    // Method to return a Transaction entity, which we need for database operations such as updating and deleting transactions.
    //This retrieves the entity and converts it into a TransactionResponse for the frontend.
    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = findTransactionById(id); //using the entity retrieved from helper method above.

        TransactionResponse response = mapToResponse(transaction);
        return response;

    }


    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {

        Transaction transaction = mapToEntity(request);

        Transaction savedTransaction = transactionRepository.save(transaction);//saves to mysql

        TransactionResponse response = mapToResponse(savedTransaction);

        return response;
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction existingTransaction = findTransactionById(id);

        //updates all the fields.
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setType(request.getType());
        existingTransaction.setDate(request.getDate());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        existingTransaction.setCategory(category);


        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        TransactionResponse response = mapToResponse(savedTransaction);

        return response;

    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction existingTransaction = findTransactionById(id);

        transactionRepository.delete(existingTransaction);
    }

    // Method to Convert TransactionRequest to Transaction, Dto to Entity Mapping: request to Entity
    private Transaction mapToEntity(TransactionRequest request) {

        Transaction transaction = new Transaction();

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        transaction.setCategory(category); // Associates the Category with the Transaction

        return transaction; //whole transaction with all the fields including category name

    }

    // Method to Convert Transaction Entity → TransactionResponse. Reverse from the above method.
    private TransactionResponse mapToResponse(Transaction transaction) {

        TransactionResponse response = new TransactionResponse();

        response.setId(transaction.getId());
        response.setDescription(transaction.getDescription());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setDate(transaction.getDate());

        Category category = transaction.getCategory();

        if (category != null) { //checks whether the category exists, if no category, it will remove the Nullpointer exception.
            response.setCategoryId(category.getId());
            response.setCategoryName(category.getName());
        }


        return response;

    }

    @Override
    public SummaryResponse getSummary() {

        //Sum of all INCOME transactions
        BigDecimal totalIncome = transactionRepository.sumAmountByType(TransactionType.INCOME); // sumAmountByType() → Calculates the sum of transaction amounts

        // Sum of all EXPENSE transactions
        BigDecimal totalExpenses = transactionRepository.sumAmountByType(TransactionType.EXPENSE);

        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO; // This ensures totalIncome always contains a valid number(O). No Nullpointer Exception.
        }

        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }

        // Total income minus total Expenses
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return new SummaryResponse(totalIncome, totalExpenses, balance);

    }

}
