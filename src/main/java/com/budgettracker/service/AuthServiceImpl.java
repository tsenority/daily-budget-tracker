package com.budgettracker.service;


import com.budgettracker.dto.LoginRequest;
import com.budgettracker.dto.RegisterRequest;
import com.budgettracker.entity.User;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.UserRepository;
import com.budgettracker.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import com.budgettracker.entity.Category;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository; // Declares a repository field

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService; // To connect my existing login functionality to JwtService.

    private final CategoryRepository categoryRepository;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, CategoryRepository categoryRepository ) { // Constructor receives the repository
        this.userRepository = userRepository;
        this.passwordEncoder= passwordEncoder; // Assigns the parameter to the class field
        this.jwtService = jwtService;
        this.categoryRepository = categoryRepository;
    }


    @Override
    @Transactional // It ensures registration is treated as one database operation. If saving a category fails, the transaction rolls back the user creation too.
    public void registerUser(RegisterRequest request) {

        // 1. Check whether email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // 2. Create a new user with an encrypted password
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        // 3. Save the user
        userRepository.save(user);

        // 4. Define default categories
        String[] defaultCategories = {
                "Salary",
                "Freelance",
                "Other Income",
                "Groceries",
                "Rent",
                "Transportation",
                "Entertainment",
                "Utilities",
                "Shopping",
                "Healthcare",
                "Other Expenses"
        };

        // 5. Create and save each category for this user
        for (String categoryName : defaultCategories) {

            Category category = new Category();

            category.setName(categoryName);
            category.setUser(user);

            categoryRepository.save(category);
        }
    }

    @Override
    public String loginUser(LoginRequest request) {

        // For Email address verification
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        // .orElseThrow() --- Returns the user or throws an exception.

        // For Password Verification
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
            // The if block executes when the password is incorrect. When the password is correct, Java skips the block and proceeds to generate the JWT.
        }
            return jwtService.generateToken(user.getEmail());


    }


}
