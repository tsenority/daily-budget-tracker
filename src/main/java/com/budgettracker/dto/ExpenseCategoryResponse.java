package com.budgettracker.dto;

import java.math.BigDecimal;

public class ExpenseCategoryResponse {

        private String name;
        private BigDecimal amount;

        public ExpenseCategoryResponse(String name, BigDecimal amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }


