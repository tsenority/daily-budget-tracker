package com.budgettracker.service;

import com.budgettracker.dto.LoginRequest;
import com.budgettracker.dto.RegisterRequest;

public interface AuthService {

    public void registerUser(RegisterRequest request);

    public String loginUser(LoginRequest request);
}
