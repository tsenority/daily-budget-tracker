package com.budgettracker.service;


import com.budgettracker.entity.Category;
import com.budgettracker.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();

    }

    @Override
    public Category getCategoryById(Long id){
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Override
    public Category createCategory(Category category) {
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
