package com.budgettracker.integration;


import com.budgettracker.entity.Category;
import com.budgettracker.entity.Transaction;
import com.budgettracker.entity.TransactionType;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;
import com.budgettracker.entity.User;
import com.budgettracker.repository.UserRepository;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;



@SpringBootTest // Tells Spring Boot to load the application context for integration testing.
@ActiveProfiles("test") // Activates our test configuration.(application-test.properties)
@AutoConfigureMockMvc// MockMvc allows us to simulate HTTP requests to our Spring Boot REST endpoints without postman.
@WithMockUser(username = "test@example.com")
public class TransactionIntegrationTest {

    @Test // contextLoads() ---checks whether Spring Boot can initialize its components
    void contextLoads() {
    }

    @Autowired // Injects MockMvc Using @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository; // This injects the real Spring Data JPA repository, connected to our H2 test database.

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Test
    void testGetAllTransactions() throws Exception { // Allows us to execute MockMvc methods without handling checked exceptions manually.

        mockMvc.perform(get("/api/transactions")
                        .with(user("test@example.com").roles("USER")))// Executes a simulated HTTP request. Using all real MVC code except h2 database.

        .andExpect(status().isOk()) // Testing 200 OK.
                    // $ : The root of JSON response. Need a resultMatcher import, not the requestMatchers.
                .andExpect(jsonPath("$.totalElements").value(0));// To check whether JSON field totalElements equals 0.

    }

    @Test
    void testGetAllTransactionsWithData() throws Exception {

        Transaction transaction = new Transaction();

        transaction.setDescription("Groceries");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setDate(LocalDate.of(2026, 10, 15));

        Category category = new Category();
        category.setName("Food");
        categoryRepository.save(category);

        transaction.setCategory(category); // Many to One Relationship.It connects our "Groceries" transaction to the "Food" category.

        transaction.setUser(testUser);


        transactionRepository.save(transaction); // Saves our transaction in H2 database.

        mockMvc.perform(get("/api/transactions")
                        .with(user("test@example.com").roles("USER")))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content[0].description").value("Groceries"))
                .andExpect(jsonPath("$.content[0].categoryName").value("Food"));
    }

    @BeforeEach
    void setUp() {

        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User(
                "Test User",
                "test@example.com",
                "testPassword123"
        );

        testUser = userRepository.save(testUser);
    }
    /*Why this matters: Each test starts with an empty database, so one test won't interfere with another.
     This is called test isolation, an important practice in professional software development.*/

    @Test // To verify that the API returns HTTP 404, when no transactionID found.
    void testGetTransactionByIdNotFound() throws Exception {

        mockMvc.perform(get("/api/transactions/999")
                        .with(user("test@example.com").roles("USER")))
                .andExpect(status().isNotFound());

    }

    @Test // To test Post transaction method
    void testCreateTransaction() throws Exception {

        Category category = new Category();
        category.setName("Food");
        category.setUser(testUser);
        categoryRepository.save(category);

        //Java Text block to create Json object as a Java String.
        String requestBody = """ 
                {
                "description" : "Groceries",
                "amount" : 100,
                "type" : "EXPENSE",
                "date" : "2026-10-15",
                "categoryId" : %d
                    }
                """.formatted(category.getId());

        mockMvc.perform(post("/api/transactions")
                        .with(user("test@example.com").roles("USER"))
                .contentType(APPLICATION_JSON)
                .content(requestBody))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.description").value("Groceries"));

                // To verify that the transaction was actually saved in H2.
                assertEquals(1L,
                        transactionRepository.count());

                    } // You've now verified that your Daily Budget Tracker can successfully create a transaction and save it to the H2 database.

    @Test // To Test Invalid Transaction Data
    void testCreateTransactionWithInvalidAmount() throws Exception {

        Category category = new Category();
        category.setName("Food");
        categoryRepository.save(category);

        String requestBody= """ 
                {
                "description" : "Groceries",
                "amount" : -100,
                "type" : "EXPENSE",
                "date" : "2026-10-15",
                "categoryId" : %d
                    }
                """.formatted(category.getId());

        mockMvc.perform(post("/api/transactions")
                        .with(user("test@example.com").roles("USER"))
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(status().isBadRequest());

        // To Verify That Invalid Data Was Not Saved.
        assertEquals(0L,
                transactionRepository.count());

    } // we have confirmed that Spring Validation rejects invalid data without saving it to the database.

            }
