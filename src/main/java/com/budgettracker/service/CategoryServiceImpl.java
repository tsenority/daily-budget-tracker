package com.budgettracker.service;


import com.budgettracker.entity.Category;
import com.budgettracker.entity.User;
import com.budgettracker.exception.CategoryNotFoundException;
import com.budgettracker.repository.CategoryRepository;

import com.budgettracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;


    public CategoryServiceImpl(CategoryRepository categoryRepository,UserRepository userRepository ) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();


        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

       return categoryRepository.findByUser(user);

    }

    @Override
    public Category getCategoryById(Long id){

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();


        // Retrieve the User Entity
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));
    }

    @Override
    public Category createCategory(Category category) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        category.setUser(user);

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) { // category parameter because we are assigning the update value to category.
        Category existingCategory = getCategoryById(id);
        //calls the getCategoryById method from above.
        //In Java, you can call a method and assign its returned value directly to a variable. Variable Initialization.
        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory( Long id) {
        Category existingCategory = getCategoryById(id);
        categoryRepository.delete(existingCategory);

    }
}
