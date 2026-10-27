package com.budgettracker.service;


import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.entity.Category;
import com.budgettracker.entity.Transaction;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository; //To Save, retrieve, update, and delete transactions
    private final CategoryRepository categoryRepository; // To Retrieve the category associated with a transaction

    public TransactionServiceImpl(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }




    @Override
    public List<TransactionResponse> getAllTransactions() {
        // returns a list of transactions--- return transactionRepository.findAll();

        //To convert each entity into a DTO, we can use JAVA STREAMS
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //private helper method to retrieve the Transaction entity
     private Transaction findTransactionById(Long id) {
        return transactionRepository.findById(id)

                .orElseThrow(() -> new RuntimeException("Transaction Not found"));
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

        TransactionResponse response= mapToResponse(savedTransaction);

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
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

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
    private Transaction mapToEntity(TransactionRequest request){

        Transaction transaction = new Transaction();

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category Not Found"));

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

}
