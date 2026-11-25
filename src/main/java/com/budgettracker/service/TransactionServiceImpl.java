package com.budgettracker.service;


import com.budgettracker.dto.ExpenseCategoryResponse;
import com.budgettracker.dto.SummaryResponse;
import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.entity.Category;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.TransactionType;
import com.budgettracker.entity.User;
import com.budgettracker.exception.CategoryNotFoundException;
import com.budgettracker.exception.TransactionNotFoundException;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import com.budgettracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository; //To Save, retrieve, update, and delete transactions
    private final CategoryRepository categoryRepository; // To Retrieve the category associated with a transaction
    private final UserRepository userRepository; // To find the authenticated User.

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    // 	Creates or retrieves a logger. 	Identifies which class produces the logs. Logger provides methods such as info(), debug(), and error().
    // Comments explain the code. Logs record its execution. Debugging investigates problems.

    public TransactionServiceImpl(TransactionRepository transactionRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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

        // For Authenticated Users:  we need to retrieve that email when processing a transaction request.
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));



        //SORTING
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);// Converts "ASC" or "DESC" into an enum

        Sort sort = Sort.by(sortDirection, sortBy); // Creates sorting instructions in sort variable

        Pageable pageable = PageRequest.of(page, size, sort);


        // FILTERING
        Page<Transaction> transactions; //DECLARES A TRANSACTIONS VARIABLE TO BE USED BELOW.

        // Add Date-Filtering Logic
        if (startDate != null && endDate != null) { // Checks if both dates are provided
            transactions =  transactionRepository.findByUserAndDateBetween(user,startDate,endDate,pageable);

        }
        // search and filter
        else if (keyword != null && !keyword.isBlank() && type != null) {
            transactions = transactionRepository.findByUserAndDescriptionContainingIgnoreCaseAndType(user,keyword, type, pageable);

            // search only
        } else if (keyword != null && !keyword.isBlank()) {
            transactions = transactionRepository.findByUserAndDescriptionContainingIgnoreCase(user,keyword, pageable);

            // Filter by transaction type
        } else if (type != null) {
            transactions = transactionRepository.findByUserAndType(user,type, pageable); // Filter only

        } else {
            transactions = transactionRepository.findByUser(user,pageable); // Retrieves only the authenticated user's transactions, while preserving pagination and sorting.
        }


        // Converts entities into response DTOs USING STREAM.
        return transactions.map(this::mapToResponse); //This converts each Transaction entity into a TransactionResponse DTO while preserving pagination information.
    }

    //private helper method to retrieve the Transaction entity
    private Transaction findTransactionById(User user,Long id) {
        return transactionRepository.findByIdAndUser(id, user)

                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    // Method to return a Transaction entity, which we need for database operations such as updating and deleting transactions.
    //This retrieves the entity and converts it into a TransactionResponse for the frontend.
    @Override
    public TransactionResponse getTransactionById(Long id) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Transaction transaction = findTransactionById(user,id); //using the entity retrieved from helper method above.

        TransactionResponse response = mapToResponse(transaction);
        return response;

    }


    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {

        logger.info("Creating a new transaction");

        // Retrieve the authenticated user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Retrieve the User entity
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Authenticated user not found");
                    return new IllegalArgumentException("User not found");
                });


        Transaction transaction = mapToEntity(request, user);

        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);//saves to mysql

        TransactionResponse response = mapToResponse(savedTransaction);

        return response;
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {

        // Retrieve the authenticated email:
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Retrieve the User Entity
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Transaction existingTransaction = findTransactionById(user, id);

        //updates all the fields.
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setType(request.getType());
        existingTransaction.setDate(request.getDate());

        Category category = categoryRepository.findByIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        existingTransaction.setCategory(category);


        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        TransactionResponse response = mapToResponse(savedTransaction);

        return response;

    }

    @Override
    public void deleteTransaction(Long id) {
        // Retrieve the authenticated user's email.
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Retrieve the User Entity
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Transaction existingTransaction = findTransactionById(user, id);

        transactionRepository.delete(existingTransaction);
    }

    // Method to Convert TransactionRequest to Transaction, Dto to Entity Mapping: request to Entity
    private Transaction mapToEntity(TransactionRequest request, User user){

        Transaction transaction = new Transaction();

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());

        Category category = categoryRepository.findByIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        transaction.setCategory(category); // Associates the Category with the Transaction

        return transaction; // returning a Transaction entity, which contains a reference to the Category entity.

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
        // Retrieve the authenticated user's email
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Retrieve the User Entity
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));


        // Calculate user-specific income and expenses:
        BigDecimal totalIncome = transactionRepository.sumAmountByType(TransactionType.INCOME, user); // sumAmountByType() → Calculates the sum of transaction amounts

        // Sum of all EXPENSE transactions
        BigDecimal totalExpenses = transactionRepository.sumAmountByType(TransactionType.EXPENSE, user);

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

    // For getExpensesByCategory() Pie chart.
    @Override
    public List<ExpenseCategoryResponse> getExpensesByCategory() {

        // 1. Identify the logged-in user
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // 2. Find the user in MySQL
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Retrieve expense totals
        List<Object[]> results = transactionRepository.findExpensesByCategory(
                user,
                TransactionType.EXPENSE
        );

        // 4. Convert database results into DTOs
        return results.stream()
                .map(row -> new ExpenseCategoryResponse(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }



}
