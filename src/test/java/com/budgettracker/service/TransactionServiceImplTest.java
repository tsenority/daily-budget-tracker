package com.budgettracker.service;

import com.budgettracker.dto.SummaryResponse;
import com.budgettracker.entity.TransactionType;
import com.budgettracker.entity.User;
import com.budgettracker.exception.TransactionNotFoundException;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import com.budgettracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setEmail("test@example.com");

        // Simulate an authenticated user
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "test@example.com",
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetSummary() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.sumAmountByType(TransactionType.INCOME, user))
                .thenReturn(new BigDecimal("5000"));

        when(transactionRepository.sumAmountByType(TransactionType.EXPENSE, user))
                .thenReturn(new BigDecimal("2000"));

        SummaryResponse result = transactionService.getSummary();

        assertEquals(new BigDecimal("5000"), result.getTotalIncome());
        assertEquals(new BigDecimal("2000"), result.getTotalExpenses());
        assertEquals(new BigDecimal("3000"), result.getBalance());

        verify(transactionRepository)
                .sumAmountByType(TransactionType.INCOME, user);

        verify(transactionRepository)
                .sumAmountByType(TransactionType.EXPENSE, user);
    }

    @Test
    void testGetSummaryWhenNoTransactions() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        // Unstubbed Mockito methods return null for BigDecimal.
        // This tests the service's null-to-zero handling.

        SummaryResponse result = transactionService.getSummary();

        assertNotNull(result);

        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpenses());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void testGetTransactionByIdNotFound() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByIdAndUser(100L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.getTransactionById(100L)
        );
    }
}