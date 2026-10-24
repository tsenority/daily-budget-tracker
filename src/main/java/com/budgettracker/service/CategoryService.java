package com.budgettracker.service;

import com.budgettracker.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories(); //Interface doesnt have a method body. so, no curly braces

    Category getCategoryById(Long id); //Category:object we are returning

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);

}